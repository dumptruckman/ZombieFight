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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandInfo(
        primaryAlias = "cleanup",
        desc = "Allows user to clean up during a game."
)
public class CleanupCommand extends ZFCommand {

    @Override
    public Perm getPerm() {
        return Perms.CMD_ENABLE;
    }

    @Override
    public Message getHelp() {
        return Language.CMD_CLEANUP_HELP;
    }

    @Override
    public void runCommand(ZombieFight p, BasePlayer sender, CommandContext context) {
        if (!sender.isPlayer()) {
            p.getMessager().bad(sender, Language.IN_GAME_ONLY);
            return;
        }
        Player player = Bukkit.getPlayerExact(sender.getName());
        if (p.isCleaner(player)) {
            p.setCleaner(player, false);
            p.getMessager().good(sender, Language.CMD_CLEANUP_DISABLE);
        } else {
            p.setCleaner(player, true);
            p.getMessager().good(sender, Language.CMD_CLEANUP_ENABLE);
        }
    }
}
