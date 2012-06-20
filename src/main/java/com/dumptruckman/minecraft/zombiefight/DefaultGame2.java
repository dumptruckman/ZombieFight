package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.locale.Message;
import com.dumptruckman.minecraft.pluginbase.locale.Messager;
import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GamePlayer;
import com.dumptruckman.minecraft.zombiefight.api.GameStatus;
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
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

class DefaultGame2 implements Game {

    private ZombieFight plugin;

    private World world;

    private Snapshot snapshot;

    private Map<String, GamePlayer> gamePlayers = new HashMap<String, GamePlayer>();

    boolean started = false;
    boolean ended = false;

    DefaultGame2(ZombieFight plugin, World world) {
        this.plugin = plugin;
        this.world = world;
    }

    protected ZFConfig getConfig() {
        return plugin.config();
    }

    protected Messager getMessager() {
        return plugin.getMessager();
    }

    protected Player getPlayer(String name) {
        return plugin.getServer().getPlayerExact(name);
    }

    protected GamePlayer getGamePlayer(String name) {
        GamePlayer gPlayer = gamePlayers.get(name);
        if (gPlayer == null) {
            gPlayer = new DefaultGamePlayer(name, plugin, this);
            gamePlayers.put(name, gPlayer);
        }
        return gPlayer;
    }

    protected boolean isCountingDown() {

    }

    protected void haltCountdown() {

    }

    protected void checkGameStart() {

    }

    protected void checkGameEnd() {

    }

    protected boolean isLockingZombies() {

    }


    /**
     * PUBLIC METHODS FROM Game INTERFACE
     */

    @Override
    public Location getSpawnLocation() {
        Location loc;
        if (!hasStarted()) {
            loc = getConfig().get(ZFConfig.PRE_GAME_SPAWN.specific(world.getName()));
        } else {
            loc = getConfig().get(ZFConfig.GAME_SPAWN.specific(world.getName()));
        }
        if (loc == null) {
            Logging.fine("No spawn set, will use world spawn.");
            loc = world.getSpawnLocation();
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
    public boolean isZombie(Player player) {
        return hasStarted() && getGamePlayer(player.getName()).isZombie();
    }

    @Override
    public void playerJoined(final Player player) {
        Logging.finer("Player joined game in world: " + world.getName());
        // Announcement
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                getMessager().sendMessage(player, ChatColor.GRAY + "Visit the official Minecraft Zombies website: "
                        + ChatColor.RED + "mczombies.com");
                plugin.displayKits(player);
            }
        }, 2L);
        // Mark player as joined
        GamePlayer gPlayer = getGamePlayer(player.getName());
        gPlayer.joinedGame();

        // Inform the player
        if (!hasStarted()) {
            if (isCountingDown()) {
                getMessager().normal(Language.JOIN_WHILE_GAME_STARTING, player);
            } else {
                getMessager().normal(Language.JOIN_WHILE_GAME_PREPARING, player);
            }
        } else {
            getMessager().normal(Language.JOIN_WHILE_GAME_IN_PROGRESS, player);
        }

        // Spawn the player
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                player.teleport(getSpawnLocation());
            }
        }, 2L);

        // Check if game should start, but not right away
        if (!hasStarted()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    // Handle starting game
                    checkGameStart();
                }
            }, 4L);
        }
    }

    @Override
    public void playerQuit(Player player) {
        Logging.finer("Player quit game in world: " + world.getName());

        getGamePlayer(player.getName()).leftGame();
        if (!hasStarted()) {
            if (isCountingDown()) {
                int playersInWorld = world.getPlayers().size();
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
        if (hasStarted() && !hasEnded() && isLockingZombies()) {
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
        GamePlayer gAttacker = getGamePlayer(attacker.getName());
        GamePlayer gVictim = getGamePlayer(victim.getName());
    }

    @Override
    public World getWorld() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void broadcast(Message message, Object... args) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void snapshotChunk(Chunk chunk) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void snapshotBlock(Block block) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
