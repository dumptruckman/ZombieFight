package com.dumptruckman.minecraft.zombiefight.command;

import com.dumptruckman.minecraft.pluginbase.util.commandhandler.CommandHandler;
import com.dumptruckman.minecraft.zombiefight.api.LootTable;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.zombiefight.util.Perms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class KitCommand extends ZFCommand {

    public KitCommand(ZombieFight plugin) {
        super(plugin);
        this.setName(getMessager().getMessage(Language.CMD_KIT_NAME));
        this.setCommandUsage("/kit [kit name]");
        this.setArgRange(0, 500);
        this.addKey("kit");
        this.addCommandExample("/kit");
        this.addCommandExample("/kit starter kit");
        this.setPermission(Perms.KIT.getPermission());
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            getMessager().bad(Language.IN_GAME_ONLY, sender);
            return;
        }
        Player player = (Player) sender;
        if (args.size() == 0) {
            plugin.displayKits(player);
        } else {
            StringBuilder kit = new StringBuilder();
            for (String arg : args) {
                if (!kit.toString().isEmpty()) {
                    kit.append(" ");
                }
                kit.append(arg);
            }
            if (!Perms.KIT.specific(kit.toString()).hasPermission(player)) {
                getMessager().bad(Language.CMD_KIT_NO_ACCESS, player, kit.toString());
                return;
            }
            LootTable lootTable = plugin.getLootConfig().getKit(kit.toString());
            if (lootTable == null) {
                getMessager().bad(Language.KIT_ERROR, player, kit.toString());
                return;
            }
            plugin.setPlayerKit(player.getName(), kit.toString());
            getMessager().good(Language.CMD_KIT_SUCCESS, player, kit.toString());
        }
    }
}
