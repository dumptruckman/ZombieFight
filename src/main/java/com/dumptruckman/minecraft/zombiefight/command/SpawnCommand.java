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
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandInfo(
        primaryAlias = "spawn",
        desc = "Warps you to the game spawn or sets one.",
        flags = "s"
)
public class SpawnCommand extends ZFCommand {

    @Override
    public Perm getPerm() {
        return Perms.CMD_GSPAWN;
    }

    @Override
    public Message getHelp() {
        return Language.CMD_GSPAWN_HELP;
    }

    @Override
    public void runCommand(ZombieFight p, BasePlayer sender, CommandContext context) {
        if (!sender.isPlayer()) {
            p.getMessager().bad(sender, Language.IN_GAME_ONLY);
            return;
        }
        Player player = Bukkit.getPlayerExact(sender.getName());
        if (context.hasFlag('s')) {
            if (Perms.CMD_GSPAWN_SET.hasPermission(sender)) {
                Location loc = player.getLocation();
                p.config().set(ZFConfig.GAME_SPAWN, loc.getWorld().getName(), loc);
                p.config().save();
                p.getMessager().good(sender, Language.CMD_GSPAWN_SET_SUCCESS);
            } else {
                p.getMessager().bad(sender, Language.CMD_GSPAWN_SET_NO_PERM);
            }
        } else {
            Location loc = p.config().get(ZFConfig.GAME_SPAWN, player.getWorld().getName());
            if (loc != null) {
                player.teleport(loc);
            } else {
                p.getMessager().bad(sender, Language.CMD_GSPAWN_FAIL);
            }
        }
    }
}
