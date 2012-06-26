package com.dumptruckman.minecraft.zombiefight.command;

import com.dumptruckman.minecraft.pluginbase.plugin.command.PluginCommand;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;

/**
 * An abstract ZombieFight command.
 */
public abstract class ZFCommand extends PluginCommand<ZombieFight> {

    public ZFCommand(ZombieFight plugin) {
        super(plugin);
    }
}
