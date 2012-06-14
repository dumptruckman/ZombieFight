package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.util.BukkitTools;
import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GameStatus;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

class DefaultGame implements Game {

    private GameStatus status = GameStatus.PREPARING;
    private ZombieFight plugin;
    private String worldName;
    private int countdown;
    private int countdownTask = -1;
    private Set<String> zombiePlayers;
    private Set<String> humanPlayers;
    private Set<String> onlinePlayers;
    private String firstZombie = "";

    private class CountdownTask implements Runnable {
        @Override
        public void run() {
            countdown--;
            if (countdown <= 0) {
                forceStart();
                Bukkit.getScheduler().cancelTask(countdownTask);
            }
        }
    }

    private class ZombieLockTask implements Runnable {
        @Override
        public void run() {
            firstZombie = null;
        }
    }

    DefaultGame(ZombieFight plugin, String worldName) {
        this.plugin = plugin;
        this.worldName = worldName;
        this.countdown = plugin.config().get(ZFConfig.COUNTDOWN_TIME);
        zombiePlayers = new HashSet<String>(plugin.config().get(ZFConfig.MAX_PLAYERS));
        humanPlayers = new LinkedHashSet<String>(plugin.config().get(ZFConfig.MAX_PLAYERS));
        onlinePlayers = new LinkedHashSet<String>(plugin.config().get(ZFConfig.MAX_PLAYERS));
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
        humanPlayers.addAll(plugin.getPlayersForWorld(worldName));
        status = GameStatus.IN_PROGRESS;
        Location location = plugin.config().get(ZFConfig.GAME_SPAWN.specific(worldName));
        if (location == null) {
            World world = Bukkit.getWorld(worldName);
            location = world.getSpawnLocation();
        }
        for (String playerName : humanPlayers) {
            Player player = Bukkit.getPlayerExact(playerName);
            if (playerName != null) {
                player.teleport(location);
            }
        }
        firstZombie = randomZombie();
        int secondsToRun = plugin.config().get(ZFConfig.ZOMBIE_LOCK);
        plugin.broadcastWorld(worldName, plugin.getMessager().getMessage(Language.RUN_FROM_ZOMBIE, secondsToRun));
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new ZombieLockTask(), BukkitTools.convertSecondsToTicks(secondsToRun));
        makeZombie(firstZombie);
    }

    @Override
    public void endGame() {
        plugin.broadcastWorld(worldName, plugin.getMessager().getMessage(Language.GAME_ENDED));
        status = GameStatus.ENDED;
    }

    @Override
    public boolean isPlaying(String name) {
        return humanPlayers.contains(name) || zombiePlayers.contains(name);
    }

    @Override
    public void haltCountdown() {
        Bukkit.getScheduler().cancelTask(countdownTask);
        countdownTask = -1;
    }

    @Override
    public boolean isZombie(String playerName) {
        return zombiePlayers.contains(playerName);
    }

    @Override
    public boolean hasOnlineZombies() {
        for (String name : zombiePlayers) {
            if (onlinePlayers.contains(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasOnlineHumans() {
        for (String name : humanPlayers) {
            if (onlinePlayers.contains(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int onlinePlayerCount() {
        return onlinePlayers.size();
    }

    @Override
    public void playerQuit(String playerName) {
        Logging.finer("Player quit game in world: " + worldName);
        onlinePlayers.remove(playerName);
    }

    @Override
    public void playerJoined(String playerName) {
        Logging.finer("Player joined game in world: " + worldName);
        onlinePlayers.add(playerName);
    }

    @Override
    public String randomZombie() {
        Random random = new Random(System.currentTimeMillis());
        int randPlayer = random.nextInt(humanPlayers.size());
        int i = 0;
        for (String name : humanPlayers) {
            if (i == randPlayer) {
                Logging.fine("Random zombie selected: " + name);
                return name;
            }
            i++;
        }
        Logging.fine("Could not select random zombie.");
        return null;
    }

    @Override
    public void makeZombie(String name) {
        humanPlayers.remove(name);
        plugin.zombifyPlayer(name);
        zombiePlayers.add(name);
    }

    @Override
    public String getFirstZombie() {
        return firstZombie;
    }
}
