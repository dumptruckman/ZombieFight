package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.pluginbase.locale.Messager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ZombieFightListener implements Listener {

    private ZombieFight plugin;
    private Messager messager;

    public ZombieFightListener(ZombieFight plugin) {
        this.plugin = plugin;
        this.messager = plugin.getMessager();
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        event.
    }
}
