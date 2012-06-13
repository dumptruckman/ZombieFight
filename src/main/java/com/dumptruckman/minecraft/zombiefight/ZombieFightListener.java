package com.dumptruckman.minecraft.zombiefight;

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
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

public class ZombieFightListener implements Listener {

    private ZombieFight plugin;
    private Messager messager;

    public ZombieFightListener(ZombieFight plugin) {
        this.plugin = plugin;
        this.messager = plugin.getMessager();
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location playerLoc = player.getLocation();
        World world = playerLoc.getWorld();
        Game game = plugin.getGameManager().getGame(world.getName());
        if (game == null) {
            return;
        }
        Location spawnLoc = plugin.config().get(ZFConfig.PRE_GAME_SPAWN.specific(world.getName()));
        if (spawnLoc == null) {
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
                    player.teleport(spawnLoc);
                    messager.normal(Language.JOIN_WHILE_GAME_IN_PROGRESS, player);
                }
                break;
            default:
                player.teleport(spawnLoc);
                messager.normal(Language.JOIN_WHILE_GAME_PREPARING, player);
                break;
        }

        // Handle starting game
        switch (game.getStatus()) {
            case STARTING:
            case PREPARING:
                int playersInWorld = world.getPlayers().size();
                int minPlayers = plugin.config().get(ZFConfig.MIN_PLAYERS);
                int maxPlayers = plugin.config().get(ZFConfig.MAX_PLAYERS);
                if (playersInWorld >= minPlayers && playersInWorld < maxPlayers) {
                    plugin.broadcastWorld(world.getName(), messager.getMessage(Language.ENOUGH_FOR_COUNTDOWN_START));
                    game.countdown();
                } else if (world.getPlayers().size() >= maxPlayers) {
                    plugin.broadcastWorld(world.getName(), messager.getMessage(Language.ENOUGH_FOR_QUICK_START));
                    game.forceStart();
                }
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Location playerLoc = player.getLocation();
        World world = playerLoc.getWorld();
        Game game = plugin.getGameManager().getGame(world.getName());
        if (game == null) {
            return;
        }
        switch (game.getStatus()) {
            case STARTING:
                int playersInWorld = world.getPlayers().size();
                int minPlayers = plugin.config().get(ZFConfig.MIN_PLAYERS);
                if (playersInWorld < minPlayers) {
                    plugin.broadcastWorld(world.getName(), messager.getMessage(Language.TOO_FEW_PLAYERS));
                    game.haltCountdown();
                }
                break;
            case IN_PROGRESS:
                if (game.isPlaying(player.getName())) {
                    game.playerQuit(player.getName());
                    if (!game.hasOnlineZombies()) {
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
                                gameEnd(game);
                            }
                        } else {
                            gameEnd(game);
                        }
                    }
                    if (!game.hasOnlineHumans()) {
                        gameEnd(game);
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
}
