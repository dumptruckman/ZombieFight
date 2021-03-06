/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.command;

import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.zombiefight.util.Perms;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PreGameSpawnCommand extends ZFCommand {

    public PreGameSpawnCommand(ZombieFight plugin) {
        super(plugin);
        this.setName(getMessager().getMessage(Language.CMD_PGSPAWN_NAME));
        this.setCommandUsage("/" + plugin.getCommandPrefixes().get(0) + " pgspawn [set]");
        this.setArgRange(0, 1);
        for (String prefix : plugin.getCommandPrefixes()) {
            this.addKey(prefix + " pgspawn");
        }
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " pgspawn");
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " pgspawn set");
        this.setPermission(Perms.CMD_PGSPAWN.getPermission());
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            getMessager().bad(Language.IN_GAME_ONLY, sender);
            return;
        }
        Player player = (Player) sender;
        if (!args.isEmpty() && args.get(0).equalsIgnoreCase("set")) {
            if (Perms.CMD_PGSPAWN_SET.hasPermission(player)) {
                Location loc = player.getLocation();
                plugin.config().set(ZFConfig.PRE_GAME_SPAWN.specific(loc.getWorld().getName()), loc);
                plugin.config().save();
                getMessager().good(Language.CMD_PGSPAWN_SET_SUCCESS, player);
            } else {
                getMessager().bad(Language.CMD_PGSPAWN_SET_NO_PERM, player);
            }
        } else {
            Location loc = plugin.config().get(ZFConfig.PRE_GAME_SPAWN.specific(player.getWorld().getName()));
            if (loc != null) {
                player.teleport(loc);
            } else {
                getMessager().bad(Language.CMD_PGSPAWN_FAIL, player);
            }
        }
    }
}
