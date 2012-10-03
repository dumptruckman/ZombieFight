package com.dumptruckman.minecraft.zombiefight.util;

import com.dumptruckman.minecraft.pluginbase.database.SQLDatabase;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;

import java.sql.SQLException;

public abstract class SQLRunnable {

    private final ZombieFight plugin;

    public SQLRunnable(final ZombieFight plugin) {
        this.plugin = plugin;
    }

    protected ZombieFight getPlugin() {
        return this.plugin;
    }

    protected SQLDatabase getDB() {
        return getPlugin().getDB();
    }

    public abstract void run() throws SQLException;
}
