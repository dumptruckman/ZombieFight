package com.dumptruckman.minecraft.zombiefight.util;

import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.pluginbase.config.AbstractYamlConfig;
import com.dumptruckman.minecraft.pluginbase.plugin.BukkitPlugin;

import java.io.File;
import java.io.IOException;

public class CommentedConfig extends AbstractYamlConfig<ZFConfig> implements ZFConfig {

    public CommentedConfig(BukkitPlugin plugin, boolean doComments, File configFile, Class<? extends ZFConfig>... configClasses) throws IOException {
        super(plugin, doComments, true, configFile, configClasses);
    }

    @Override
    protected String getHeader() {
        return "";
    }
}
