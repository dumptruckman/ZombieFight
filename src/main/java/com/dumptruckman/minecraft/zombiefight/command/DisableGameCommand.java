/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.command;

import com.dumptruckman.minecraft.pluginbase.util.commandhandler.CommandHandler;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.zombiefight.util.Perms;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DisableGameCommand extends ZFCommand {

    public DisableGameCommand(ZombieFight plugin) {
        super(plugin);
        this.setName(getMessager().getMessage(Language.CMD_DISABLE_NAME));
        this.setCommandUsage("/" + plugin.getCommandPrefixes().get(0) + " disable [-w <worldname>]");
        this.setArgRange(0, 2);
        for (String prefix : plugin.getCommandPrefixes()) {
            this.addKey(prefix + " disable");
        }
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " disable");
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " disable -w world_nether");
        this.setPermission(Perms.CMD_DISABLE.getPermission());
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String worldName = CommandHandler.getFlag("-w", args);
        World world;
        if (worldName == null) {
            if (!(sender instanceof Player)) {
                getMessager().bad(Language.CMD_CONSOLE_REQUIRES_WORLD, sender);
                return;
            } else {
                world = ((Player) sender).getWorld();
            }
        } else {
            world = Bukkit.getWorld(worldName);
        }
        if (world == null) {
            getMessager().bad(Language.NO_WORLD, sender, worldName);
            return;
        }
        if (!plugin.getGameManager().isWorldEnabled(world)) {
            getMessager().bad(Language.CMD_DISABLE_ALREADY, sender);
            return;
        }
        plugin.getGameManager().disableWorld(world);
        getMessager().good(Language.CMD_DISABLE_SUCCESS, sender);
    }
}
