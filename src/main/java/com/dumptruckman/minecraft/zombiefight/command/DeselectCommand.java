/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.command;

import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.zombiefight.util.Perms;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DeselectCommand extends ZFCommand {

    public DeselectCommand(ZombieFight plugin) {
        super(plugin);
        this.setName(getMessager().getMessage(Language.CMD_DESELECT_NAME));
        this.setCommandUsage("/" + plugin.getCommandPrefixes().get(0) + " deselect");
        this.setArgRange(0, 0);
        for (String prefix : plugin.getCommandPrefixes()) {
            this.addKey(prefix + " deselect");
        }
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " deselect");
        this.setPermission(Perms.CMD_DESELECT.getPermission());
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (plugin.getGameManager().getPrimaryGame() == null) {
            getMessager().bad(Language.CMD_DESELECT_NONE_SELECTED, sender);
            return;
        }
        plugin.getGameManager().setPrimaryGame(null);
        getMessager().good(Language.CMD_DESELECT_SUCCESS, sender);
    }
}
