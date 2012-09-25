/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.command;

import com.dumptruckman.minecraft.pluginbase.util.commandhandler.CommandHandler;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.zombiefight.util.Perms;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class EndGameCommand extends ZFCommand {

    public EndGameCommand(ZombieFight plugin) {
        super(plugin);
        this.setName(getMessager().getMessage(Language.CMD_END_GAME_NAME));
        this.setCommandUsage("/" + plugin.getCommandPrefixes().get(0) + " end [-w <worldname>] [-f]");
        this.setArgRange(0, 3);
        for (String prefix : plugin.getCommandPrefixes()) {
            this.addKey(prefix + " end");
        }
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " end");
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " end -w world_nether");
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " end -f");
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " end -w world_nether -f");
        this.setPermission(Perms.CMD_END.getPermission());
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
        Game game = plugin.getGameManager().getGame(world);
        if (!game.isEnabled()) {
            getMessager().bad(Language.NOT_GAME_WORLD, sender, world.getName());
            return;
        }
        if (CommandHandler.hasFlag("-f", args)) {
            if (!game.forceEnd(true)) {
                getMessager().normal(Language.CMD_END_ALREADY_ENDED, sender);
                return;
            }
        } else {
            if (!game.end()) {
                getMessager().normal(Language.CMD_END_ALREADY_ENDED, sender);
                return;
            }
        }
        game.broadcast(Language.CMD_END_BROADCAST, sender.getName());

        getMessager().good(Language.CMD_END_SUCCESS, sender);
    }
}
