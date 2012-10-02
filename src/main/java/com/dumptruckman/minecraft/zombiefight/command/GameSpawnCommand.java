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

public class GameSpawnCommand extends ZFCommand {

    public GameSpawnCommand(ZombieFight plugin) {
        super(plugin);
        this.setName(getMessager().getMessage(Language.CMD_GSPAWN_NAME));
        this.setCommandUsage("/" + plugin.getCommandPrefixes().get(0) + " gspawn [set]");
        this.setArgRange(0, 1);
        for (String prefix : plugin.getCommandPrefixes()) {
            this.addKey(prefix + " gspawn");
        }
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " gspawn");
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " gspawn set");
        this.setPermission(Perms.CMD_GSPAWN.getPermission());
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            getMessager().bad(Language.IN_GAME_ONLY, sender);
            return;
        }
        Player player = (Player) sender;
        if (!args.isEmpty() && args.get(0).equalsIgnoreCase("set")) {
            if (Perms.CMD_GSPAWN_SET.hasPermission(player)) {
                Location loc = player.getLocation();
                plugin.config().set(ZFConfig.GAME_SPAWN, loc.getWorld().getName(), loc);
                plugin.config().save();
                getMessager().good(Language.CMD_GSPAWN_SET_SUCCESS, player);
            } else {
                getMessager().bad(Language.CMD_GSPAWN_SET_NO_PERM, player);
            }
        } else {
            Location loc = plugin.config().get(ZFConfig.GAME_SPAWN, player.getWorld().getName());
            if (loc != null) {
                player.teleport(loc);
            } else {
                getMessager().bad(Language.CMD_GSPAWN_FAIL, player);
            }
        }
    }
}
