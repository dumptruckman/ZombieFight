/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.locale.Messager;
import com.dumptruckman.minecraft.pluginbase.util.BukkitTools;
import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.zombiefight.util.Perms;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Button;
import org.bukkit.material.Lever;
import org.bukkit.material.Openable;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ZombieFightListener implements Listener {

    private ZombieFight plugin;
    private Map<String, Set<String>> playersPastBorder = new HashMap<String, Set<String>>();
    private int borderTask = -1;
    private Map<Player, Game> playersMoved;

    private class BorderDamagerTask implements Runnable {
        @Override
        public void run() {
            for (String worldBorder : playersPastBorder.keySet()) {
                World world = Bukkit.getWorld(worldBorder);
                if (world == null) {
                    playersPastBorder.get(worldBorder).clear();
                    continue;
                }
                Game game = plugin.getGameManager().getGame(world);
                if (!game.isEnabled() || !game.hasStarted() || game.hasReset()) {
                    playersPastBorder.get(worldBorder).clear();
                    continue;
                }
                Location spawnLoc = plugin.config().get(ZFConfig.GAME_SPAWN.specific(world.getName()));
                if (spawnLoc == null) {
                    spawnLoc = world.getSpawnLocation();
                }
                List<String> playersNotPastBorder = new LinkedList<String>();
                for (String name : playersPastBorder.get(worldBorder)) {
                    Player player = plugin.getServer().getPlayerExact(name);
                    if (player != null) {
                        if (player.isDead()) {
                            playersNotPastBorder.add(name);
                            break;
                        }
                        Location loc = player.getLocation();
                        int radius = plugin.config().get(ZFConfig.BORDER_RADIUS.specific(world.getName()));
                        if (loc.distance(spawnLoc) < radius) {
                            playersNotPastBorder.add(name);
                            break;
                        }
                        int playerHealth = player.getHealth();
                        int newHealth = playerHealth - plugin.config().get(ZFConfig.BORDER_DAMAGE);
                        if (newHealth < 0) {
                            newHealth = 0;
                        }
                        player.setHealth(newHealth);
                    }
                }
                playersPastBorder.get(worldBorder).removeAll(playersNotPastBorder);
            }
        }
    }

    void resetBorderDamager() {
        if (borderTask != -1) {
            Bukkit.getScheduler().cancelTask(borderTask);
        }
        borderTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new BorderDamagerTask(), 0, BukkitTools.convertSecondsToTicks(plugin.config().get(ZFConfig.BORDER_TIME)));
    }

    public ZombieFightListener(ZombieFight plugin) {
        this.plugin = plugin;
        playersMoved = new HashMap<Player, Game>(Bukkit.getMaxPlayers());
    }

    private Messager getMessager() {
        return plugin.getMessager();
    }

    @EventHandler
    public void portalCreate(PortalCreateEvent event) {
        Game game = plugin.getGameManager().getGame(event.getWorld());
        if (!game.isEnabled() || event.getReason() != PortalCreateEvent.CreateReason.FIRE) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void playerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getGame(player.getWorld());
        if (!game.isEnabled()) {
            return;
        }
        if (game.hasStarted() && !game.hasEnded()) {
            if (game.isBanned(player) && !Perms.CAN_ALWAYS_BREAK.hasPermission(player)) {
                event.setKickMessage("Banned for current game for logging out as only zombie!  Check back in a bit.");
                event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getGame(player.getWorld());
        if (!game.isEnabled()) {
            return;
        }
        game.playerJoined(player);
        if (game.hasStarted()) {
            if (game.isZombie(player)) {
                event.setJoinMessage(getMessager().getMessage(Language.PLAYER_JOINED_AS_ZOMBIE, player.getName()));
            } else {
                event.setJoinMessage(getMessager().getMessage(Language.PLAYER_JOINED_AS_HUMAN, player.getName()));
            }
        }
    }

    @EventHandler
    public void blockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getGame(player.getWorld());
        if (!game.isEnabled()) {
            return;
        }
        if (game.hasStarted() && !game.hasEnded()) {
            if (game.isZombie(player)) {
                if (game.isZombieLockPhase()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getGame(player.getWorld());
        if (!game.isEnabled()) {
            return;
        }
        if (game.hasStarted() && !game.hasEnded()) {
            if (game.isZombie(player) && game.isZombieLockPhase()) {
                //event.setCancelled(true);
            }
        } else if (!Perms.CAN_ALWAYS_BREAK.hasPermission(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getGame(player.getWorld());
        if (!game.isEnabled()) {
            return;
        }
        if (game.hasStarted() && !game.hasEnded()) {
            if (game.isZombie(player) && game.isZombieLockPhase()) {
                event.setCancelled(true);
            }
        } else if (!Perms.CAN_ALWAYS_BREAK.hasPermission(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerChangeWorld(PlayerChangedWorldEvent event) {
        Logging.finest(event.getPlayer() + " joined world");
        Player player = event.getPlayer();
        World fromWorld = event.getFrom();
        World toWorld = player.getWorld();
        Game fromGame = plugin.getGameManager().getGame(fromWorld);
        Game toGame = plugin.getGameManager().getGame(toWorld);
        if (fromGame.isEnabled()) {
            fromGame.broadcast(Language.LEAVE_WORLD, player.getName());
            fromGame.playerQuit(player);
        }
        if (toGame.isEnabled()) {
            toGame.broadcast(Language.JOIN_WORLD, player.getName());
            toGame.playerJoined(player);
        }
    }

    @EventHandler
    public void playerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getGame(player.getWorld());
        if (!game.isEnabled()) {
            return;
        }
        if (game.isZombie(player)) {
            if (!event.getItem().getItemStack().getType().isBlock()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerOpenChest(InventoryOpenEvent event) {
        Player player = Bukkit.getPlayerExact(event.getPlayer().getName());
        if (player == null) {
            return;
        }
        Game game = plugin.getGameManager().getGame(player.getWorld());
        if (!game.isEnabled()) {
            return;
        }
        if ((!game.hasStarted() || game.hasEnded()) && !Perms.CAN_ALWAYS_BREAK.hasPermission(player)) {
            event.setCancelled(true);
            return;
        }
        if (game.isZombie(player) && event.getInventory().getType() != InventoryType.PLAYER) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final Game game = plugin.getGameManager().getGame(player.getWorld());
        if (!game.isEnabled()) {
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                game.playerQuit(player);
            }
        }, 2L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerTracking(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!playersMoved.containsKey(player)) {
            return;
        }
        Game game = playersMoved.get(player);
        playersMoved.remove(player);
        if (event.isCancelled()) {
            return;
        }
        game.handleMove(player, event.getTo());
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
        Game game = plugin.getGameManager().getGame(world);
        if (!game.isEnabled()) {
            return;
        }
        playersMoved.put(player, game);
        if (game.isZombie(player) && game.isZombieLockPhase()) {
            if (!(event instanceof PlayerTeleportEvent)) {
                if (!(fromBlock.getX() == toBlock.getX()
                        && fromBlock.getZ() == toBlock.getZ())) {
                    event.setCancelled(true);
                    player.teleport(fromBlock.getLocation());
                    return;
                }
            }
        }
        if (game.hasStarted() && !game.hasReset()) {
            // TODO clean this up
            Location loc = game.getSpawnLocation();
            int radius = plugin.config().get(ZFConfig.BORDER_RADIUS.specific(world.getName()));
            int warnRadius = plugin.config().get(ZFConfig.BORDER_WARN.specific(world.getName()));
            double distance = loc.distance(event.getTo());
            if (distance >= radius) {
                Set<String> players = playersPastBorder.get(world.getName());
                if (players == null) {
                    players = new HashSet<String>();
                    playersPastBorder.put(world.getName(), players);
                }
                players.add(player.getName());
            } else if (distance >= radius - warnRadius) {
                double oldDistance = loc.distance(event.getFrom());
                if (oldDistance < distance) {
                    getMessager().normal(Language.APPROACHING_BORDER, player);
                }
            }
            if (distance < radius) {
                playersPastBorder.remove(player.getName());
            }
        }
    }

    @EventHandler(priority =  EventPriority.HIGHEST)
    public void playerDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player attacker = null;
        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile proj = (Projectile) event.getDamager();
            if (proj.getShooter() instanceof Player) {
                attacker = (Player) proj.getShooter();
            } else {
                return;
            }
        } else {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player victim = (Player) event.getEntity();
        Game game = plugin.getGameManager().getGame(attacker.getWorld());
        if (!game.isEnabled()) {
            return;
        }
        boolean allow = game.allowDamage(attacker, victim);
        if (!allow) {
            event.setCancelled(true);
            return;
        }
        if (game.hasStarted() && !game.hasEnded()) {
            if (plugin.config().get(ZFConfig.ZOMBIE_HUNGER_CHANCE) > 0) {
                if (game.isZombie(victim)) {
                    if (attacker.getItemInHand().getType() == Material.AIR) {
                        poisonChance(attacker);
                    }
                } else {
                    poisonChance(victim);
                }
            }
            if (game.isZombie(attacker)) {
                // TODO Make this better
                ItemStack itemInHand = attacker.getItemInHand();
                if (itemInHand.getType() == Material.IRON_AXE) {
                    event.setDamage(event.getDamage() - 4);
                } else if (itemInHand.getType() == Material.IRON_PICKAXE) {
                    event.setDamage(event.getDamage() - 3);
                }  else if (itemInHand.getType() == Material.IRON_SPADE) {
                    event.setDamage(event.getDamage() - 2);
                }
                event.setDamage(event.getDamage() + plugin.config().get(ZFConfig.ZOMBIE_DAMAGE));
            }
        } else {
            event.setCancelled(true);
        }
    }

    private void poisonChance(Player player) {
        Random rand = new Random(System.currentTimeMillis());
        int chance = rand.nextInt(100);
        if (chance < plugin.config().get(ZFConfig.ZOMBIE_HUNGER_CHANCE)) {
            player.addPotionEffect(PotionEffectType.HUNGER.createEffect((int) BukkitTools.convertSecondsToTicks(plugin.config().get(
                    ZFConfig.ZOMBIE_HUNGER_DURATION)), plugin.config().get(ZFConfig.ZOMBIE_HUNGER_STRENGTH)));
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
        Game game = plugin.getGameManager().getGame(player.getWorld());
        if (!game.isEnabled()) {
            return;
        }
        if (game.hasStarted() && !game.hasReset()) {
            if (game.isZombie(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getGame(player.getWorld());
        if (!game.isEnabled()) {
            return;
        }
        event.setRespawnLocation(game.getSpawnLocation());
        if (game.isZombie(player)) {
            game.addZombieItems(player.getInventory());
        }
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Game game = plugin.getGameManager().getGame(player.getWorld());
        if (!game.isEnabled()) {
            return;
        }
        game.playerDied(player, player.getKiller());
        if (game.isZombie(player)) {
            Iterator<ItemStack> it = event.getDrops().iterator();
            while (it.hasNext()) {
                ItemStack item = it.next();
                if (!item.getType().isBlock()) {
                    it.remove();
                }
            }
        }
    }

    @EventHandler
    public void playerDropItem(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        Game game = plugin.getGameManager().getGame(player.getWorld());
        if (!game.isEnabled()) {
            return;
        }
        if ((!game.hasStarted() || game.hasEnded()) && !Perms.CAN_ALWAYS_BREAK.hasPermission(player)) {
            event.setCancelled(true);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    player.updateInventory();
                }
            }, 1L);
            return;
        }
        if (game.isZombie(player)) {
            if (!event.getItemDrop().getItemStack().getType().isBlock()) {
                event.setCancelled(true);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        player.updateInventory();
                    }
                }, 1L);
            }
        }
    }

    @EventHandler
    public void lightningFire(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void abilityUse(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }
        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getGame(player.getWorld());
        if (!game.isEnabled()) {
            return;
        }
        Block clicked = event.getClickedBlock();
        if (clicked != null
                && (clicked instanceof Openable || clicked instanceof InventoryHolder
                || clicked instanceof Lever || clicked instanceof Button)) {
            return;
        }
        if (event.getAction() == Action.LEFT_CLICK_BLOCK
                || event.getAction() == Action.LEFT_CLICK_AIR) {
            game.leftClickAbilityUse(player, event.getItem());
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK
                || event.getAction() == Action.RIGHT_CLICK_AIR) {
            game.rightClickAbilityUse(player, event.getItem());
        }
    }
}
