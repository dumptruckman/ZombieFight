/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Disguiser;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import net.minecraft.server.v1_12_R1.DataWatcher;
import net.minecraft.server.v1_12_R1.MathHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

class DefaultDisguiser implements Disguiser {

    private ZombieFight plugin;
    private boolean dead = false;

    DefaultDisguiser(ZombieFight plugin) {
        this.plugin = plugin;
        //Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new DisguiseTask());
    }

    public void terminate() {
        dead = true;
    }

    @Override
    public void disguise(Player player, EntityType type) {
        Disguise disguise = new MobDisguise(DisguiseType.valueOf(type.name()), true);
        DisguiseAPI.disguiseToAll(player, disguise);
    }

    @Override
    public void undisguise(Player player) {
        DisguiseAPI.undisguiseToAll(player);
    }

    @Override
    public boolean isDisguised(Player player) {
        return DisguiseAPI.isDisguised(player);
    }

    @Override
    public void handleQuit(Player player) {
//        dataMap.remove(player.getName());
//        Packet29DestroyEntity p29 = new Packet29DestroyEntity(player.getEntityId());
//        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
//            if (!player.getWorld().equals(p.getWorld())) {
//                continue;
//            }
//            if (p.getName().equals(player.getName())) {
//                continue;
//            }
//            ((CraftPlayer) p).getHandle().netServerHandler.sendPacket(p29);
//            ((CraftPlayer) p).getHandle().netServerHandler.sendPacket(p29);
//        }
    }
}
