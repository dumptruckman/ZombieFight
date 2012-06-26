package com.dumptruckman.minecraft.zombiefight.command;

import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.zombiefight.util.Perms;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CleanupCommand extends ZFCommand {

    public CleanupCommand(ZombieFight plugin) {
        super(plugin);
        this.setName(getMessager().getMessage(Language.CMD_CLEANUP_NAME));
        this.setCommandUsage("/" + plugin.getCommandPrefixes().get(0) + " cleanup");
        this.setArgRange(0, 0);
        for (String prefix : plugin.getCommandPrefixes()) {
            this.addKey(prefix + " cleanup");
        }
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " cleanup");
        this.setPermission(Perms.CMD_ENABLE.getPermission());
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            getMessager().bad(Language.IN_GAME_ONLY, sender);
            return;
        }
        Player player = (Player) sender;
        if (plugin.isCleaner(player)) {
            plugin.setCleaner(player, false);
            getMessager().good(Language.CMD_CLEANUP_DISABLE, player);
        } else {
            plugin.setCleaner(player, true);
            getMessager().good(Language.CMD_CLEANUP_ENABLE, player);
        }
    }
}
