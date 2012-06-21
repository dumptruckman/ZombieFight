package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.locale.Message;
import com.dumptruckman.minecraft.pluginbase.locale.Messager;
import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GamePlayer;
import com.dumptruckman.minecraft.zombiefight.api.LootTable;
import com.dumptruckman.minecraft.zombiefight.api.Snapshot;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.task.GameCountdownTask;
import com.dumptruckman.minecraft.zombiefight.task.GameEndTask;
import com.dumptruckman.minecraft.zombiefight.task.ZombieLockCountdownTask;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

class DefaultGame2 implements Game {

    private ZombieFight plugin;

    private World world;

    private Snapshot snapshot;

    private Map<String, GamePlayer> gamePlayers = new HashMap<String, GamePlayer>();

    boolean started = false;
    boolean ended = false;
    boolean reset = false;

    boolean countingDown = false;
    boolean zombiesLocked = true;
    boolean lastHuman = false;

    private GameCountdownTask countdownTask;
    private ZombieLockCountdownTask zombieLockTask;

    DefaultGame2(ZombieFight plugin, World world) {
        this.plugin = plugin;
        this.world = world;
        snapshot = new DefaultSnapshot(getWorld());
        countdownTask = new GameCountdownTask(this, plugin);
        zombieLockTask = new ZombieLockCountdownTask(this, plugin);
    }

    protected ZombieFight getPlugin() {
        return plugin;
    }

    protected ZFConfig getConfig() {
        return getPlugin().config();
    }

    protected Messager getMessager() {
        return getPlugin().getMessager();
    }

    protected GamePlayer getGamePlayer(String name) {
        GamePlayer gPlayer = gamePlayers.get(name);
        if (gPlayer == null) {
            gPlayer = new DefaultGamePlayer(name, getPlugin(), this);
            gamePlayers.put(name, gPlayer);
        }
        return gPlayer;
    }

    protected void haltCountdown() {
        countingDown = false;
    }

    protected void checkGameStart() {
        if (!hasStarted()) {
            int playersInWorld = getWorld().getPlayers().size();
            int minPlayers = getConfig().get(ZFConfig.MIN_PLAYERS);
            int maxPlayers = getConfig().get(ZFConfig.MAX_PLAYERS);
            Logging.finer("players: " + playersInWorld + " minPlayers: " + minPlayers + " maxPlayers: " + maxPlayers);
            if (playersInWorld >= minPlayers && playersInWorld < maxPlayers) {
                Logging.fine("Enough players to start countdown.");
                broadcast(Language.ENOUGH_FOR_COUNTDOWN_START);
                start();
            } else if (world.getPlayers().size() >= maxPlayers) {
                Logging.fine("Enough players to force game start.");
                broadcast(Language.ENOUGH_FOR_QUICK_START);
                forceStart();
            }
        }
    }

    protected void checkGameEnd() {
        Set<GamePlayer> onlinePlayers = getOnlinePlayers();
        List<GamePlayer> onlineZombies = new LinkedList<GamePlayer>();
        List<GamePlayer> onlineHumans = new LinkedList<GamePlayer>();
        for (GamePlayer player : onlinePlayers) {
            if (player.isZombie()) {
                onlineZombies.add(player);
            } else {
                onlineHumans.add(player);
            }
        }

        if (onlineZombies.size() < 1) {
            Logging.finest("No zombies left online!");
            if (onlineHumans.size() > 1) {
                Random rand = new Random(System.currentTimeMillis());
                int index = rand.nextInt(onlineHumans.size());
                GamePlayer newZombie = onlineHumans.get(index);
                Logging.fine("Found a new zombie candidate: "+ newZombie.getPlayer().getName());
                newZombie.makeZombie();
                onlineHumans.remove(index);
                onlineZombies.add(newZombie);
            } else {
                broadcast(Language.GAME_ENDED_TOO_FEW_PLAYERS);
                _gameOver();
                return;
            }
        }
        if (onlineHumans.size() < 1) {
            broadcast(Language.ALL_HUMANS_DEAD);
            _gameOver();
            return;
        }
        if (onlineHumans.size() == 1 && !isZombieLockPhase()) {
            _lastHuman(onlineHumans.get(0));
        }
    }

