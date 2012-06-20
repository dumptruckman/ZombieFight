package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.locale.Message;
import com.dumptruckman.minecraft.pluginbase.util.BukkitTools;
import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GameStatus;
import com.dumptruckman.minecraft.zombiefight.api.LootTable;
import com.dumptruckman.minecraft.zombiefight.api.Snapshot;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
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
    private int humanFinderTask = -1;
    private int gameEndTask = -1;
    private long humanFinder = 0;
    private ZombieLockTask zombieLock;
    private Set<String> zombiePlayers;
    private Set<String> humanPlayers;
    private Set<String> onlinePlayers;
    private String firstZombie = "";
    private Snapshot snapshot;
    boolean rollingBack = false;

    private class CountdownTask implements Runnable {
        private CountdownTask() {
            Logging.finest("Countdown task started");
        }
        @Override
        public void run() {
            if (plugin.shouldWarn(countdown)) {
                broadcast(Language.GAME_STARTING_IN, countdown);
            }
            countdown--;
            if (countdown > 0) {
                countdownTask = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this, 20L);
            } else {
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
            if (getStatus() == GameStatus.IN_PROGRESS) {
                humanFinderTask = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new HumanFinderTask(), 0L);
            }
        }
    }

    private class GameEndTask implements Runnable {
        private GameEndTask() {
            Logging.finest("Game end task started");
        }
        @Override
        public void run() {
            Logging.finest("Game end task ended");
            end(true);
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

    private class HumanFinderTask implements Runnable {
        private long beaconTick = 0;
        private HumanFinderTask() {
            Logging.finest("Human finder task started");
        }
        @Override
        public void run() {
            if (getStatus() != GameStatus.IN_PROGRESS) {
                Bukkit.getScheduler().cancelTask(humanFinderTask);
            }
            long lastHit = humanFinder - plugin.config().get(ZFConfig.HUMAN_FINDER_START);
            if (lastHit == 0) {
                strike();
                tick();
            } else if (lastHit > 0) {
                if (beaconTick >= plugin.config().get(ZFConfig.HUMAN_FINDER_TICK)) {
                    strike();
                    beaconTick = 0;
                }
                tick();
            } else {
                beaconTick = 0;
                humanFinder++;
            }
            if (getStatus() == GameStatus.IN_PROGRESS) {
                humanFinderTask = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this, 20L);
            }
        }
        private void tick() {
            humanFinder++;
            beaconTick++;
        }
        private void strike() {
            for (String name : humanPlayers) {
                Player player = plugin.getServer().getPlayerExact(name);
                if (player != null) {
                    Location loc = player.getLocation();
                    loc.getWorld().strikeLightningEffect(loc);
                }
            }
        }
    }

    private class RollbackTask implements Runnable {
        private Snapshot snapshot;
        private RollbackTask(Snapshot snapshot) {
            this.snapshot = snapshot;
        }
        @Override
        public void run() {
            snapshot.applySnapshot();
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
        if (countdownTask == -1) {
            countdownTask = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new CountdownTask(), 20L);
        }
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
        Logging.finer("Snapshotting chunks before game start.");
        startSnapshot();
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
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setSaturation(5F);
                player.setExhaustion(0F);
                String kitName = plugin.getPlayerKit(playerName);
                if (kitName != null) {
                    LootTable kit = plugin.getLootConfig().getKit(kitName);
                    if (kit != null) {
                        kit.addToInventory(player.getInventory());
                    } else {
                        plugin.getMessager().bad(Language.KIT_ERROR_DEFAULT, player, kitName);
                        plugin.setPlayerKit(playerName, null);
                        plugin.getLootConfig().getDefaultKit().addToInventory(player.getInventory());
                    }
                } else {
                    plugin.getLootConfig().getDefaultKit().addToInventory(player.getInventory());
                }
                fixName(player.getName());
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
        if (humanFinderTask != -1) {
            Bukkit.getScheduler().cancelTask(humanFinderTask);
            humanFinderTask = -1;
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

    private void end(boolean restart) {
        if (lastHumanTask != -1) {
            Bukkit.getScheduler().cancelTask(lastHumanTask);
            lastHumanTask = -1;
        }
        if (humanFinderTask != -1) {
            Bukkit.getScheduler().cancelTask(humanFinderTask);
            humanFinderTask = -1;
        }
        if (gameEndTask != -1) {
            Bukkit.getScheduler().cancelTask(gameEndTask);
            gameEndTask = -1;
        }
        if (zombieLockTask != -1) {
            zombieLock.run();
            Bukkit.getScheduler().cancelTask(zombieLockTask);
            zombieLockTask = -1;
        }
        status = GameStatus.ENDED;
        Location location = plugin.config().get(ZFConfig.PRE_GAME_SPAWN.specific(worldName));
        if (location == null) {
            World world = Bukkit.getWorld(worldName);
            location = world.getSpawnLocation();
        }
        for (String playerName : humanPlayers) {
            Player player = plugin.getServer().getPlayerExact(playerName);
            fixName(playerName);
            if (player != null) {
                player.teleport(location);
            }
        }
        for (String playerName : zombiePlayers) {
            fixName(playerName);
            Player player = plugin.getServer().getPlayerExact(playerName);
            if (player != null) {
                plugin.unZombifyPlayer(player.getName());
                player.teleport(location);
            }
        }
        rollbackWorld(restart);
        humanPlayers.clear();
        zombiePlayers.clear();
        onlinePlayers.clear();
    }

    public void forceEnd(boolean restart) {
        broadcast(Language.FORCE_END);
        end(restart);
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
        switch (getStatus()) {
            case STARTING:
                int playersInWorld = Bukkit.getWorld(worldName).getPlayers().size();
                int minPlayers = plugin.config().get(ZFConfig.MIN_PLAYERS);
                if (playersInWorld < minPlayers) {
                    Logging.fine("Player quit caused countdown to halt.");
                    broadcast(Language.TOO_FEW_PLAYERS);
                    haltCountdown();
                }
                break;
            case IN_PROGRESS:
                if (isPlaying(playerName)) {
                    onlinePlayers.remove(playerName);
                    checkGameEnd();
                }
                break;
            default:
                break;
        }
        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null) {
            player.setDisplayName(ChatColor.stripColor(player.getDisplayName()));
        }
    }

    public void endAllTasks() {
        Bukkit.getScheduler().cancelTask(countdownTask);
        Bukkit.getScheduler().cancelTask(lastHumanTask);
        Bukkit.getScheduler().cancelTask(zombieLockTask);
        Bukkit.getScheduler().cancelTask(humanFinderTask);
        Bukkit.getScheduler().cancelTask(gameEndTask);
    }

    @Override
    public void playerJoined(String playerName) {
        Location loc = plugin.config().get(ZFConfig.PRE_GAME_SPAWN.specific(worldName));
        Location loc2 = plugin.config().get(ZFConfig.GAME_SPAWN.specific(worldName));
        if (loc == null) {
            Logging.fine("No pre-game spawn set, will use world spawn.");
            loc = Bukkit.getWorld(worldName).getSpawnLocation();
        }
        if (loc2 == null) {
            Logging.fine("No game spawn set, will use world spawn.");
            loc2 = Bukkit.getWorld(worldName).getSpawnLocation();
        }
        final Location spawnLoc = loc;
        final Location gameSpawn = loc2;
        final Player player = Bukkit.getPlayerExact(playerName);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                plugin.getMessager().sendMessage(player, ChatColor.GRAY + "Visit the official ZombieFight website:"
                        + ChatColor.RED + "mczombies.com");
                plugin.displayKits(player);
            }
        }, 2L);

        // Handle joining
        switch (getStatus()) {
            case IN_PROGRESS:
                if (isPlaying(playerName)) {
                    Logging.finer("Player joined game in world: " + worldName);
                    onlinePlayers.add(playerName);
                    if (!isZombie(playerName)) {
                        plugin.unZombifyPlayer(player.getName());
                    }
                } else {
                    onlinePlayers.add(playerName);
                    makeZombie(playerName);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            player.teleport(gameSpawn);
                            plugin.getMessager().normal(Language.JOIN_WHILE_GAME_IN_PROGRESS, player);
                        }
                    }, 2L);
                }
                break;
            case ENDED:
                if (!isPlaying(player.getName())) {
                    Logging.fine("Teleporting non-game-playing player to spawn.");
                    plugin.unZombifyPlayer(player.getName());
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            player.teleport(spawnLoc);
                            plugin.getMessager().normal(Language.JOIN_WHILE_GAME_IN_PROGRESS, player);
                        }
                    }, 2L);
                } else {
                    if (!isZombie(playerName)) {
                        plugin.unZombifyPlayer(player.getName());
                    }
                }
                break;
            case PREPARING:
                Logging.fine("Game not started, teleporting player to spawn.");
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.teleport(spawnLoc);
                        plugin.getMessager().normal(Language.JOIN_WHILE_GAME_PREPARING, player);
                    }
                }, 2L);
                plugin.unZombifyPlayer(player.getName());
                break;
            case STARTING:
                Logging.fine("Game starting soon, teleporting player to spawn.");
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.teleport(spawnLoc);
                        plugin.getMessager().normal(Language.JOIN_WHILE_GAME_STARTING, player);
                    }
                }, 2L);
                plugin.unZombifyPlayer(player.getName());
                break;
            default:
                break;
        }

        fixName(playerName);

        if (getStatus() == GameStatus.PREPARING || getStatus() == GameStatus.STARTING) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    // Handle starting game
                    checkGameStart();
                }
            }, 5L);
        }
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
        fixName(name);
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
                Logging.finer("players: " + playersInWorld + " minPlayers: " + minPlayers + " maxPlayers: " + maxPlayers);
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
            LootTable reward = plugin.getLootConfig().getLastHumanReward();
            if (reward != null) {
                for (String name : humanPlayers) {
                    if (onlinePlayers.contains(name)) {
                        Player player = plugin.getServer().getPlayerExact(name);
                        if (player != null) {
                            reward.addToInventory(player.getInventory());
                            break;
                        }
                    }
                }
            } else {
                Logging.warning("Last human reward is not setup correctly!");
            }
            if (lastHumanTask == -1) {
                lastHumanTask = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new LastHumanTask(), BukkitTools.convertSecondsToTicks(finalHuman));
            }
        }
    }

    public void broadcast(Message message, Object...args) {
        plugin.broadcastWorld(worldName, plugin.getMessager().getMessage(message, args));
    }

    @Override
    public void humanFound() {
        humanFinder = 0;
    }

    private void startSnapshot() {
        snapshot = new DefaultSnapshot(Bukkit.getWorld(worldName));
    }

    private Snapshot getSnapshot() {
        return snapshot;
    }

    @Override
    public void snapshotChunk(Chunk chunk) {
        if (rollingBack) {
            return;
        }
        if (getStatus() != GameStatus.IN_PROGRESS) {
            return;
        }
        if (!chunk.getWorld().getName().equals(worldName)) {
            Logging.finer("Tried to snapshot chunk for world not for this game");
            return;
        }
        getSnapshot().snapshotChunk(chunk);
    }

    @Override
    public void snapshotBlock(Block block) {
        if (rollingBack) {
            return;
        }
        if (getStatus() != GameStatus.IN_PROGRESS) {
            return;
        }
        if (!block.getWorld().getName().equals(worldName)) {
            Logging.finer("Tried to snapshot block for world not for this game");
            return;
        }
        getSnapshot().snapshotBlock(block);
    }

    private void rollbackWorld(boolean restartAfter) {
        rollingBack = true;
        broadcast(Language.ROLLBACK);
        World world = Bukkit.getWorld(worldName);
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Item) {
                entity.remove();
            }
        }
        getSnapshot().applySnapshot();
        if (restartAfter) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    plugin.getGameManager().newGame(worldName);
                }
            });
        }
    }

    private void fixName(String playerName) {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null) {
            player.setDisplayName(ChatColor.stripColor(player.getDisplayName()));
            if (isZombie(playerName)) {
                player.setDisplayName(plugin.getMessager().getMessage(Language.ZOMBIE_NAME, player.getDisplayName()));
            } else {
                player.setDisplayName(plugin.getMessager().getMessage(Language.HUMAN_NAME, player.getDisplayName()));
            }
        }
    }
}
