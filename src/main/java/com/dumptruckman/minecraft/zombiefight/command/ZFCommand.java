package com.dumptruckman.minecraft.zombiefight.command;

import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.pluginbase.plugin.command.PluginCommand;

/**
 * An abstract ZombieFight command.
 */
public abstract class ZFCommand extends PluginCommand<ZombieFight> {

    public ZFCommand(ZombieFight plugin) {
        super(plugin);
    }
}
