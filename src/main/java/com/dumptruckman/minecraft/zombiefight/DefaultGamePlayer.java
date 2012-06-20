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

    private String name;

    boolean online = false;
    boolean zombie = false;

    DefaultGamePlayer(String name, ZombieFight plugin, Game game) {
        this.name = name;
        this.plugin = plugin;
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
            fixUpPlayer(player);
        }
    }
}
