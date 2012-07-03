package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.locale.Message;
import com.dumptruckman.minecraft.pluginbase.locale.Messager;
import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GamePlayer;
import com.dumptruckman.minecraft.zombiefight.api.LootTable;
import com.dumptruckman.minecraft.zombiefight.api.Snapshot;
import com.dumptruckman.minecraft.zombiefight.api.StatsDatabase;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.task.GameCountdownTask;
import com.dumptruckman.minecraft.zombiefight.task.GameEndTask;
import com.dumptruckman.minecraft.zombiefight.task.HumanFinderTask;
import com.dumptruckman.minecraft.zombiefight.task.LastHumanCountdownTask;
import com.dumptruckman.minecraft.zombiefight.task.ZombieLockCountdownTask;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.zombiefight.util.Perms;
import com.dumptruckman.minecraft.zombiefight.util.TimeTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

class DefaultGame implements Game {

    private ZombieFight plugin;

    private int id = -1;

    private World world;

    private Snapshot snapshot;

    private Map<String, GamePlayer> gamePlayers;

    private Set<String> bannedPlayers;

    private boolean started;
    private boolean ended;
    private boolean reset;
    private boolean finalized;

    private boolean countingDown;
    private boolean zombiesLocked;
    private boolean lastHuman;

    private GameCountdownTask countdownTask;
    private ZombieLockCountdownTask zombieLockTask;
    private LastHumanCountdownTask lastHumanTask;
    private GameEndTask gameEndTask;
    private HumanFinderTask humanFinderTask;

