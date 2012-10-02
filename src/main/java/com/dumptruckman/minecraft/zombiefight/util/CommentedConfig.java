/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.util;

import com.dumptruckman.minecraft.pluginbase.config.AbstractYamlConfig;
import com.dumptruckman.minecraft.pluginbase.plugin.BukkitPlugin;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;

import java.io.File;
import java.io.IOException;

public class CommentedConfig extends AbstractYamlConfig implements ZFConfig {

    public CommentedConfig(BukkitPlugin plugin, boolean doComments, File configFile, Class<? extends ZFConfig>... configClasses) throws IOException {
        super(plugin, doComments, true, configFile, configClasses);
    }

    @Override
    protected String getHeader() {
        return "";
    }
}
