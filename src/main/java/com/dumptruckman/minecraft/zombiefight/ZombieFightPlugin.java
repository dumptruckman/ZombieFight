package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.command.CheckCommand;
import com.dumptruckman.minecraft.zombiefight.util.CommentedConfig;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.pluginbase.plugin.AbstractBukkitPlugin;
import com.dumptruckman.minecraft.pluginbase.plugin.command.HelpCommand;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ZombieFightPlugin extends AbstractBukkitPlugin<ZFConfig> implements ZombieFight {

    private final List<String> cmdPrefixes = Arrays.asList("zf");

    @Override
    protected ZFConfig newConfigInstance() throws IOException {
        return new CommentedConfig(this, true, new File(getDataFolder(), "config.yml"), ZFConfig.class);
    }

    @Override
    public void preEnable() {
        Language.init();
        HelpCommand.addStaticPrefixedKey("");
    }

    @Override
    public void postEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ZombieFightListener(this), this);
        getCommandHandler().registerCommand(new CheckCommand(this));
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        super.onDisable();
    }

    @Override
    public List<String> getCommandPrefixes() {
        return cmdPrefixes;
    }

    @Override
    public List<String> dumpVersionInfo() {
        List<String> versionInfo = new LinkedList<String>();
        return versionInfo;
    }
}
