package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.locale.Message;
import com.dumptruckman.minecraft.pluginbase.util.BukkitTools;
import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GameStatus;
import com.dumptruckman.minecraft.zombiefight.api.LootTable;
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
    private int lastHumanTask = -1;
    private int zombieLockTask = -1;
    private ZombieLockTask zombieLock;
    private Set<String> zombiePlayers;
    private Set<String> humanPlayers;
    private Set<String> onlinePlayers;
    private String firstZombie = "";

    private class CountdownTask implements Runnable {
        private CountdownTask() {
            Logging.finest("Countdown task started");
        }
        @Override
        public void run() {
            countdown--;
            if (countdown <= 0) {
                Logging.finest("Countdown task ended");
                Bukkit.getScheduler().cancelTask(countdownTask);
                forceStart();
            }
        }
    }

    private class ZombieLockTask implements Runnable {
        private ZombieLockTask() {
            Logging.finest("Zombie lock task started");
        }
        @Override
        public void run() {
            Logging.finest("Zombie lock task ended");
            firstZombie = null;
            broadcast(Language.ZOMBIE_RELEASE);
            zombieLockTask = -1;
        }
    }

    private class GameEndTask implements Runnable {
        private GameEndTask() {
            Logging.finest("Game end task started");
        }
        @Override
        public void run() {
            Logging.finest("Game end task ended");
            forceEnd();
        }
    }

    private class LastHumanTask implements Runnable {
        private LastHumanTask() {
            Logging.finest("Last human task started");
        }
        @Override
        public void run() {
            Logging.finest("Last human task ended");
            String winner = null;
            for (String name : humanPlayers) {
                if (onlinePlayers.contains(name)) {
                    Player player = plugin.getServer().getPlayerExact(name);
                    if (player != null) {
                        winner = player.getName();
                        break;
                    }
                }
            }
            broadcast(Language.LAST_HUMAN_WON, winner);
            endGame();
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
            broadcast(Language.COULD_NOT_COUNTDOWN);
            forceStart();
        }
    }

    @Override
    public void forceStart() {
        if (countdownTask != -1) {
            Bukkit.getScheduler().cancelTask(countdownTask);
            countdownTask = -1;
        }
        if (status == GameStatus.IN_PROGRESS || status == GameStatus.ENDED) {
            Logging.finest("Game.forceStart() called while game in progress or ended");
            return;
        }
        Logging.finest("Game.forceStart() called");
        broadcast(Language.GAME_STARTING);
        humanPlayers.addAll(plugin.getPlayersForWorld(worldName));
        onlinePlayers.addAll(humanPlayers);
        status = GameStatus.IN_PROGRESS;
        Location location = plugin.config().get(ZFConfig.GAME_SPAWN.specific(worldName));
        if (location == null) {
            World world = Bukkit.getWorld(worldName);
            location = world.getSpawnLocation();
        }
        for (String playerName : humanPlayers) {
            Player player = plugin.getServer().getPlayerExact(playerName);
            if (player != null) {
                player.teleport(location);
                player.getInventory().clear();
            }
        }
        firstZombie = randomZombie();
        int secondsToRun = plugin.config().get(ZFConfig.ZOMBIE_LOCK);
        broadcast(Language.RUN_FROM_ZOMBIE, secondsToRun);
        zombieLock = new ZombieLockTask();
        zombieLockTask = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, zombieLock, BukkitTools.convertSecondsToTicks(secondsToRun));
        makeZombie(firstZombie);
        checkGameEnd();
    }

    @Override
    public void endGame() {
        if (lastHumanTask != -1) {
            Bukkit.getScheduler().cancelTask(lastHumanTask);
            lastHumanTask = -1;
        }
        if (zombieLockTask != -1) {
            zombieLock.run();
            Bukkit.getScheduler().cancelTask(zombieLockTask);
            zombieLockTask = -1;
        }
        if (status == GameStatus.STARTING || status == GameStatus.PREPARING) {
            Logging.finest("Game.endGame() called while game preparing or starting");
            return;
        }
        Logging.finest("Game.endGame() called");
        broadcast(Language.GAME_ENDED);
        status = GameStatus.ENDED;
        int secondsToReset = plugin.config().get(ZFConfig.END_DURATION);
        broadcast(Language.GAME_RESETTING, secondsToReset);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new GameEndTask(), BukkitTools.convertSecondsToTicks(secondsToReset));
    }

    private void forceEnd() {
        if (lastHumanTask != -1) {
            Bukkit.getScheduler().cancelTask(lastHumanTask);
            lastHumanTask = -1;
        }
        if (zombieLockTask != -1) {
            zombieLock.run();
            Bukkit.getScheduler().cancelTask(zombieLockTask);
            zombieLockTask = -1;
        }
        Location location = plugin.config().get(ZFConfig.PRE_GAME_SPAWN.specific(worldName));
        if (location == null) {
            World world = Bukkit.getWorld(worldName);
            location = world.getSpawnLocation();
        }
        for (String playerName : humanPlayers) {
            Player player = plugin.getServer().getPlayerExact(playerName);
            if (player != null) {
                player.teleport(location);
            }
        }
        for (String playerName : zombiePlayers) {
            Player player = plugin.getServer().getPlayerExact(playerName);
            if (player != null) {
                plugin.unZombifyPlayer(player.getName());
                player.teleport(location);
            }
        }
        plugin.getGameManager().newGame(worldName);
        plugin.getGameManager().getGame(worldName).checkGameStart();
    }

    @Override
    public boolean isPlaying(String name) {
        return humanPlayers.contains(name) || zombiePlayers.contains(name);
    }

    @Override
    public void haltCountdown() {
        Logging.finest("Countdown task halted");
        if (countdownTask != -1) {
            Bukkit.getScheduler().cancelTask(countdownTask);
            countdownTask = -1;
        }
    }

    @Override
    public boolean isZombie(String playerName) {
        return zombiePlayers.contains(playerName);
    }

    @Override
    public boolean hasOnlineZombies() {
        for (String name : zombiePlayers) {
            Logging.finest("Does onlinePlayers contain: " + name);
            if (onlinePlayers.contains(name)) {
                Logging.finest("onlinePlayers contains: " + name);
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

    @Override
    public String getWorld() {
        return worldName;
    }

    public void checkGameStart() {
        switch (status) {
            case STARTING:
            case PREPARING:
                World world = Bukkit.getWorld(worldName);
                int playersInWorld = world.getPlayers().size();
                int minPlayers = plugin.config().get(ZFConfig.MIN_PLAYERS);
                int maxPlayers = plugin.config().get(ZFConfig.MAX_PLAYERS);
                if (playersInWorld >= minPlayers && playersInWorld < maxPlayers) {
                    Logging.fine("Enough players to start countdown.");
                    broadcast(Language.ENOUGH_FOR_COUNTDOWN_START);
                    countdown();
                } else if (world.getPlayers().size() >= maxPlayers) {
                    Logging.fine("Enough players to force game start.");
                    broadcast(Language.ENOUGH_FOR_QUICK_START);
                    forceStart();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void checkGameEnd() {
        if (!hasOnlineZombies()) {
            Logging.finest("No zombies left online!");
            if (onlinePlayerCount() > 1) {
                Player newZombiePlayer = null;
                while (newZombiePlayer == null && onlinePlayerCount() > 1) {
                    String newZombie = randomZombie();
                    newZombiePlayer = plugin.getServer().getPlayerExact(newZombie);
                    if (newZombiePlayer == null) {
                        playerQuit(newZombie);
                    }
                }
                if (newZombiePlayer == null) {
                    Logging.fine("Could not acquire new random zombie. Ending game.");
                    endGame();
                    return;
                } else {
                    Logging.fine("Found a new zombie candidate: "+ newZombiePlayer.getName());
                    makeZombie(newZombiePlayer.getName());
                }
            } else {
                Logging.fine("Ending game due to only 1 or less players left.");
                endGame();
                return;
            }
        }
        if (!hasOnlineHumans()) {
            plugin.broadcastWorld(worldName, plugin.getMessager().getMessage(Language.ALL_HUMANS_DEAD));
            endGame();
            return;
        }
        int onlineHumans = 0;
        for (String name : humanPlayers) {
            if (onlinePlayers.contains(name)) {
                onlineHumans++;
            }
        }
        if (onlineHumans == 1) {
            int finalHuman = plugin.config().get(ZFConfig.FINAL_HUMAN);
            broadcast(Language.ONE_HUMAN_LEFT, finalHuman);
            LootTable reward = plugin.getLootConfig().getLootTable();
            if (reward != null) {
                for (String name : humanPlayers) {
                    if (onlinePlayers.contains(name)) {
                        Player player = plugin.getServer().getPlayerExact(name);
                        if (player != null) {
                            reward.addToInventory(player.getInventory());
                            return;
                        }
                    }
                }
            } else {
                Logging.warning("Last human reward is not setup correctly!");
            }
            lastHumanTask = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new LastHumanTask(), BukkitTools.convertSecondsToTicks(finalHuman));
        }
    }

    private void broadcast(Message message, Object...args) {
        plugin.broadcastWorld(worldName, plugin.getMessager().getMessage(message, args));
    }
}
