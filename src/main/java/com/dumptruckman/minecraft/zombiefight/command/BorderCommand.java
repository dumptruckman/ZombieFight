/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.command;

import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.zombiefight.util.Perms;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BorderCommand extends ZFCommand {

    public BorderCommand(ZombieFight plugin) {
        super(plugin);
        this.setName(getMessager().getMessage(Language.CMD_BORDER_NAME));
        this.setCommandUsage("/" + plugin.getCommandPrefixes().get(0) + " border [value]");
        this.setArgRange(0, 1);
        for (String prefix : plugin.getCommandPrefixes()) {
            this.addKey(prefix + " border");
        }
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " border");
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " border 150");
        this.setPermission(Perms.CMD_BORDER.getPermission());
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            getMessager().bad(Language.IN_GAME_ONLY, sender);
            return;
        }
        Player player = (Player) sender;
        if (!args.isEmpty()) {
            try {
                int radius = Integer.valueOf(args.get(0));
                plugin.config().set(ZFConfig.BORDER_RADIUS, player.getWorld().getName(), radius);
                plugin.config().save();
                getMessager().good(Language.CMD_BORDER_SET_SUCCESS, player, radius);
            } catch (NumberFormatException ignore) {
                getMessager().bad(Language.CMD_BORDER_SET_FAILURE, player);
            }
        } else {
            getMessager().normal(Language.CMD_BORDER_CHECK, sender, plugin.config().get(ZFConfig.BORDER_RADIUS, player.getWorld().getName()));
        }
    }
}
