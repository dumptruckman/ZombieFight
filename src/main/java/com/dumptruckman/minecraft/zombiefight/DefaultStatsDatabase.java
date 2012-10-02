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
import org.bukkit.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Set;

class DefaultStatsDatabase implements StatsDatabase {

    private ZombieFight plugin;
    private boolean isTracking = false;

    DefaultStatsDatabase(ZombieFight plugin) {
        this.plugin = plugin;
        this.isTracking = plugin.config().get(ZFConfig.TRACK_STATS);
    }

    synchronized SQLDatabase getDB() {
        return plugin.getDB();
    }

    private void checkTables() {
        try {
            if (!getDB().checkTable(QueryGen.PLAYER_TYPE_TABLE)) {
                getDB().execute(QueryGen.createPlayerTypeTable());
            }
            for (PlayerType type : PlayerType.values()) {
                getDB().execute(QueryGen.addPlayerType(type));
                ResultSet result = getDB().executeQueryNow(QueryGen.getPlayerTypeId(type));
                try {
                    result.next();
                    type.setId(result.getInt("id"));
                } catch (SQLException ignore) {
                    Logging.warning("Could not set PlayerType id");
                }
            }
            if (!getDB().checkTable(QueryGen.PLAYERS_TABLE)) {
                getDB().execute(QueryGen.createPlayersTable());
            }
            if (!getDB().checkTable(QueryGen.GAMES_TABLE)) {
                getDB().execute(QueryGen.createGamesTable());
            }
            if (!getDB().checkTable(QueryGen.STATS_TABLE)) {
                getDB().execute(QueryGen.createStatsTable());
            }
            if (!getDB().checkTable(QueryGen.TYPE_HISTORY_TABLE)) {
                getDB().execute(QueryGen.createTypeHistoryTable());
            }
            if (!getDB().checkTable(QueryGen.KILLS_TABLE)) {
                getDB().execute(QueryGen.createKillsTable());
            }
            getDB().execute(QueryGen.createGrandTotalKillsView());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPlayer(String player) {
        if (!isTracking) {
            return -1;
        }
        try {
            ResultSet result = getDB().executeQueryNow(QueryGen.getPlayer(player));
            if (!result.next()) {
                getDB().queueUpdate(QueryGen.updatePlayer(player, null, null));
                result = getDB().executeQueryAfterQueue(QueryGen.getPlayer(player));
                result.next();
            }
            int id = result.getInt("id");
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException ignore) {}
        return -1;
    }

    @Override
    public int newGame(Timestamp createTime, World world) {
        if (!isTracking) {
            return -1;
        }
        try {
            getDB().queueUpdate(QueryGen.createGame(createTime, world.getName()));
            ResultSet result = getDB().executeQueryAfterQueue(QueryGen.getGame(createTime, world.getName()));
            result.next();
            int id = result.getInt("id");
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException ignore) {}
        return -1;
    }

    @Override
    public void gameStarted(Game game, Timestamp startTime) {
        if (!isTracking) {
            return;
        }
        if (game.getId() < 0) {
            return;
        }
        getDB().queueUpdate(QueryGen.startGame(game.getId(), startTime));
        for (GamePlayer player : game.getGamePlayers()) {
            getDB().queueUpdate(QueryGen.updatePlayer(player.getName(),
                    (player.getType().getId() >= 0 ? player.getType().getId() : null),
                    plugin.getPlayerKit(player.getName())));
            if (player.isOnline() && player.getId() > -1) {
                getDB().queueUpdate(QueryGen.playerStartingInGame(player.getId(), game.getId(), player.isZombie(), plugin.getPlayerKit(player.getName())));
            }
        }
    }

    @Override
    public void playerJoinedGame(Game game, GamePlayer player) {
        if (!isTracking) {
            return;
        }
        if (game.getId() < 0 || player.getId() < 0) {
            return;
        }
        getDB().queueUpdate(QueryGen.playerJoiningInGame(player.getId(), game.getId(), player.isZombie(), plugin.getPlayerKit(player.getName())));
    }

    @Override
    public void gameEnded(Game game, Timestamp endTime) {
        if (!isTracking) {
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
        getDB().queueUpdate(QueryGen.endGame(game.getId(), endTime, humansWon));
        for (GamePlayer player : gamePlayers) {
            getDB().queueUpdate(QueryGen.updatePlayer(player.getName(),
                    (player.getType().getId() >= 0 ? player.getType().getId() : null),
                    plugin.getPlayerKit(player.getName())));
            if (player.isOnline() && player.getId() > -1) {
                getDB().queueUpdate(QueryGen.playerFinishingInGame(player.getId(), game.getId(), player.isZombie()));
            }
        }
    }

    @Override
    public void playerUpdate(GamePlayer player) {
        if (!isTracking) {
            return;
        }
        getDB().queueUpdate(QueryGen.updatePlayer(player.getName(),
                (player.getType().getId() >= 0 ? player.getType().getId() : null),
                plugin.getPlayerKit(player.getName())));
    }

    @Override
    public void playerKilled(GamePlayer killer, GamePlayer victim, Game game, Timestamp time, int weapon) {
        if (!isTracking) {
            return;
        }
        getDB().queueUpdate(QueryGen.playerKilled((killer != null ? killer.getId() : -1),
                (killer != null && killer.getType().getId() >= 0 ? killer.getType().getId() : null),
                victim.getId(), (victim.getType().getId() >= 0 ? victim.getType().getId() : null),
                game.getId(), time, weapon));
    }

    @Override
    public void playerTypeChange(Game game, GamePlayer player, PlayerType type) {
        if (!isTracking) {
            return;
        }
        if (game.getId() < 0 || player.getId() < 0 || type.getId() < 0) {
            return;
        }
        getDB().queueUpdate(QueryGen.playerTypeChange(player.getId(), game.getId(), type.getId()));
    }
}
