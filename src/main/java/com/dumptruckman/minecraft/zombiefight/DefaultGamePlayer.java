/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GamePlayer;
import com.dumptruckman.minecraft.zombiefight.api.PlayerType;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import pgDev.bukkit.DisguiseCraft.Disguise;
import pgDev.bukkit.DisguiseCraft.Disguise.MobType;
import pgDev.bukkit.DisguiseCraft.DisguiseCraft;

class DefaultGamePlayer implements GamePlayer {

    private final ZombieFight plugin;
    private final Game game;
    private final String name;
    private final DBInfo dbInfo;

    private boolean online = false;

    private PlayerType playerType = PlayerType.HUMAN;

    private String trackedPlayer = "";

    DefaultGamePlayer(String name, ZombieFight plugin, Game game) {
        this.name = name;
        this.plugin = plugin;
        this.game = game;
        this.dbInfo = new DBInfo();
        Player player = getPlayer();
        if (player != null && player.getWorld().equals(game.getWorld())) {
            online = true;
        }
        plugin.getStats().setupPlayer(this);
    }

    private void fixUpPlayer(Player player) {
        player.setDisplayName(ChatColor.stripColor(player.getDisplayName()));
        player.setPlayerListName(ChatColor.stripColor(player.getPlayerListName()));
        if (isZombie()) {
            if (!DisguiseCraft.getAPI().isDisguised(player)) {
                DisguiseCraft.getAPI().disguisePlayer(player, new Disguise(DisguiseCraft.getAPI().newEntityID(), MobType.Zombie));
            }
            /*if (!plugin.getDisguiser().isDisguised(player)) {
                plugin.getDisguiser().disguise(player, EntityType.ZOMBIE);
            }*/
            String name = plugin.getMessager().getMessage(Language.ZOMBIE_NAME, player.getDisplayName());
            player.setDisplayName(name + ChatColor.WHITE);
            int length = name.length();
            if (length > 16) {
                length = 15;
            }
            player.setPlayerListName(name.substring(0, length));
            game.addZombieItems(player.getInventory());
            //player.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(Integer.MAX_VALUE, plugin.config().get(ZFConfig.ZOMBIE_BREAK_SPEED_STRENGTH)));
        } else {
            if (DisguiseCraft.getAPI().isDisguised(player)) {
                DisguiseCraft.getAPI().undisguisePlayer(player);
            }
            /*if (plugin.getDisguiser().isDisguised(player)) {
                plugin.getDisguiser().undisguise(player);
            }*/
            String name = plugin.getMessager().getMessage(Language.HUMAN_NAME, player.getDisplayName());
            player.setDisplayName(name + ChatColor.WHITE);
            int length = name.length();
            if (length > 16) {
                length = 15;
            }
            player.setPlayerListName(name.substring(0, length));
        }
    }

    @Override
    public DBInfo getDBInfo() {
        return dbInfo;
    }

    @Override
    public final Player getPlayer() {
        return Bukkit.getPlayerExact(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isZombie() {
        return playerType != PlayerType.HUMAN;
    }

    @Override
    public PlayerType getType() {
        return playerType;
    }

    @Override
    public boolean isOnline() {
        if (online && getPlayer() == null) {
            leftGame();
        }
        return online;
    }

    @Override
    public void joinedGame() {
        Player player = getPlayer();
        if (player == null) {
            Logging.fine("Offline player '" + name + "' somehow joined game!");
            online = false;
            return;
        }
        online = true;
        fixUpPlayer(player);
    }

    @Override
    public void leftGame() {
        online = false;
        Player player = getPlayer();
        if (player != null) {
            player.setDisplayName(ChatColor.stripColor(player.getDisplayName()));
            if (DisguiseCraft.getAPI().isDisguised(player)) {
                DisguiseCraft.getAPI().undisguisePlayer(player);
            }
        }
    }

    @Override
    public void makeZombie(final boolean broadcast) {
        if (isZombie()) {
            return;
        }
        playerType = PlayerType.ZOMBIE;
        final Player player = getPlayer();
        if (player != null) {
            resetPlayer(player);
            fixUpPlayer(player);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if (broadcast) {
                        plugin.broadcastWorld(game.getWorld().getName(), plugin.getMessager().getMessage(Language.PLAYER_ZOMBIFIED, player.getName()));
                    }
                    plugin.getMessager().normal(Language.YOU_ARE_ZOMBIE, player);
                }
            }, 3L);
        }
        plugin.getStats().playerUpdate(this);
        if (game.hasStarted() && !game.hasEnded()) {
            plugin.getStats().playerTypeChange(game, this, PlayerType.ZOMBIE);
        }
    }

    @Override
    public void makeHuman() {
        playerType = PlayerType.HUMAN;
        Player player = getPlayer();
        if (player != null) {
            resetPlayer(player);
            fixUpPlayer(player);
        }
        plugin.getStats().playerUpdate(this);
        if (game.hasStarted() && !game.hasEnded()) {
            plugin.getStats().playerTypeChange(game, this, PlayerType.HUMAN);
        }
    }

    private void resetPlayer(final Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (int i = 0; i < armor.length; i++) {
            armor[i] = new ItemStack(Material.AIR);
        }
        player.getInventory().setArmorContents(armor);
        player.getInventory().clear();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(5F);
        player.setExhaustion(0F);
        player.closeInventory();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    @Override
    public Player getTrackedPlayer() {
        return Bukkit.getPlayerExact(trackedPlayer);
    }

    @Override
    public void setTrackedPlayer(final Player player) {
        trackedPlayer = player.getName();
    }

    @Override
    public boolean isTracking(final Player player) {
        return player.getName().equals(trackedPlayer);
    }
}
