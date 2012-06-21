package com.dumptruckman.minecraft.zombiefight.task;

import com.dumptruckman.minecraft.pluginbase.locale.Messager;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;

public abstract class GameTask implements Runnable {

    private Game game;
    private ZombieFight plugin;

    public GameTask(Game game, ZombieFight plugin) {
        this.game = game;
        this.plugin = plugin;
    }

    protected Game getGame() {
        return this.game;
    }

    protected ZombieFight getPlugin() {
        return this.plugin;
    }

    protected ZFConfig getConfig() {
        return plugin.config();
    }

    protected Messager getMesager() {
        return plugin.getMessager();
    }
}
