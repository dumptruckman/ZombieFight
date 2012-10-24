/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.command;

import com.dumptruckman.minecraft.pluginbase.entity.BasePlayer;
import com.dumptruckman.minecraft.pluginbase.locale.Message;
import com.dumptruckman.minecraft.pluginbase.permission.Perm;
import com.dumptruckman.minecraft.pluginbase.plugin.command.CommandInfo;
import com.dumptruckman.minecraft.zombiefight.api.LootTable;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.zombiefight.util.Perms;
import com.sk89q.minecraft.util.commands.CommandContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandInfo(
        primaryAlias = "kit",
        aliases = "kit",
        desc = "Lists kits available to you or selects a kit for the next game.",
        usage = "[name of kit]"
)
public class KitCommand extends ZFCommand {

    @Override
    public Perm getPerm() {
        return Perms.KIT;
    }

    @Override
    public Message getHelp() {
        return Language.CMD_KIT_HELP;
    }

    @Override
    public void runCommand(ZombieFight p, BasePlayer sender, CommandContext context) {
        if (!sender.isPlayer()) {
            p.getMessager().bad(sender, Language.IN_GAME_ONLY);
            return;
        }
        Player player = Bukkit.getPlayerExact(sender.getName());
        if (context.argsLength() == 0) {
            p.displayKits(player);
        } else {
            final String kit = context.getJoinedStrings(0);
            if (!Perms.KIT.hasPermission(sender, kit)) {
                p.getMessager().bad(sender, Language.CMD_KIT_NO_ACCESS, kit);
                return;
            }
            LootTable lootTable = p.getLootConfig().getKit(kit);
            if (lootTable == null) {
                p.getMessager().bad(sender, Language.KIT_ERROR, kit);
                return;
            }
            p.setPlayerKit(player.getName(), kit);
            p.getMessager().good(sender, Language.CMD_KIT_SUCCESS, kit);
        }
    }
}
