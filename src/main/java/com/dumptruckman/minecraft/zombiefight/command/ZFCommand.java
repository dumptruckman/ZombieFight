/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.command;

import com.dumptruckman.minecraft.pluginbase.entity.BasePlayer;
import com.dumptruckman.minecraft.pluginbase.locale.Message;
import com.dumptruckman.minecraft.pluginbase.permission.Perm;
import com.dumptruckman.minecraft.pluginbase.plugin.command.Command;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.sk89q.minecraft.util.commands.CommandContext;

/**
 * An abstract ZombieFight command.
 */
public abstract class ZFCommand extends Command<ZombieFight> {

    @Override
    public abstract Perm getPerm();

    @Override
    public abstract Message getHelp();

    @Override
    public abstract void runCommand(ZombieFight p, BasePlayer sender, CommandContext context);
}
