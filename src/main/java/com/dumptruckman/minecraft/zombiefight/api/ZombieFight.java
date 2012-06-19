package com.dumptruckman.minecraft.zombiefight.api;

import com.dumptruckman.minecraft.pluginbase.plugin.BukkitPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

/**
 * The main interface of the ZombieFight plugin.  You can use this for casting Plugin when got from Bukkit's
 * plugin manager.
 */
public interface ZombieFight extends BukkitPlugin<ZFConfig>, Plugin {

    GameManager getGameManager();

    void broadcastWorld(String worldName, String message);

    Collection<String> getPlayersForWorld(String worldName);

    void zombifyPlayer(String name);

    void unZombifyPlayer(String name);

    LootConfig getLootConfig();

    boolean shouldWarn(int time);
}
