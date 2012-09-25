/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.task;

import com.dumptruckman.minecraft.pluginbase.locale.Messager;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import org.bukkit.Bukkit;

public abstract class GameTask implements Runnable {

    private Game game;
    private ZombieFight plugin;

    protected boolean started = false, dead = false;

    protected final long delay, repeat;

    public GameTask(Game game, ZombieFight plugin, long delay, long repeat) {
        this.game = game;
        this.plugin = plugin;
        this.delay = delay;
        this.repeat = repeat;
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

    protected Messager getMessager() {
        return plugin.getMessager();
    }

    public final void start() {
        if (!started && !dead) {
            started = true;
            Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), this, delay);
            onStart();
        }
    }

    public final void kill() {
        dead = true;
    }

    @Override
    public void run() {
        if (!dead && !shouldEnd()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), this, repeat);
        }
        onRun();
    }

    protected void onStart() { }

    protected void onRun() { }

    public abstract boolean shouldEnd();
}
