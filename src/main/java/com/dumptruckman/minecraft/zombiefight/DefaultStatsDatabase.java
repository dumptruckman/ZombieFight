package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.database.SQLDatabase;
import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GamePlayer;
import com.dumptruckman.minecraft.zombiefight.api.StatsDatabase;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import org.bukkit.entity.Player;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class DefaultStatsDatabase implements StatsDatabase {

    private Queue<String> queries = new ConcurrentLinkedQueue<String>();
    private ZombieFight plugin;

    DefaultStatsDatabase(ZombieFight plugin) {
        this.plugin = plugin;
        this.checkTables();
        this.updateDB();
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
        return getDB().connect(plugin);
    }

    @Override
    public void gameStarted(Game game) {
        queries.add(QueryGen.createGame(game.getStartTime()));
        for (GamePlayer player : game.getGamePlayers()) {
            queries.add(QueryGen.updatePlayer(player.getName(), null, plugin.getPlayerKit(player.getName())));
            if (player.isOnline()) {

            }
        }
    }

    @Override
    public void playerJoinedGame(Player player, Game game) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
