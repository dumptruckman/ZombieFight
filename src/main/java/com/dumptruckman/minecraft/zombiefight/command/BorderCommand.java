/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.command;

import com.dumptruckman.minecraft.pluginbase.entity.BasePlayer;
import com.dumptruckman.minecraft.pluginbase.locale.Message;
import com.dumptruckman.minecraft.pluginbase.permission.Perm;
import com.dumptruckman.minecraft.pluginbase.plugin.command.CommandInfo;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.zombiefight.util.Perms;
import com.sk89q.minecraft.util.commands.CommandContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandInfo(
        primaryAlias = "border",
        desc = "Checks or sets a world's game border size.",
        usage = "[radius]",
        max = 1
)
public class BorderCommand extends ZFCommand {

    @Override
    public Perm getPerm() {
        return Perms.CMD_BORDER;
    }

    @Override
    public Message getHelp() {
        return Language.CMD_BORDER_HELP;
    }

    @Override
    public void runCommand(ZombieFight p, BasePlayer sender, CommandContext context) {
        if (!sender.isPlayer()) {
            p.getMessager().bad(sender, Language.IN_GAME_ONLY);
            return;
        }
        Player player = Bukkit.getServer().getPlayerExact(sender.getName());
        if (context.argsLength() != 0) {
            try {
                int radius = context.getInteger(0);
                p.config().set(ZFConfig.BORDER_RADIUS, player.getWorld().getName(), radius);
                p.config().save();
                p.getMessager().good(sender, Language.CMD_BORDER_SET_SUCCESS, radius);
            } catch (NumberFormatException ignore) {
                p.getMessager().bad(sender, Language.CMD_BORDER_SET_FAILURE);
            }
        } else {
            p.getMessager().normal(sender, Language.CMD_BORDER_CHECK, p.config().get(ZFConfig.BORDER_RADIUS, player.getWorld().getName()));
        }
    }
}
