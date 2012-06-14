package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GameStatus;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.pluginbase.locale.Messager;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

public class ZombieFightListener implements Listener {

    private ZombieFight plugin;
    private Messager messager;

    public ZombieFightListener(ZombieFight plugin) {
        this.plugin = plugin;
        this.messager = plugin.getMessager();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        Location playerLoc = player.getLocation();
        final World world = playerLoc.getWorld();
        final Game game = plugin.getGameManager().getGame(world.getName());
        if (game == null) {
            Logging.finest("Player joined non-game world.");
            return;
        }
        Location spawnLoc = plugin.config().get(ZFConfig.PRE_GAME_SPAWN.specific(world.getName()));
        if (spawnLoc == null) {
            Logging.fine("No pre-game spawn set, will use world spawn.");
            spawnLoc = world.getSpawnLocation();
        }

        // Handle joining
        switch (game.getStatus()) {
            case IN_PROGRESS:
                if (game.isPlaying(player.getName())) {
                    game.playerJoined(player.getName());
                }
            case ENDED:
                if (!game.isPlaying(player.getName())) {
                    Logging.fine("Teleporting non-game-playing player to spawn.");
                    player.teleport(spawnLoc);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            messager.normal(Language.JOIN_WHILE_GAME_IN_PROGRESS, player);
                        }
                    });
                }
                break;
            case PREPARING:
                Logging.fine("Game not started, teleporting player to spawn.");
                player.teleport(spawnLoc);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        messager.normal(Language.JOIN_WHILE_GAME_PREPARING, player);
                    }
                });
                break;
            case STARTING:
                Logging.fine("Game starting soon, teleporting player to spawn.");
                player.teleport(spawnLoc);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        messager.normal(Language.JOIN_WHILE_GAME_STARTING, player);
                    }
                });
                break;
            default:
                break;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                // Handle starting game
                switch (game.getStatus()) {
                    case STARTING:
                    case PREPARING:
                        int playersInWorld = world.getPlayers().size();
                        int minPlayers = plugin.config().get(ZFConfig.MIN_PLAYERS);
                        int maxPlayers = plugin.config().get(ZFConfig.MAX_PLAYERS);
                        if (playersInWorld >= minPlayers && playersInWorld < maxPlayers) {
                            Logging.fine("Enough players to start countdown.");
                            plugin.broadcastWorld(world.getName(), messager.getMessage(Language.ENOUGH_FOR_COUNTDOWN_START));
                            game.countdown();
                        } else if (world.getPlayers().size() >= maxPlayers) {
                            Logging.fine("Enough players to force game start.");
                            plugin.broadcastWorld(world.getName(), messager.getMessage(Language.ENOUGH_FOR_QUICK_START));
                            game.forceStart();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Location playerLoc = player.getLocation();
        World world = playerLoc.getWorld();
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
                    plugin.broadcastWorld(world.getName(), messager.getMessage(Language.TOO_FEW_PLAYERS));
                    game.haltCountdown();
                }
                break;
            case IN_PROGRESS:
                if (game.isPlaying(player.getName())) {
                    game.playerQuit(player.getName());
                    if (!game.hasOnlineZombies()) {
                        Logging.finest("No zombies left online!");
                        if (game.onlinePlayerCount() > 1) {
                            Player newZombiePlayer = null;
                            while (newZombiePlayer == null && game.onlinePlayerCount() > 1) {
                                String newZombie = game.randomZombie();
                                newZombiePlayer = Bukkit.getPlayerExact(newZombie);
                                if (newZombiePlayer == null) {
                                    game.playerQuit(newZombie);
                                }
                            }
                            if (newZombiePlayer == null) {
                                Logging.fine("Could not acquire new random zombie. Ending game.");
                                gameEnd(game);
                                return;
                            } else {
                                Logging.fine("Found a new zombie candidate: "+ newZombiePlayer.getName());
                                game.makeZombie(newZombiePlayer.getName());
                            }
                        } else {
                            Logging.fine("Ending game due to only 1 player left.");
                            gameEnd(game);
                            return;
                        }
                    }
                    if (!game.hasOnlineHumans()) {
                        Logging.fine("Game has no more humans online. Ending game.");
                        gameEnd(game);
                        return;
                    }
                }
                break;
            default:
                break;
        }
    }

    private void gameEnd(Game game) {
        game.endGame();
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        World world = player.getWorld();
        Game game = plugin.getGameManager().getGame(world.getName());
        if (game == null) {
            return;
        }
        if (game.getStatus() == GameStatus.IN_PROGRESS) {
            String firstZombie = game.getFirstZombie();
            if (firstZombie != null) {
                if (firstZombie.equals(player.getName())) {
                    event.setCancelled(true);
                }
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
        World world = damager.getWorld();
        Game game = plugin.getGameManager().getGame(world.getName());
        if (game == null) {
            return;
        }
        if (game.getStatus() == GameStatus.IN_PROGRESS) {
            if (game.isZombie(damager.getName())) {
                String firstZombie = game.getFirstZombie();
                if (firstZombie != null && firstZombie.equals(damager.getName())) {
                    event.setCancelled(true);
                    return;
                }
                event.setDamage(event.getDamage() + plugin.config().get(ZFConfig.ZOMBIE_DAMAGE));
            }
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
}
