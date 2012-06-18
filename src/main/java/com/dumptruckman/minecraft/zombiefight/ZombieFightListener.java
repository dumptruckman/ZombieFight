package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.util.BukkitTools;
import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GameStatus;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.pluginbase.locale.Messager;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.zombiefight.util.Perms;
import net.minecraft.server.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ZombieFightListener implements Listener {

    private ZombieFight plugin;
    private Set<String> playersPastBorder = new HashSet<String>();
    private int borderTask = -1;

    private class BorderDamagerTask implements Runnable {
        @Override
        public void run() {
            for (String name : playersPastBorder) {
                Player player = plugin.getServer().getPlayerExact(name);
                if (player != null) {
                    int newHealth = player.getHealth() - plugin.config().get(ZFConfig.BORDER_DAMAGE);
                    if (newHealth < 0) {
                        newHealth = 0;
                    }
                    player.setHealth(newHealth);
                }
            }
        }
    }

    public void resetBorderDamager() {
        if (borderTask != -1) {
            Bukkit.getScheduler().cancelTask(borderTask);
        }
        borderTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new BorderDamagerTask(), 0, BukkitTools.convertSecondsToTicks(plugin.config().get(ZFConfig.BORDER_TIME)));
    }

    public ZombieFightListener(ZombieFight plugin) {
        this.plugin = plugin;
    }

    private Messager getMessager() {
        return plugin.getMessager();
    }

    // Stuff for testing
    private String[] names = new String[3];
    @EventHandler(priority = EventPriority.LOWEST)
    public void playerNameChange(PlayerLoginEvent event) {
        for (int i = 0; i < names.length; i++) {
            if (names[i] == null) {
                CraftPlayer cPlayer = (CraftPlayer) event.getPlayer();
                EntityPlayer ePlayer = cPlayer.getHandle();
                ePlayer.name = i + ePlayer.name;
                ePlayer.displayName = ePlayer.name;
                ePlayer.listName = ePlayer.name;
                names[i] = ePlayer.name;
                return;
            }
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerNameUnchange(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (int i = 0; i < names.length; i++) {
            if (names[i] != null && names[i] == player.getName()) {
                names[i] = null;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final World world = player.getWorld();
        final Game game = plugin.getGameManager().getGame(world.getName());
        if (game == null) {
            Logging.finest("Player joined non-game world.");
            return;
        }
        Location loc = plugin.config().get(ZFConfig.PRE_GAME_SPAWN.specific(world.getName()));
        if (loc == null) {
            Logging.fine("No pre-game spawn set, will use world spawn.");
            loc = world.getSpawnLocation();
        }
        final Location spawnLoc = loc;

        // Handle joining
        switch (game.getStatus()) {
            case IN_PROGRESS:
                if (game.isPlaying(player.getName())) {
                    game.playerJoined(player.getName());
                }
                break;
            case ENDED:
                if (!game.isPlaying(player.getName())) {
                    Logging.fine("Teleporting non-game-playing player to spawn.");
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            player.teleport(spawnLoc);
                            getMessager().normal(Language.JOIN_WHILE_GAME_IN_PROGRESS, player);
                        }
                    }, 2L);
                }
                break;
            case PREPARING:
                Logging.fine("Game not started, teleporting player to spawn.");
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.teleport(spawnLoc);
                        getMessager().normal(Language.JOIN_WHILE_GAME_PREPARING, player);
                    }
                }, 2L);
                break;
            case STARTING:
                Logging.fine("Game starting soon, teleporting player to spawn.");
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.teleport(spawnLoc);
                        getMessager().normal(Language.JOIN_WHILE_GAME_STARTING, player);
                    }
                }, 2L);
                break;
            default:
                break;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                // Handle starting game
            game.checkGameStart();
            }
        }, 5L);
    }

    @EventHandler
    public void blockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Game game = plugin.getGameManager().getGame(world.getName());
        if (game == null) {
            return;
        }
        if (game.getStatus() == GameStatus.IN_PROGRESS) {
            if (game.isZombie(player.getName())) {
                String firstZombie = game.getFirstZombie();
                if (firstZombie != null && firstZombie.equals(player.getName())) {
                    event.setCancelled(true);
                    return;
                }
                Random rand = new Random(System.currentTimeMillis());
                if (rand.nextInt(100) < plugin.config().get(ZFConfig.INSTA_BREAK)) {
                    event.setInstaBreak(true);
                }
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Game game = plugin.getGameManager().getGame(world.getName());
        if (game == null) {
            return;
        }
        if (game.getStatus() == GameStatus.IN_PROGRESS) {
            if (game.isZombie(player.getName())) {
                String firstZombie = game.getFirstZombie();
                if (firstZombie != null && firstZombie.equals(player.getName())) {
                    event.setCancelled(true);
                }
            }
        } else {
            if (!Perms.CAN_ALWAYS_BREAK.hasPermission(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Game game = plugin.getGameManager().getGame(world.getName());
        if (game == null) {
            Logging.finest("Player quit non-game world.");
            return;
        }
        switch (game.getStatus()) {
            case STARTING:
                int playersInWorld = world.getPlayers().size();
                int minPlayers = plugin.config().get(ZFConfig.MIN_PLAYERS);
                if (playersInWorld < minPlayers) {
                    Logging.fine("Player quit caused countdown to halt.");
                    plugin.broadcastWorld(world.getName(), getMessager().getMessage(Language.TOO_FEW_PLAYERS));
                    game.haltCountdown();
                }
                break;
            case IN_PROGRESS:
                if (game.isPlaying(player.getName())) {
                    game.playerQuit(player.getName());
                    game.checkGameEnd();
                }
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Block fromBlock = event.getFrom().getBlock();
        Block toBlock = event.getTo().getBlock();
        if (fromBlock.equals(toBlock)) {
            return;
        }
        Player player = event.getPlayer();
        World world = player.getWorld();
        Game game = plugin.getGameManager().getGame(world.getName());
        if (game == null) {
            return;
        }
        if (game.getStatus() == GameStatus.IN_PROGRESS || game.getStatus() == GameStatus.ENDED) {
            String firstZombie = game.getFirstZombie();
            if (firstZombie != null) {
                if (firstZombie.equals(player.getName())) {
                    if (!(event instanceof PlayerTeleportEvent)) {
                        event.setCancelled(true);
                        player.teleport(event.getFrom().getBlock().getLocation());
                        return;
                    }
                }
            }
            Location loc = plugin.config().get(ZFConfig.GAME_SPAWN.specific(world.getName()));
            if (loc == null) {
                loc = world.getSpawnLocation();
            }
            int radius = plugin.config().get(ZFConfig.BORDER_RADIUS.specific(world.getName()));
            int warnRadius = plugin.config().get(ZFConfig.BORDER_WARN.specific(world.getName()));
            double distance = loc.distance(event.getTo());
            if (distance >= radius) {
                playersPastBorder.add(player.getName());
            } else if (distance >= radius - warnRadius) {
                getMessager().normal(Language.APPROACHING_BORDER, player);
            }
            if (distance < radius) {
                playersPastBorder.remove(player.getName());
            }
        }
    }

    @EventHandler
    public void playerDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player damager = (Player) event.getDamager();
        Player damagee = (Player) event.getEntity();
        World world = damager.getWorld();
        Game game = plugin.getGameManager().getGame(world.getName());
        if (game == null) {
            return;
        }
        if (game.getStatus() == GameStatus.IN_PROGRESS) {
            String firstZombie = game.getFirstZombie();
            if (firstZombie != null && (firstZombie.equals(damager.getName()) || firstZombie.equals(damagee.getName()))) {
                event.setCancelled(true);
                return;
            }
            if (game.isZombie(damager.getName()) && game.isZombie(damagee.getName())) {
                // Zombies no hurt zombies.
                event.setCancelled(true);
                return;
            } else if (!game.isZombie(damager.getName()) && !game.isZombie(damagee.getName())) {
                // Humans no hurt humans.
                event.setCancelled(true);
                return;
            } else {
                game.humanFound();
            }
            if (game.isZombie(damager.getName())) {
                event.setDamage(event.getDamage() + plugin.config().get(ZFConfig.ZOMBIE_DAMAGE));
            }
        } else /*if (game.getStatus() == GameStatus.ENDED)*/ {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerFood(FoodLevelChangeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        World world = player.getWorld();
        Game game = plugin.getGameManager().getGame(world.getName());
        if (game == null) {
            return;
        }
        if (game.getStatus() == GameStatus.IN_PROGRESS) {
            if (game.isZombie(player.getName())) {
                event.setCancelled(true);
            }
        }
    }

    public void playerChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        World world = player.getWorld();
        Game game = plugin.getGameManager().getGame(world.getName());
        if (game == null) {
            return;
        }
        if (game.isZombie(player.getName())) {
            event.setMessage(getMessager().getMessage(Language.ZOMBIE_TAG) + event.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Game game = plugin.getGameManager().getGame(world.getName());
        if (game == null) {
            return;
        }
        Location spawnLoc;
        switch (game.getStatus()) {
            case PREPARING:
            case STARTING:
                spawnLoc = plugin.config().get(ZFConfig.PRE_GAME_SPAWN.specific(world.getName()));
                if (spawnLoc == null) {
                    Logging.fine("No pre-game spawn set, will use world spawn.");
                    spawnLoc = world.getSpawnLocation();
                }
                event.setRespawnLocation(spawnLoc);
                break;
            case IN_PROGRESS:
            case ENDED:
                spawnLoc = plugin.config().get(ZFConfig.GAME_SPAWN.specific(world.getName()));
                if (spawnLoc == null) {
                    Logging.fine("No pre-game spawn set, will use world spawn.");
                    spawnLoc = world.getSpawnLocation();
                }
                event.setRespawnLocation(spawnLoc);
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        World world = player.getWorld();
        final Game game = plugin.getGameManager().getGame(world.getName());
        if (game == null) {
            return;
        }
        if (game.getStatus() == GameStatus.IN_PROGRESS) {
            if (!game.isZombie(player.getName())) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        game.makeZombie(player.getName());
                        game.checkGameEnd();
                    }
                });
            }
        }
    }
}
