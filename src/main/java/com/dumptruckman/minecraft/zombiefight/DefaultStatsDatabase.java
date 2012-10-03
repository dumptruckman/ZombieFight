/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.database.SQLDatabase;
import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GamePlayer;
import com.dumptruckman.minecraft.zombiefight.api.PlayerType;
import com.dumptruckman.minecraft.zombiefight.api.StatsDatabase;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.SQLRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class DefaultStatsDatabase implements StatsDatabase {

    private final static class SQLQueueThread extends Thread {

        private final BlockingQueue<SQLRunnable> queryQueue = new LinkedBlockingQueue<SQLRunnable>();

        SQLQueueThread() {
            super("ZF_SQLQueueThread");
        }

        @Override
        public void run() {
            while (true) {
                try {
                    final SQLRunnable query = queryQueue.take();
                    try {
                        query.run();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException ignore) { }
            }
        }

        public void queueQuery(SQLRunnable query) {
            queryQueue.add(query);
        }
    }

    private final ZombieFight plugin;
    private final SQLQueueThread queueThread;

    DefaultStatsDatabase(ZombieFight plugin) {
        this.plugin = plugin;
        if (plugin.config().get(ZFConfig.TRACK_STATS)) {
            if (checkTables()) {
                queueThread = new SQLQueueThread();
                queueThread.start();
            } else {
                queueThread = null;
            }
        } else {
            queueThread = null;
        }
    }

    SQLDatabase getDB() {
        return plugin.getDB();
    }

    private boolean checkTables() {
        try {
            if (!getDB().checkTable(QueryGen.PLAYER_TYPE_TABLE)) {
                getDB().getFreshPreparedStatementHotFromTheOven(QueryGen.createPlayerTypeTable()).executeUpdate();
            }
            final PreparedStatement setTypeStatement = getDB().getFreshPreparedStatementWithGeneratedKeys(QueryGen.addPlayerType());
            final PreparedStatement retrieveTypeStatement = getDB().getFreshPreparedStatementHotFromTheOven(QueryGen.getPlayerTypeId());
            for (PlayerType type : PlayerType.values()) {
                setTypeStatement.setString(1, type.name());
                setTypeStatement.executeUpdate();
                ResultSet resultSet = setTypeStatement.getGeneratedKeys();
                if (!resultSet.next()) {
                    retrieveTypeStatement.setString(1, type.name());
                    resultSet = retrieveTypeStatement.executeQuery();
                    resultSet.next();
                }
                type.setId(resultSet.getInt(1));
            }
            if (!getDB().checkTable(QueryGen.PLAYERS_TABLE)) {
                getDB().getFreshPreparedStatementHotFromTheOven(QueryGen.createPlayersTable()).executeUpdate();
            }
            if (!getDB().checkTable(QueryGen.GAMES_TABLE)) {
                getDB().getFreshPreparedStatementHotFromTheOven(QueryGen.createGamesTable()).executeUpdate();
            }
            if (!getDB().checkTable(QueryGen.STATS_TABLE)) {
                getDB().getFreshPreparedStatementHotFromTheOven(QueryGen.createStatsTable()).executeUpdate();
            }
            if (!getDB().checkTable(QueryGen.TYPE_HISTORY_TABLE)) {
                getDB().getFreshPreparedStatementHotFromTheOven(QueryGen.createTypeHistoryTable()).executeUpdate();
            }
            if (!getDB().checkTable(QueryGen.KILLS_TABLE)) {
                getDB().getFreshPreparedStatementHotFromTheOven(QueryGen.createKillsTable()).executeUpdate();
            }
            getDB().getFreshPreparedStatementHotFromTheOven(QueryGen.createGrandTotalKillsView()).executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Logging.warning("Database could not be initialized!  ZombieFight will not track stats.");
            return false;
        }
    }

    private void queueQuery(SQLRunnable query) {
        if (queueThread != null) {
            queueThread.queueQuery(query);
        }
    }

    @Override
    public void setupPlayer(final GamePlayer player) {
        queueQuery(new PlayerInitializer(plugin, player));
    }

    private static class PlayerInitializer extends SQLRunnable {
        private final GamePlayer player;
        private PlayerInitializer(final ZombieFight plugin, final GamePlayer player) {
            super(plugin);
            this.player = player;
        }
        @Override
        public void run() throws SQLException {
            PreparedStatement preparedStatement = getDB().getFreshPreparedStatementHotFromTheOven(QueryGen.getPlayer());
            preparedStatement.setString(1, player.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                preparedStatement = getDB().getFreshPreparedStatementWithGeneratedKeys(QueryGen.createPlayer());
                preparedStatement.setString(1, player.getName());
                preparedStatement.executeUpdate();
                resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();
            }
            player.getDBInfo().setId(resultSet.getInt(1));
        }
    }

    @Override
    public void newGame(final Game game) {
        queueQuery(new GameInitializer(plugin, game));
    }

    private static class GameInitializer extends SQLRunnable {
        private final Game game;
        private final Timestamp createTime;
        private final String world;
        private GameInitializer(final ZombieFight plugin, final Game game) {
            super(plugin);
            this.game = game;
            this.createTime = new Timestamp(System.currentTimeMillis());
            this.world = game.getWorld().getName();
        }
        @Override
        public void run() throws SQLException {
            final PreparedStatement preparedStatement = getDB().getFreshPreparedStatementWithGeneratedKeys(QueryGen.createGame());
            preparedStatement.setString(1, world);
            preparedStatement.setTimestamp(2, createTime);
            preparedStatement.executeUpdate();
            final ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            game.getDBInfo().setId(resultSet.getInt(1));
        }
    }

    @Override
    public void gameReset(final Game game) {
        queueQuery(new GameReseter(plugin, game));
    }

    private static class GameReseter extends SQLRunnable {
        private final Game game;
        private GameReseter(final ZombieFight plugin, final Game game) {
            super(plugin);
            this.game = game;
        }
        @Override
        public void run() throws SQLException {
            game.getDBInfo().setId(-1);
        }
    }

    @Override
    public void gameStarted(final Game game) {
        queueQuery(new GameStarter(plugin, game));
        for (GamePlayer player : game.getGamePlayers()) {
            queueQuery(new PlayerUpdater(plugin, player));
            if (player.isOnline()) {
                queueQuery(new PlayerGameStarter(plugin, player, game));
            }
        }
    }

    private static class GameStarter extends SQLRunnable {
        private final Game game;
        private final Timestamp startTime;
        private GameStarter(final ZombieFight plugin, final Game game) {
            super(plugin);
            this.game = game;
            this.startTime = new Timestamp(System.currentTimeMillis());
        }
        @Override
        public void run() throws SQLException {
            final PreparedStatement preparedStatement = getDB().getFreshPreparedStatementHotFromTheOven(QueryGen.startGame());
            preparedStatement.setTimestamp(1, startTime);
            preparedStatement.setInt(2, game.getDBInfo().getId());
            preparedStatement.executeUpdate();
        }
    }

    private static class PlayerGameStarter extends SQLRunnable {
        private final GamePlayer player;
        private final Game game;
        private final int isZombie;
        private final String kit;
        private PlayerGameStarter(final ZombieFight plugin, final GamePlayer player, final Game game) {
            super(plugin);
            this.player = player;
            this.game = game;
            this.isZombie = player.isZombie() ? 1 : 0;
            final String tempKit = plugin.getPlayerKit(player.getName());
            if (tempKit == null) {
                this.kit = "";
            } else {
                this.kit = tempKit;
            }
        }
        @Override
        public void run() throws SQLException {
            final PreparedStatement preparedStatement = getDB().getFreshPreparedStatementHotFromTheOven(QueryGen.playerStartingInGame());
            preparedStatement.setInt(1, player.getDBInfo().getId());
            preparedStatement.setInt(2, game.getDBInfo().getId());
            preparedStatement.setInt(3, 1);
            preparedStatement.setInt(4, isZombie);
            preparedStatement.setInt(5, isZombie);
            preparedStatement.setString(6, kit);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void playerJoinedGame(final Game game, final GamePlayer player) {
        queueQuery(new PlayerGameJoiner(plugin, player, game));
    }

    private static class PlayerGameJoiner extends SQLRunnable {
        private final GamePlayer player;
        private final Game game;
        private final int isZombie;
        private final String kit;
        private PlayerGameJoiner(final ZombieFight plugin, final GamePlayer player, final Game game) {
            super(plugin);
            this.player = player;
            this.game = game;
            this.isZombie = player.isZombie() ? 1 : 0;
            final String tempKit = plugin.getPlayerKit(player.getName());
            if (tempKit == null) {
                this.kit = "";
            } else {
                this.kit = tempKit;
            }
        }
        @Override
        public void run() throws SQLException {
            final PreparedStatement preparedStatement = getDB().getFreshPreparedStatementHotFromTheOven(QueryGen.playerJoiningInGame());
            preparedStatement.setInt(1, player.getDBInfo().getId());
            preparedStatement.setInt(2, game.getDBInfo().getId());
            preparedStatement.setInt(3, 1);
            preparedStatement.setInt(4, isZombie);
            preparedStatement.setString(5, kit);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void gameEnded(final Game game) {
        final Set<GamePlayer> gamePlayers = game.getGamePlayers();
        boolean humansWon = false;
        for (GamePlayer player : gamePlayers) {
            if (player.isOnline() && !player.isZombie()) {
                humansWon = true;
                break;
            }
        }
        queueQuery(new GameEnder(plugin, game, humansWon));
        for (GamePlayer player : gamePlayers) {
            queueQuery(new PlayerUpdater(plugin, player));
            if (player.isOnline()) {
                queueQuery(new PlayerGameFinisher(plugin, player, game));
            }
        }
    }

    private static class GameEnder extends SQLRunnable {
        private final Game game;
        private final Timestamp endTime;
        private final int humansWon;
        private GameEnder(final ZombieFight plugin, final Game game, final boolean humansWon) {
            super(plugin);
            this.game = game;
            this.endTime = new Timestamp(System.currentTimeMillis());
            this.humansWon = humansWon ? 1 : 0;
        }
        @Override
        public void run() throws SQLException {
            final PreparedStatement preparedStatement = getDB().getFreshPreparedStatementHotFromTheOven(QueryGen.endGame());
            preparedStatement.setTimestamp(1, endTime);
            preparedStatement.setInt(2, humansWon);
            preparedStatement.setInt(3, game.getDBInfo().getId());
            preparedStatement.executeUpdate();
        }
    }

    private static class PlayerGameFinisher extends SQLRunnable {
        private final GamePlayer player;
        private final Game game;
        private final int isZombie;
        private PlayerGameFinisher(final ZombieFight plugin, final GamePlayer player, final Game game) {
            super(plugin);
            this.player = player;
            this.game = game;
            this.isZombie = player.isZombie() ? 1 : 0;
        }
        @Override
        public void run() throws SQLException {
            final PreparedStatement preparedStatement = getDB().getFreshPreparedStatementHotFromTheOven(QueryGen.playerFinishingInGame());
            preparedStatement.setInt(1, 1);
            preparedStatement.setInt(2, isZombie);
            preparedStatement.setInt(3, player.getDBInfo().getId());
            preparedStatement.setInt(4, game.getDBInfo().getId());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void playerUpdate(GamePlayer player) {
        if (queueThread == null) {
            return;
        }
        queueThread.queueQuery(new PlayerUpdater(plugin, player));
    }

    private static class PlayerUpdater extends SQLRunnable {
        private final GamePlayer player;
        private final String kit;
        private PlayerUpdater(final ZombieFight plugin, final GamePlayer player) {
            super(plugin);
            this.player = player;
            final String tempKit = plugin.getPlayerKit(player.getName());
            if (tempKit == null) {
                this.kit = "";
            } else {
                this.kit = tempKit;
            }
        }
        @Override
        public void run() throws SQLException {
            final PreparedStatement preparedStatement = getDB().getFreshPreparedStatementHotFromTheOven(QueryGen.updatePlayer());
            preparedStatement.setString(1, player.getName());
            preparedStatement.setInt(2, player.getType().getId());
            preparedStatement.setString(3, kit);
            preparedStatement.setInt(4, player.getDBInfo().getId());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void playerKilled(final GamePlayer killer, final GamePlayer victim, final Game game, final int weapon) {
        if (queueThread == null) {
            return;
        }
        queueQuery(new PlayerKilled(plugin, killer, victim, game, weapon));
    }

    private static class PlayerKilled extends SQLRunnable {
        private final GamePlayer killer;
        private final PlayerType killerType;
        private final GamePlayer victim;
        private final PlayerType victimType;
        private final Game game;
        private final Timestamp time;
        private final int weapon;
        private PlayerKilled(final ZombieFight plugin, final GamePlayer killer, final GamePlayer victim, final Game game, final int weapon) {
            super(plugin);
            this.killer = killer;
            if (killer != null) {
                killerType = killer.getType();
            } else {
                killerType = null;
            }
            this.victim = victim;
            victimType = victim.getType();
            this.game = game;
            this.time = new Timestamp(System.currentTimeMillis());
            this.weapon = weapon;
        }
        @Override
        public void run() throws SQLException {
            final PreparedStatement preparedStatement = getDB().getFreshPreparedStatementHotFromTheOven(QueryGen.playerKilled());
            if (killer != null) {
                preparedStatement.setInt(1, killer.getDBInfo().getId());
            } else {
                preparedStatement.setNull(1, Types.INTEGER);
            }
            if (killerType != null) {
                preparedStatement.setInt(2, killerType.getId());
            } else {
                preparedStatement.setNull(2, Types.INTEGER);
            }
            preparedStatement.setInt(3, victim.getDBInfo().getId());
            preparedStatement.setInt(4, victimType.getId());
            preparedStatement.setInt(5, game.getDBInfo().getId());
            preparedStatement.setTimestamp(6, time);
            preparedStatement.setInt(7, weapon);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void playerTypeChange(final Game game, final GamePlayer player, final PlayerType type) {
        if (queueThread == null) {
            return;
        }
        queueQuery(new PlayerTypeChange(plugin, player, game, type));
    }

    private static class PlayerTypeChange extends SQLRunnable {
        private final GamePlayer player;
        private final Game game;
        private final PlayerType type;
        private final Timestamp time;
        private PlayerTypeChange(final ZombieFight plugin, final GamePlayer player, final Game game, final PlayerType type) {
            super(plugin);
            this.player = player;
            this.game = game;
            this.type = type;
            this.time = new Timestamp(System.currentTimeMillis());
        }
        @Override
        public void run() throws SQLException {
            final PreparedStatement preparedStatement = getDB().getFreshPreparedStatementHotFromTheOven(QueryGen.playerTypeChange());
            preparedStatement.setInt(1, player.getDBInfo().getId());
            preparedStatement.setInt(2, game.getDBInfo().getId());
            preparedStatement.setInt(3, type.getId());
            preparedStatement.setTimestamp(4, time);
            preparedStatement.executeUpdate();
        }
    }
}