    protected Snapshot getSnapshot() {
        return snapshot;
    }

    private Set<GamePlayer> getOnlinePlayers() {
        Collection<GamePlayer> gamePlayers = this.gamePlayers.values();
        Set<GamePlayer> onlinePlayers = new HashSet<GamePlayer>(gamePlayers.size());
        for (GamePlayer player : gamePlayers) {
            if (player != null && player.isOnline()) {
                onlinePlayers.add(player);
            }
        }
        return onlinePlayers;
    }

    private void startSnapshot() {
        snapshot.initialize();
    }

    private void _startCountdown() {
        countingDown = true;
        countdownTask.start();
    }

    private void _startGame() {
        countingDown = false;
        started = true;
        broadcast(Language.GAME_STARTING);
        zombieLockTask.start();
    }

    private void _zombiesUnlocked() {
        zombiesLocked = false;
        broadcast(Language.ZOMBIE_RELEASE);
    }

    private void _lastHuman(GamePlayer lastHuman) {
        int finalHuman = getConfig().get(ZFConfig.LAST_HUMAN);
        broadcast(Language.ONE_HUMAN_LEFT, finalHuman);
        LootTable reward = plugin.getLootConfig().getLastHumanReward();
        if (reward != null) {
            reward.addToInventory(lastHuman.getPlayer().getInventory());
            getMessager().normal(Language.LAST_HUMAN_REWARD, lastHuman.getPlayer());
        } else {
            Logging.warning("Last human reward is not setup correctly!");
        }
    }

    private void _gameOver() {
        broadcast(Language.GAME_ENDED);
        ended = true;
        new GameEndTask(this, plugin).start();
    }

