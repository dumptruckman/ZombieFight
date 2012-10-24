/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.command;

import com.dumptruckman.minecraft.pluginbase.entity.BasePlayer;
import com.dumptruckman.minecraft.pluginbase.locale.Message;
import com.dumptruckman.minecraft.pluginbase.permission.Perm;
import com.dumptruckman.minecraft.pluginbase.plugin.command.CommandInfo;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.zombiefight.util.Perms;
import com.sk89q.minecraft.util.commands.CommandContext;

@CommandInfo(
        primaryAlias = "deselect",
        desc = "Disables primary game selection."
)
public class DeselectCommand extends ZFCommand {

    @Override
    public Perm getPerm() {
        return Perms.CMD_DESELECT;
    }

    @Override
    public Message getHelp() {
        return Language.CMD_DESELECT_HELP;
    }

    @Override
    public void runCommand(ZombieFight p, BasePlayer sender, CommandContext context) {
        if (p.getGameManager().getPrimaryGame() == null) {
            p.getMessager().bad(sender, Language.CMD_DESELECT_NONE_SELECTED);
            return;
        }
        p.getGameManager().setPrimaryGame(null);
        p.getMessager().good(sender, Language.CMD_DESELECT_SUCCESS);
    }
}
