package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GamePlayer;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import me.desmin88.mobdisguise.api.MobDisguiseAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

class DefaultGamePlayer implements GamePlayer {

    private ZombieFight plugin;
    private Game game;

    private String name;

    boolean online = false;
    boolean zombie = false;

    DefaultGamePlayer(String name, ZombieFight plugin, Game game) {
        this.name = name;
        this.plugin = plugin;
        this.game = game;
        Player player = getPlayer();
        if (player != null && player.getWorld().equals(game.getWorld())) {
            online = true;
        }
    }

    private void fixUpPlayer(Player player) {
        player.setDisplayName(ChatColor.stripColor(player.getDisplayName()));
        if (isZombie()) {
            if (!MobDisguiseAPI.isDisguised(player)) {
                MobDisguiseAPI.disguisePlayer(player, "zombie");
            }
            player.setDisplayName(plugin.getMessager().getMessage(Language.ZOMBIE_NAME, player.getDisplayName()));
        } else {
            if (MobDisguiseAPI.isDisguised(player)) {
                MobDisguiseAPI.undisguisePlayer(player);
            }
            player.setDisplayName(plugin.getMessager().getMessage(Language.HUMAN_NAME, player.getDisplayName()));
        }
    }

    @Override
    public final Player getPlayer() {
        return Bukkit.getPlayerExact(name);
    }

    @Override
    public boolean isZombie() {
        return zombie;
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
            if (MobDisguiseAPI.isDisguised(player)) {
                MobDisguiseAPI.undisguisePlayer(player);
            }
        }
    }

    @Override
    public void makeZombie() {
        zombie = true;
        Player player = getPlayer();
        if (player != null) {
            plugin.broadcastWorld(game.getWorld().getName(), plugin.getMessager().getMessage(Language.PLAYER_ZOMBIFIED, player.getName()));
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(5F);
            player.setExhaustion(0F);
            fixUpPlayer(player);
            plugin.getMessager().normal(Language.YOU_ARE_ZOMBIE, player);
        }
    }

    @Override
    public void makeHuman() {
        zombie = false;
        Player player = getPlayer();
        if (player != null) {
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(5F);
            player.setExhaustion(0F);
            fixUpPlayer(player);
        }
    }
}
