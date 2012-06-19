package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import net.minecraft.server.EntityPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TestModeListener implements Listener {

    private ZombieFight plugin;

    TestModeListener(ZombieFight plugin) {
        this.plugin = plugin;
    }

    // Stuff for testing
    private String[] names = new String[30];
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
        if (!plugin.config().get(ZFConfig.TEST_MODE)) {
            return;
        }
        Player player = event.getPlayer();
        for (int i = 0; i < names.length; i++) {
            if (names[i] != null && names[i].equals(player.getName())) {
                names[i] = null;
            }
        }
    }
}
