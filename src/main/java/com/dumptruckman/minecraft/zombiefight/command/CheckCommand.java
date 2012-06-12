package com.dumptruckman.minecraft.zombiefight.command;

import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.zombiefight.util.Perms;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CheckCommand extends ZFCommand {

    public CheckCommand(ZombieFight plugin) {
        super(plugin);
        this.setName(messager.getMessage(Language.CMD_CHECK_NAME));
        this.setCommandUsage("/" + plugin.getCommandPrefixes().get(0) + " check");
        this.setArgRange(0, 0);
        for (String prefix : plugin.getCommandPrefixes()) {
            this.addKey(prefix + " check");
        }
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " check");
        this.setPermission(Perms.CMD_CHECK.getPermission());
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
