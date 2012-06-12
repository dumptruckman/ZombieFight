package com.dumptruckman.minecraft.zombiefight.api;

import com.dumptruckman.minecraft.pluginbase.plugin.BukkitPlugin;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * The main interface of the ZombieFight plugin.  You can use this for casting Plugin when got from Bukkit's
 * plugin manager.
 */
public interface ZombieFight extends BukkitPlugin<ZFConfig>, Plugin {


}
