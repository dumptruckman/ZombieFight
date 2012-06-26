package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.database.SQLDatabase;
import com.dumptruckman.minecraft.zombiefight.api.StatsDatabase;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;

class DefaultStatsDatabase implements StatsDatabase {

    private ZombieFight plugin;

    DefaultStatsDatabase(ZombieFight plugin) {
        this.plugin = plugin;
        this.updateDB();
    }

    SQLDatabase getDB() {
        return plugin.getDB();
    }

    private void updateDB() {
        if (!getDB().isConnected()) {
            return;
        }
        //ResultSet table = getDB().query()
    }
}
