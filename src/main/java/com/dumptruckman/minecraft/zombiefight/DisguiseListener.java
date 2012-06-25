package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DisguiseListener implements Listener {

    ZombieFight plugin;

    DisguiseListener(ZombieFight plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (plugin.getDisguiser().isDisguised(player)) {
            // Should fix the "carcass" mob when disguised
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    plugin.getDisguiser().handleQuit(player);
                }
            }, 5L);

        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (plugin.getDisguiser().isDisguised(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (plugin.getDisguiser().isDisguised(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
