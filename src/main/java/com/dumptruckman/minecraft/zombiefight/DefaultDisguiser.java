/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Disguiser;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import net.minecraft.server.DataWatcher;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet24MobSpawn;
import net.minecraft.server.Packet29DestroyEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

class DefaultDisguiser implements Disguiser {

    private Map<String, EntityType> disguiseMap = new HashMap<String, EntityType>();
    private Map<String, DataWatcher> dataMap = new HashMap<String, DataWatcher>();

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
        if (disguiseMap.put(player.getName(), type) == null) {
            disguiseToAll(player);
        }
    }

    @Override
    public void undisguise(Player player) {
        if (disguiseMap.remove(player.getName()) != null) {
            dataMap.remove(player.getName());
            // Make packets out of loop!
            CraftPlayer cPlayer = (CraftPlayer) player;
            Packet29DestroyEntity p29 = new Packet29DestroyEntity(cPlayer.getEntityId());
            Packet20NamedEntitySpawn p20 = new Packet20NamedEntitySpawn(cPlayer.getHandle());

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!player.getWorld().equals(p.getWorld())) {
                    continue;
                }
                if (p.getName().equals(player.getName())) {
                    continue;
                }
                ((CraftPlayer) p).getHandle().netServerHandler.sendPacket(p29);
                ((CraftPlayer) p).getHandle().netServerHandler.sendPacket(p20);
            }
        }
    }

    @Override
    public boolean isDisguised(Player player) {
        return disguiseMap.containsKey(player.getName());
    }

    @Override
    public void updateDisguise(Player player) {
        if (isDisguised(player)) {
            disguiseToAll(player);
        }
    }

    @Override
    public void handleQuit(Player player) {
        dataMap.remove(player.getName());
        Packet29DestroyEntity p29 = new Packet29DestroyEntity(player.getEntityId());
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (!player.getWorld().equals(p.getWorld())) {
                continue;
            }
            if (p.getName().equals(player.getName())) {
                continue;
            }
            ((CraftPlayer) p).getHandle().netServerHandler.sendPacket(p29);
            ((CraftPlayer) p).getHandle().netServerHandler.sendPacket(p29);
        }
    }

    private Packet24MobSpawn packetMaker(Player player, byte id) {
        DataWatcher data = dataMap.get(player.getName());
        if (data == null) {
            data = new DataWatcher();
            dataMap.put(player.getName(), data);
        }

        Location loc = player.getLocation();
        Packet24MobSpawn packet = new Packet24MobSpawn();
        packet.a = player.getEntityId();
        packet.b = id;
        packet.c = MathHelper.floor(loc.getX() * 32.0D);
        packet.d = MathHelper.floor(loc.getY() * 32.0D);
        packet.e = MathHelper.floor(loc.getZ() * 32.0D);
        packet.f = (byte) ((int) loc.getYaw() * 256.0F / 360.0F);
        packet.g = (byte) ((int) (loc.getPitch() * 256.0F / 360.0F));
        Field datawatcher;
        try {
            datawatcher = packet.getClass().getDeclaredField("i");
            datawatcher.setAccessible(true);
            datawatcher.set(packet, data);
            datawatcher.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return packet;
    }

    private void disguiseToAll(Player player) {
        // Make packets out of loop!
        Packet24MobSpawn p24 = packetMaker(player, (byte) disguiseMap.get(player.getName()).getTypeId());
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!player.getWorld().equals(p.getWorld())) {
                continue;
            }
            if (p.getName().equals(player.getName())) {
                continue;
            }
            Logging.finest("Sending mobSpawn packet to " + p.getName());
            ((CraftPlayer) p).getHandle().netServerHandler.sendPacket(p24);
        }
    }

    private class DisguiseTask implements Runnable {
        @Override
        public void run() {
            if (!dead) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this, 20L);
            }
        }
    }
}
