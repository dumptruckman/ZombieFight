package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.database.SQLDatabase;
import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.StatsDatabase;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;

class DefaultStatsDatabase implements StatsDatabase {

    private ZombieFight plugin;

    DefaultStatsDatabase(ZombieFight plugin) {
        this.plugin = plugin;
        this.updateDB();
        this.checkTables();
    }

    SQLDatabase getDB() {
        return plugin.getDB();
    }

    private void checkTables() {
        if (!getDB().isConnected()) {
            return;
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
}
