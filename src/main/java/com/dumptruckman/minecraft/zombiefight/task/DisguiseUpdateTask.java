/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.task;

import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DisguiseUpdateTask implements Runnable {

    private ZombieFight plugin;

    public DisguiseUpdateTask(ZombieFight plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.getDisguiser().updateDisguise(player);
        }
    }
}
