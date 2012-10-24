/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.command;

import com.dumptruckman.minecraft.pluginbase.entity.BasePlayer;
import com.dumptruckman.minecraft.pluginbase.locale.Message;
import com.dumptruckman.minecraft.pluginbase.permission.Perm;
import com.dumptruckman.minecraft.pluginbase.plugin.command.CommandInfo;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.zombiefight.util.Perms;
import com.sk89q.minecraft.util.commands.CommandContext;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

@CommandInfo(
        primaryAlias = "end",
        desc = "Ends the game.",
        flags = "w:f"
)
public class EndGameCommand extends ZFCommand {

    @Override
    public Perm getPerm() {
        return Perms.CMD_END;
    }

    @Override
    public Message getHelp() {
        return Language.CMD_END_GAME_HELP;
    }

    @Override
    public void runCommand(ZombieFight p, BasePlayer sender, CommandContext context) {
        String worldName = context.getFlag('w');
        World world;
        if (worldName == null) {
            if (!sender.isPlayer()) {
                p.getMessager().bad(sender, Language.CMD_CONSOLE_REQUIRES_WORLD);
                return;
            } else {
                world = ((Player) sender).getWorld();
            }
        } else {
            world = Bukkit.getWorld(worldName);
        }
        if (world == null) {
            p.getMessager().bad(sender, Language.NO_WORLD, worldName);
            return;
        }
        Game game = p.getGameManager().getGame(world);
        if (!game.isEnabled()) {
            p.getMessager().bad(sender, Language.NOT_GAME_WORLD, world.getName());
            return;
        }
        if (context.hasFlag('f')) {
            if (!game.forceEnd(true)) {
                p.getMessager().normal(sender, Language.CMD_END_ALREADY_ENDED);
                return;
            }
        } else {
            if (!game.end()) {
                p.getMessager().normal(sender, Language.CMD_END_ALREADY_ENDED);
                return;
            }
        }
        game.broadcast(Language.CMD_END_BROADCAST, sender.getName());

        p.getMessager().good(sender, Language.CMD_END_SUCCESS);
    }
}
