package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GameStatus;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import org.bukkit.Bukkit;

class DefaultGame implements Game {

    private GameStatus status = GameStatus.PREPARING;
    private ZombieFight plugin;
    private String worldName;
    private int countdown;
    private int countdownTask = -1;

    private class CountdownTask implements Runnable {
        @Override
        public void run() {
            countdown--;
            if (countdown <= 0) {
                forceStart();
            }
            Bukkit.getScheduler().cancelTask(countdownTask);
        }
    }

    DefaultGame(ZombieFight plugin, String worldName) {
        this.plugin = plugin;
        this.worldName = worldName;
        this.countdown = plugin.config().get(ZFConfig.COUNTDOWN_TIME);
    }

    @Override
    public GameStatus getStatus() {
        return status;
    }

    @Override
    public void countdown() {
        if (status == GameStatus.PREPARING) {
            status = GameStatus.STARTING;
        }
        countdownTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new CountdownTask(), 20L, 20L);
        if (countdownTask == -1) {
            plugin.broadcastWorld(worldName, plugin.getMessager().getMessage(Language.COULD_NOT_COUNTDOWN));
            forceStart();
        }
    }

    @Override
    public void forceStart() {
        plugin.broadcastWorld(worldName, plugin.getMessager().getMessage(Language.GAME_STARTING));
        status = GameStatus.IN_PROGRESS;
    }

    @Override
    public void endGame() {
        plugin.broadcastWorld(worldName, plugin.getMessager().getMessage(Language.GAME_STARTING));
        status = GameStatus.ENDED;
    }

    @Override
    public boolean isPlaying(String name) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void haltCountdown() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isZombie(String playerName) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasOnlineZombies() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasOnlineHumans() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int onlinePlayerCount() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void playerQuit(String playerName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void playerJoined(String playerName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String randomZombie() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
