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

public class EndGameCommand extends ZFCommand {

    public EndGameCommand(ZombieFight plugin) {
        super(plugin);
        this.setName(getMessager().getMessage(Language.CMD_END_GAME_NAME));
        this.setCommandUsage("/" + plugin.getCommandPrefixes().get(0) + " end [-w <worldname>]");
        this.setArgRange(0, 2);
        for (String prefix : plugin.getCommandPrefixes()) {
            this.addKey(prefix + " end");
        }
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " end");
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " end -w world_nether");
        this.setPermission(Perms.CMD_END.getPermission());
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
        if (game.getStatus() == GameStatus.STARTING || game.getStatus() == GameStatus.PREPARING) {
            getMessager().normal(Language.CMD_END_NOT_STARTED, sender);
            return;
        }

        getMessager().good(Language.CMD_END_SUCCESS, sender);
        game.endGame();
    }
}