    private void _resetGame(boolean restart) {
        reset = true;
        for (GamePlayer gPlayer : gamePlayers.values()) {
            gPlayer.makeHuman();
            Player player = gPlayer.getPlayer();
            if (player != null) {
                player.teleport(getSpawnLocation());
            }
        }
        broadcast(Language.ROLLBACK);
        getSnapshot().applySnapshot();
        if (restart) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable() {
                @Override
                public void run() {
                    getPlugin().getGameManager().newGame(getWorld().getName());
                }
            });
        }
    }

    /**
     * PUBLIC METHODS FROM Game INTERFACE
     */

    @Override
    public Location getSpawnLocation() {
        Location loc;
        if (!hasStarted() || hasReset()) {
            loc = getConfig().get(ZFConfig.PRE_GAME_SPAWN.specific(getWorld().getName()));
        } else {
            loc = getConfig().get(ZFConfig.GAME_SPAWN.specific(getWorld().getName()));
        }
        if (loc == null) {
            Logging.fine("No spawn set, will use world spawn.");
            loc = getWorld().getSpawnLocation();
        }
        return loc;
    }

    /**
     *
     */


    @Override
    public boolean hasStarted() {
        return started;
    }

    @Override
    public boolean hasEnded() {
        return ended;
    }

    @Override
    public boolean hasReset() {
        return reset;
    }

    @Override
    public boolean isZombie(Player player) {
        return hasStarted() && getGamePlayer(player.getName()).isZombie();
    }

    @Override
    public void playerJoined(final Player player) {
        Logging.finer("Player joined game in world: " + getWorld().getName());
        // Announcement
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable() {
            @Override
            public void run() {
                getMessager().sendMessage(player, ChatColor.GRAY + "Visit the official Minecraft Zombies website: "
                        + ChatColor.RED + "mczombies.com");
                getPlugin().displayKits(player);
            }
        }, 2L);
        // Mark player as joined
        GamePlayer gPlayer = getGamePlayer(player.getName());
        gPlayer.joinedGame();

        // Inform the player
        if (!hasStarted()) {
            if (isCountdownPhase()) {
                getMessager().normal(Language.JOIN_WHILE_GAME_STARTING, player);
            } else {
                getMessager().normal(Language.JOIN_WHILE_GAME_PREPARING, player);
            }
            // Check if game should start, but not right away
            Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable() {
                @Override
                public void run() {
                    // Handle starting game
                    checkGameStart();
                }
            }, 4L);
        } else {
            getMessager().normal(Language.JOIN_WHILE_GAME_IN_PROGRESS, player);
            gPlayer.makeZombie();
        }

        // Spawn the player
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable() {
            @Override
            public void run() {
                player.teleport(getSpawnLocation());
            }
        }, 2L);
    }

    @Override
    public void playerQuit(Player player) {
        Logging.finer("Player quit game in world: " + getWorld().getName());

        getGamePlayer(player.getName()).leftGame();
        if (!hasStarted()) {
            if (isCountdownPhase()) {
                int playersInWorld = getWorld().getPlayers().size();
                int minPlayers = getConfig().get(ZFConfig.MIN_PLAYERS);
                if (playersInWorld < minPlayers) {
                    Logging.fine("Player quit caused countdown to halt.");
                    broadcast(Language.TOO_FEW_PLAYERS);
                    haltCountdown();
                }
            }
        } else if (!hasEnded()) {
            checkGameEnd();
        }
    }

    @Override
    public boolean allowMove(Player player, Block fromBlock, Block toBlock) {
        if (hasStarted() && !hasEnded() && isZombieLockPhase()) {
            GamePlayer gPlayer = getGamePlayer(player.getName());
            if (gPlayer.isZombie() && !(fromBlock.getX() == toBlock.getX() && fromBlock.getZ() == toBlock.getZ())) {
                player.teleport(fromBlock.getLocation());
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean allowDamage(Player attacker, Player victim) {
        if (!hasStarted() || hasEnded()) {
            return false;
        }
        GamePlayer gAttacker = getGamePlayer(attacker.getName());
        GamePlayer gVictim = getGamePlayer(victim.getName());
        if (isZombieLockPhase() && gAttacker.isZombie()) {
            return false;
        }
        if (gAttacker.isZombie() && gVictim.isZombie()) {
            return false;
        } else if (!gAttacker.isZombie() && !gVictim.isZombie()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void broadcast(Message message, Object... args) {
        getPlugin().broadcastWorld(getWorld().getName(), getMessager().getMessage(message, args));
    }

    @Override
    public void snapshotChunk(Chunk chunk) {
        if (!hasStarted() || hasEnded()) {
            return;
        }
        if (!chunk.getWorld().equals(getWorld())) {
            Logging.finer("Tried to snapshot chunk for world not for this game");
            return;
        }
        getSnapshot().snapshotChunk(chunk);
    }

    @Override
    public void snapshotBlock(Block block) {
        if (!hasStarted() || hasEnded()) {
            return;
        }
        if (!block.getWorld().equals(getWorld())) {
            Logging.finer("Tried to snapshot block for world not for this game");
            return;
        }
        getSnapshot().snapshotBlock(block);
    }

    @Override
    public boolean start() {
        if (hasStarted()) {
            return false;
        }
        _startCountdown();
        return true;
    }

    @Override
    public boolean forceStart() {
        if (hasStarted()) {
            return false;
        }
        _startGame();
        return true;
    }

    @Override
    public boolean end() {
        if (hasEnded()) {
            return false;
        }
        _gameOver();
        return true;
    }

    @Override
    public boolean forceEnd(boolean restart) {
        if (hasReset()) {
            return false;
        }
        _resetGame(restart);
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isCountdownPhase() {
        return countingDown;
    }

    @Override
    public boolean isZombieLockPhase() {
        return zombiesLocked;
    }

    @Override
    public void unlockZombies() {
        _zombiesUnlocked();
    }

    @Override
    public boolean isLastHumanPhase() {
        return lastHuman;
    }
}