    DefaultGame(ZombieFight plugin, World world) {
        Logging.fine("Made new game object for " + world);
        this.plugin = plugin;
        this.world = world;
        init();
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

    protected StatsDatabase getStats() {
        return getPlugin().getStats();
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
                if (!isCountdownPhase()) {
                    broadcast(Language.ENOUGH_FOR_COUNTDOWN_START);
                }
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
                newZombie.makeZombie(true);
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
        if (onlineHumans.size() == 1 && !isZombieLockPhase() && !isLastHumanPhase()) {
            _lastHuman(onlineHumans.get(0));
        }
    }

    protected GamePlayer randomZombie() {
        Random rand = new Random(System.currentTimeMillis());
        Set<GamePlayer> onlinePlayers = getOnlinePlayers();
        if (onlinePlayers.size() > 0) {
            int index = rand.nextInt(onlinePlayers.size());
            int i = 0;
            for (GamePlayer gPlayer : onlinePlayers) {
                if (i == index) {
                    return gPlayer;
                }
                i++;
            }
        }
        return null;
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

    private void _startCountdown() {
        Logging.fine("Game startCountdown called");
        countingDown = true;
        countdownTask.start();
    }

    private void _startGame() {
        Logging.fine("Game startGame called");
        countingDown = false;
        started = true;
        snapshot.initialize();
        broadcast(Language.GAME_STARTING);
        Logging.finest("Game started");
        for (Player player : getWorld().getPlayers()) {
            GamePlayer gPlayer = getGamePlayer(player.getName());
            if (!gPlayer.isOnline()) {
                gPlayer.joinedGame();
            }
            gPlayer.makeHuman();
            player.teleport(getSpawnLocation());
            String kitName = plugin.getPlayerKit(player.getName());
            if (kitName != null) {
                LootTable kit = plugin.getLootConfig().getKit(kitName);
                if (kit != null) {
                    kit.addToInventory(player.getInventory());
                } else {
                    plugin.getMessager().bad(Language.KIT_ERROR_DEFAULT, player, kitName);
                    plugin.setPlayerKit(player.getName(), null);
                    plugin.getLootConfig().getDefaultKit().addToInventory(player.getInventory());
                }
            } else {
                plugin.getLootConfig().getDefaultKit().addToInventory(player.getInventory());
            }
        }
        GamePlayer gPlayer = randomZombie();
        if (gPlayer != null) {
            gPlayer.makeZombie(true);
        } else {
            Logging.warning("Game started with NO PLAYERS!");
        }
        broadcast(Language.RUN_FROM_ZOMBIE, TimeTools.toLongForm(plugin.config().get(ZFConfig.ZOMBIE_LOCK)));
        zombiesLocked = true;
        zombieLockTask.start();
        if (getStats() != null) {
            getStats().gameStarted(this, new Timestamp(System.currentTimeMillis()));
        }
        checkGameEnd();
    }

    private void _zombiesUnlocked() {
        Logging.fine("Game zombiesUnlocked called");
        countingDown = false;
        started = true;
        zombiesLocked = false;
        broadcast(Language.ZOMBIE_RELEASE);
        humanFinderTask.start();
        checkGameEnd();
    }

    private void _lastHuman(GamePlayer lastHuman) {
        Logging.fine("Game lastHuman called");
        countingDown = false;
        started = true;
        zombiesLocked = false;
        this.lastHuman = true;
        int finalHuman = getConfig().get(ZFConfig.LAST_HUMAN);
        broadcast(Language.ONE_HUMAN_LEFT, TimeTools.toLongForm(finalHuman));
        LootTable reward = plugin.getLootConfig().getLastHumanReward();
        if (reward != null) {
            reward.addToInventory(lastHuman.getPlayer().getInventory());
            getMessager().normal(Language.LAST_HUMAN_REWARD, lastHuman.getPlayer());
        } else {
            Logging.warning("Last human reward is not setup correctly!");
        }
        lastHumanTask.setLastHuman(lastHuman);
        lastHumanTask.start();
    }

    private void _gameOver() {
        Logging.fine("Game gameOver called");
        countingDown = false;
        started = true;
        zombiesLocked = false;
        lastHuman = false;
        ended = true;
        if (getStats() != null) {
            getStats().gameEnded(this, new Timestamp(System.currentTimeMillis()));
        }
        id = -1;
        broadcast(Language.GAME_ENDED);
        gameEndTask.start();
    }

    private void _resetGame(boolean restart) {
        Logging.fine("Game resetGame called");
        countingDown = false;
        started = true;
        zombiesLocked = false;
        lastHuman = false;
        ended = true;
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
        finalized = true;
        if (restart) {
            init();
        }
    }

    /**
     * PUBLIC METHODS FROM Game INTERFACE
     */

    /**
     *
     */

    @Override
    public int getId() {
        return id;
    }

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

    @Override
    public final void init() {
        Logging.fine("Game init called");
        if (countdownTask != null) {
            countdownTask.kill();
        }
        if (zombieLockTask != null) {
            zombieLockTask.kill();
        }
        if (lastHumanTask != null) {
            lastHumanTask.kill();
        }
        if (gameEndTask != null) {
            gameEndTask.kill();
        }
        if (humanFinderTask != null) {
            humanFinderTask.kill();
        }
        gamePlayers = new HashMap<String, GamePlayer>();
        bannedPlayers = new HashSet<String>();
        started = false;
        ended = false;
        reset = false;
        finalized = false;
        countingDown = false;
        zombiesLocked = true;
        lastHuman = false;
        id = -1;
        snapshot = new DefaultSnapshot(getWorld());
        countdownTask = new GameCountdownTask(this, plugin);
        zombieLockTask = new ZombieLockCountdownTask(this, plugin);
        lastHumanTask = new LastHumanCountdownTask(this, plugin);
        gameEndTask = new GameEndTask(this, plugin);
        humanFinderTask = new HumanFinderTask(this, plugin);
        List<Player> playersInWorld = getWorld().getPlayers();
        for (Player player : playersInWorld) {
            GamePlayer gPlayer = getGamePlayer(player.getName());
            gPlayer.joinedGame();
            gPlayer.makeHuman();
        }
        if (isEnabled()) {
            if (playersInWorld.size() < getConfig().get(ZFConfig.MIN_PLAYERS)) {
                broadcast(Language.JOIN_WHILE_GAME_PREPARING);
            }
            if (getStats() != null) {
                id = getStats().newGame(new Timestamp(System.currentTimeMillis()), getWorld());
            }
        }
        checkGameStart();
    }

    @Override
    public boolean isEnabled() {
        return plugin.getGameManager().isWorldEnabled(getWorld());
    }

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
                        + ChatColor.RED + "www.mczombies.com");
                getPlugin().displayKits(player);
            }
        }, 2L);
        // Mark player as joined
        GamePlayer gPlayer = getGamePlayer(player.getName());
        gPlayer.joinedGame();

        // Inform the player
        if (!hasStarted()) {
            if (isCountdownPhase()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        getMessager().normal(Language.JOIN_WHILE_GAME_STARTING, player);
                    }
                }, 4L);
            } else {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        getMessager().normal(Language.JOIN_WHILE_GAME_PREPARING, player);
                    }
                }, 4L);
            }
            // Check if game should start, but not right away
            Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable() {
                @Override
                public void run() {
                    // Handle starting game
                    checkGameStart();
                }
            }, 4L);
            // Spawn the player
            delayedSpawn(player);
        } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    getMessager().normal(Language.JOIN_WHILE_GAME_IN_PROGRESS, player);
                }
            }, 4L);

            if (!gPlayer.isZombie()) {
                gPlayer.makeZombie(false);
                // Spawn the player
                delayedSpawn(player);
            }
        }
        if (hasStarted() && !hasEnded() && getStats() != null) {
            getStats().playerJoinedGame(this, gPlayer);
        }
    }

    private void delayedSpawn(final Player player) {
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

        GamePlayer gPlayer = getGamePlayer(player.getName());
        gPlayer.leftGame();
        if (!hasStarted()) {
            if (isCountdownPhase()) {
                int playersInWorld = getWorld().getPlayers().size();
                int minPlayers = getConfig().get(ZFConfig.MIN_PLAYERS);
                if (playersInWorld < minPlayers) {
                    Logging.fine("Player quit caused countdown to halt.");
                    if (isCountdownPhase()) {
                        broadcast(Language.TOO_FEW_PLAYERS);
                    }
                    haltCountdown();
                }
            }
        } else if (!hasEnded()) {
            if (gPlayer.isZombie()) {
                int onlineZombies = 0;
                for (GamePlayer gamePlayer : getOnlinePlayers()) {
                    if (gamePlayer.isZombie() && !gamePlayer.getName().equals(gPlayer.getName())) {
                        onlineZombies++;
                    }
                }
                if (onlineZombies < 1) {
                    bannedPlayers.add(player.getName());
                    if (!Perms.CAN_ALWAYS_BREAK.hasPermission(player)) {
                        player.kickPlayer("Banned for current game for logging out as only zombie!  Check back in a bit.");
                    }
                }
            }
            checkGameEnd();
        }
    }

    @Override
    public void playerDied(Player player, Player killer) {
        if (hasStarted() && !hasEnded()) {
            final GamePlayer gPlayer = getGamePlayer(player.getName());
            int item = -1;
            GamePlayer gKiller = null;
            if (killer != null) {
                item = killer.getItemInHand().getTypeId();
                gKiller = getGamePlayer(killer.getName());
            }
            getStats().playerKilled(gKiller, gPlayer, this, new Timestamp(System.currentTimeMillis()), item);
            if (!isZombie(player)) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        gPlayer.makeZombie(true);
                        checkGameEnd();
                    }
                });
            }
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

    public void handleMove(Player player, Location toLoc) {
        if (!isZombie(player)) {
            for (GamePlayer gPlayer : getOnlinePlayers()) {
                Player zombie = gPlayer.getPlayer();
                if (gPlayer.isZombie() && zombie != null && gPlayer.isTracking(player)) {
                    zombie.setCompassTarget(toLoc);
                }
            }
        }
    }

    @Override
    public boolean allowDamage(Player attacker, Player victim) {
        if (isZombieLockPhase() || !hasStarted() || hasEnded()) {
            return false;
        }
        GamePlayer gAttacker = getGamePlayer(attacker.getName());
        GamePlayer gVictim = getGamePlayer(victim.getName());
        if (gAttacker.isZombie() && gVictim.isZombie()) {
            return false;
        } else if (!gAttacker.isZombie() && !gVictim.isZombie()) {
            return false;
        } else {
            humanFinderTask.humanFound();
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
    public void snapshotBlock(BlockState block) {
        if (!hasStarted() || hasReset()) {
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
        if (finalized) {
            Logging.fine("Initializing game before starting...");
            init();
        }
        if (hasStarted()) {
            return false;
        }
        _startCountdown();
        return true;
    }

    @Override
    public boolean forceStart() {
        if (finalized) {
            Logging.fine("Initializing game before starting...");
            init();
        }
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
        return true;  //To change body of implemented methods use File | Settings | File Templates.
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

    @Override
    public void addZombieItems(Inventory inventory) {
        if (inventory.contains(Material.COMPASS)) {
            return;
        }
        inventory.addItem(new ItemStack(Material.COMPASS));
        ItemStack item = new ItemStack(Material.IRON_PICKAXE);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 100);
        inventory.addItem(item);
        item = new ItemStack(Material.IRON_SPADE);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 100);
        inventory.addItem(item);
        item = new ItemStack(Material.IRON_AXE);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 100);
        inventory.addItem(item);
    }

    @Override
    public void rightClickAbilityUse(Player player, ItemStack item) {
        if (isZombie(player) && item.getType() == Material.COMPASS) {
            rightClickCompass(player);
        }
    }

    @Override
    public void leftClickAbilityUse(Player player, ItemStack item) {
        if (isZombie(player) && item.getType() == Material.COMPASS) {
            leftClickCompass(player);
        }
    }

    private void leftClickCompass(Player player) {
        Location location = player.getLocation();
        Player closestHuman = null;
        Location closestLocation = null;
        double distance = 0;
        for (Player human : getWorld().getPlayers()) {
            if (!isZombie(human) && !human.equals(player)) {
                Location currentLocation = human.getLocation();
                double currentDistance = location.distance(currentLocation);
                if (closestLocation == null || currentDistance < distance) {
                    closestHuman = human;
                    closestLocation = currentLocation;
                    distance = currentDistance;
                }
            }
        }
        GamePlayer gPlayer = getGamePlayer(player.getName());
        if (closestHuman == null || gPlayer.isTracking(closestHuman)) {
            return;
        } else {
            player.setCompassTarget(closestLocation);
            gPlayer.setTrackedPlayer(closestHuman);
            getMessager().normal(Language.ZOMBIE_SMELL_LOCK, player);
        }
    }

    private void rightClickCompass(Player player) {
        GamePlayer gPlayer = getGamePlayer(player.getName());
        Player trackedPlayer = gPlayer.getTrackedPlayer();
        if (trackedPlayer == null || isZombie(trackedPlayer)) {
            getMessager().normal(Language.ZOMBIE_SMELL_NOT_LOCKED, player);
            return;
        }
        Location location = player.getLocation();
        Location trackedLocation = trackedPlayer.getLocation();
        double yaw = 0.0D;
        double distX = trackedLocation.getX() - location.getX();
        double distY = trackedLocation.getY() - location.getY()/* + trackedPlayer.getEyeHeight() / 2.1D*/;
        double distZ = trackedLocation.getZ() - location.getZ();

        if (distZ > 0.0D && distX > 0.0D) yaw = Math.toDegrees(-Math.atan(distX / distZ));
        else if (distZ > 0.0D && distX < 0.0D) yaw = Math.toDegrees(-Math.atan(distX / distZ));
        else if (distZ < 0.0D && distX > 0.0D) yaw = -90D + Math.toDegrees(Math.atan(distZ / distX));
        else if (distZ < 0.0D && distX < 0.0D) yaw = 90D + Math.toDegrees(Math.atan(distZ / distX));

        double distance = location.distance(trackedLocation);
        double pitch = -Math.toDegrees(Math.atan(distY / distance));
        location = player.getLocation();
        player.teleport(new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), (float) yaw, (float) pitch));
        getMessager().normal(Language.ZOMBIE_SMELL_FACE, player);
        /*
        double yaw = 0;
        double pitch;

        Location loc = locations.get(0), pl = player.getLocation();

        double xDiff = pl.getX() - loc.getX();
        double yDiff = pl.getY() - loc.getY();
        double zDiff = pl.getZ() - loc.getZ();
        double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
        yaw = (Math.acos(xDiff / DistanceXZ) * 180 / Math.PI);
        pitch = (Math.acos(yDiff / DistanceY) * 180 / Math.PI) - 90;
        if (zDiff < 0.0) {
            yaw = yaw + (Math.abs(180 - yaw) * 2);
        }
         */
    }

    @Override
    public Set<GamePlayer> getGamePlayers() {
        return new HashSet<GamePlayer>(gamePlayers.values());
    }

    @Override
    public boolean isBanned(Player player) {
        return bannedPlayers.contains(player);
    }
}
