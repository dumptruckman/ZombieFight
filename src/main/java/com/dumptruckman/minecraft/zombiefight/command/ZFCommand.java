/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
