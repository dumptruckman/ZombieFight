package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.database.SQLDatabase;
import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GamePlayer;
import com.dumptruckman.minecraft.zombiefight.api.PlayerType;
import com.dumptruckman.minecraft.zombiefight.api.StatsDatabase;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import org.bukkit.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Set;

class DefaultStatsDatabase implements StatsDatabase {
    private ZombieFight plugin;

    DefaultStatsDatabase(ZombieFight plugin) {
        this.plugin = plugin;
    }

    synchronized SQLDatabase getDB() {
        return plugin.getDB();
    }

    private void checkTables() {
        if (!getDB().isConnected()) {
            return;
        }
        if (!getDB().checkTable(QueryGen.PLAYER_TYPE_TABLE)) {
            if (!getDB().createTable(QueryGen.createPlayerTypeTable())) {
                Logging.severe("Could not create player type table!");
            }
        }
        for (PlayerType type : PlayerType.values()) {
            getDB().query(QueryGen.addPlayerType(type));
            ResultSet result = getDB().query(QueryGen.getPlayerTypeId(type));
            try {
                result.next();
                type.setId(result.getInt("id"));
            } catch (SQLException ignore) {
                Logging.warning("Could not set PlayerType id");
            }
        }
        if (!getDB().checkTable(QueryGen.PLAYERS_TABLE)) {
            if (!getDB().createTable(QueryGen.createPlayersTable())) {
                Logging.severe("Could not create players table!");
            }
        }
        if (!getDB().checkTable(QueryGen.GAMES_TABLE)) {
            if (!getDB().createTable(QueryGen.createGamesTable())) {
                Logging.severe("Could not create games table!");
            }
        }
        if (!getDB().checkTable(QueryGen.STATS_TABLE)) {
            if (!getDB().createTable(QueryGen.createStatsTable())) {
                Logging.severe("Could not create stats table!");
            }
        }
        if (!getDB().checkTable(QueryGen.TYPE_HISTORY_TABLE)) {
            if (!getDB().createTable(QueryGen.createTypeHistoryTable())) {
                Logging.severe("Could not create type history table!");
            }
        }
        if (!getDB().checkTable(QueryGen.KILLS_TABLE)) {
            if (!getDB().createTable(QueryGen.createKillsTable())) {
                Logging.severe("Could not create kills table!");
            }
        }
    }

    private void updateDB() {
        if (!getDB().isConnected()) {
            return;
        }
        //ResultSet table = getDB().query()
    }

    @Override
    public boolean connect() {
        if (getDB().isConnected()) {
            return false;
        }
        boolean ret = getDB().connect(plugin);
        if (ret) {
            this.checkTables();
            this.updateDB();
        }
        return ret;
    }

    @Override
    public int getPlayer(String player) {
        if (!getDB().isConnected()) {
            return -1;
        }
        ResultSet result = getDB().query(QueryGen.getPlayer(player));
        try {
            if (!result.next()) {
                getDB().query(QueryGen.updatePlayer(player, null, null));
                result = getDB().query(QueryGen.getPlayer(player));
                result.next();
            }
            return result.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int newGame(Timestamp createTime, World world) {
        if (!getDB().isConnected()) {
            return -1;
        }
        getDB().query(QueryGen.createGame(createTime, world.getName()));
        try {
            ResultSet result = getDB().query(QueryGen.getGame(createTime, world.getName()));
            result.next();
            return result.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void gameStarted(Game game, Timestamp startTime) {
        if (!getDB().isConnected()) {
            return;
        }
        if (game.getId() < 0) {
            return;
        }
        getDB().query(QueryGen.startGame(game.getId(), startTime));
        for (GamePlayer player : game.getGamePlayers()) {
            getDB().query(QueryGen.updatePlayer(player.getName(),
                    (player.getType().getId() >= 0 ? player.getType().getId() : null),
                    plugin.getPlayerKit(player.getName())));
            if (player.isOnline() && player.getId() > -1) {
                getDB().query(QueryGen.playerStartingInGame(player.getId(), game.getId(), player.isZombie(), plugin.getPlayerKit(player.getName())));
            }
        }
    }

    @Override
    public void playerJoinedGame(Game game, GamePlayer player) {
        if (!getDB().isConnected()) {
            return;
        }
        if (game.getId() < 0 || player.getId() < 0) {
            return;
        }
        getDB().query(QueryGen.playerJoiningInGame(game.getId(), player.getId(), player.isZombie(), plugin.getPlayerKit(player.getName())));
    }

    @Override
    public void gameEnded(Game game, Timestamp endTime) {
        if (!getDB().isConnected()) {
            return;
        }
        if (game.getId() < 0) {
            return;
        }
        Set<GamePlayer> gamePlayers = game.getGamePlayers();
        boolean humansWon = false;
        for (GamePlayer player : gamePlayers) {
            if (player.isOnline() && !player.isZombie()) {
                humansWon = true;
                break;
            }
        }
        getDB().query(QueryGen.endGame(game.getId(), endTime, humansWon));
        for (GamePlayer player : gamePlayers) {
            getDB().query(QueryGen.updatePlayer(player.getName(),
                    (player.getType().getId() >= 0 ? player.getType().getId() : null),
                    plugin.getPlayerKit(player.getName())));
            if (player.isOnline() && player.getId() > -1) {
                getDB().query(QueryGen.playerFinishingInGame(player.getId(), game.getId(), player.isZombie()));
            }
        }
    }

    @Override
    public void playerUpdate(GamePlayer player) {
        if (!getDB().isConnected()) {
            return;
        }
        getDB().query(QueryGen.updatePlayer(player.getName(),
                (player.getType().getId() >= 0 ? player.getType().getId() : null),
                plugin.getPlayerKit(player.getName())));
    }

    @Override
    public void playerKilled(GamePlayer killer, GamePlayer victim, Game game, Timestamp time, int weapon) {
        if (!getDB().isConnected()) {
            return;
        }
        getDB().query(QueryGen.playerKilled((killer != null ? killer.getId() : -1),
                (killer != null && killer.getType().getId() >= 0 ? killer.getType().getId() : null),
                victim.getId(), (victim.getType().getId() >= 0 ? victim.getType().getId() : null),
                game.getId(), time, weapon));
    }

    @Override
    public void playerTypeChange(Game game, GamePlayer player, PlayerType type) {
        if (!getDB().isConnected()) {
            return;
        }
        if (game.getId() < 0 || player.getId() < 0 || type.getId() < 0) {
            return;
        }
        getDB().query(QueryGen.playerTypeChange(player.getId(), game.getId(), type.getId()));
    }
}
