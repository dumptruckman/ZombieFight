/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.command;

import com.dumptruckman.minecraft.pluginbase.util.commandhandler.CommandHandler;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.zombiefight.util.Perms;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class EnableGameCommand extends ZFCommand {

    public EnableGameCommand(ZombieFight plugin) {
        super(plugin);
        this.setName(getMessager().getMessage(Language.CMD_ENABLE_NAME));
        this.setCommandUsage("/" + plugin.getCommandPrefixes().get(0) + " enable [-w <worldname>]");
        this.setArgRange(0, 2);
        for (String prefix : plugin.getCommandPrefixes()) {
            this.addKey(prefix + " enable");
        }
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " enable");
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " enable -w world_nether");
        this.setPermission(Perms.CMD_ENABLE.getPermission());
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String worldName = CommandHandler.getFlag("-w", args);
        World world = null;
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
        if (plugin.getGameManager().isWorldEnabled(world)) {
            getMessager().bad(Language.CMD_ENABLE_ALREADY, sender);
            return;
        }
        int borderRadius = plugin.config().get(ZFConfig.BORDER_RADIUS.specific(world.getName()));
        plugin.config().set(ZFConfig.BORDER_RADIUS.specific(world.getName()), borderRadius);
        int borderWarn = plugin.config().get(ZFConfig.BORDER_WARN.specific(world.getName()));
        plugin.config().set(ZFConfig.BORDER_WARN.specific(world.getName()), borderWarn);
        plugin.getGameManager().enableWorld(world);
        getMessager().good(Language.CMD_ENABLE_SUCCESS, sender);
    }
}
