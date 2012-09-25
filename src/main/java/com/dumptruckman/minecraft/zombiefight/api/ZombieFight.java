/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.api;

import com.dumptruckman.minecraft.pluginbase.plugin.BukkitPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * The main interface of the ZombieFight plugin.  You can use this for casting Plugin when got from Bukkit's
 * plugin manager.
 */
public interface ZombieFight extends BukkitPlugin<ZFConfig>, Plugin {

    GameManager getGameManager();

    void broadcastWorld(String worldName, String message);

    LootConfig getLootConfig();

    void setPlayerKit(String name, String kit);

    String getPlayerKit(String name);

    void displayKits(Player player);

    boolean isCleaner(Player player);

    void setCleaner(Player player, boolean cleaning);

    Disguiser getDisguiser();

    StatsDatabase getStats();
}
