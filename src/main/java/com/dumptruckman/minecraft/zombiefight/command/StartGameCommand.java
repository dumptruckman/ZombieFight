package com.dumptruckman.minecraft.zombiefight.command;

import com.dumptruckman.minecraft.pluginbase.util.commandhandler.CommandHandler;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GameStatus;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.zombiefight.util.Perms;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StartGameCommand extends ZFCommand {

    public StartGameCommand(ZombieFight plugin) {
        super(plugin);
        this.setName(getMessager().getMessage(Language.CMD_START_GAME_NAME));
        this.setCommandUsage("/" + plugin.getCommandPrefixes().get(0) + " start [-f] [-w <worldname>]");
        this.setArgRange(0, 3);
        for (String prefix : plugin.getCommandPrefixes()) {
            this.addKey(prefix + " start");
        }
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " start");
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " start -f");
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " start -w world_nether");
        this.setPermission(Perms.CMD_START.getPermission());
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
        Game game = plugin.getGameManager().getGame(world.getName());
        if (game == null) {
            getMessager().bad(Language.NOT_GAME_WORLD, sender, world.getName());
            return;
        }
        if (game.getStatus() == GameStatus.IN_PROGRESS || game.getStatus() == GameStatus.ENDED) {
            getMessager().normal(Language.CMD_START_ALREADY_STARTED, sender);
            return;
        }

        if (CommandHandler.hasFlag("-f", args)) {
            getMessager().good(Language.CMD_START_FORCE_SUCCESS, sender);
            game.forceStart();
        } else {
            getMessager().good(Language.CMD_START_SUCCESS, sender);
            game.countdown();
        }
    }
}
