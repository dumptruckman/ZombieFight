/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Snapshot;
import com.dumptruckman.minecraft.zombiefight.util.BlockLocation;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

class DefaultSnapshot implements Snapshot {

    private String worldName;
    boolean init;
    private Map<BlockLocation, BlockState> blocks;
    private List<EntitySnapshot> entities;
    private Set<UUID> entityIds;

    private static class EntitySnapshot {
        private int x, y, z;
        private Class type;
        private int age = 0;

        private EntitySnapshot(Entity entity) {
            Location loc = entity.getLocation();
            x = loc.getBlockX();
            y = loc.getBlockY();
            z = loc.getBlockZ();
            type = entity.getClass();
            if (entity instanceof Ageable) {
                age = ((Ageable) entity).getAge();
            }
        }
        void spawn(World world) {
            if (!Item.class.isAssignableFrom(type)) {
                Entity entity = world.spawn(new Location(world, x, y, z), type);
                if (entity instanceof Ageable) {
                    ((Ageable) entity).setAge(age);
                }
            }
        }
    }

    DefaultSnapshot(World world) {
        worldName = world.getName();
        blocks = new HashMap<BlockLocation, BlockState>();
        entities = new LinkedList<EntitySnapshot>();
        entityIds = new HashSet<UUID>();
        init = false;
    }

    @Override
    public void initialize() {
        for (Entity e : Bukkit.getWorld(worldName).getEntities()) {
            snapEntity(e);
        }
        init = true;
    }

    @Override
    public void snapshotChunk(Chunk chunk) {
        for (Entity e : chunk.getEntities()) {
            snapEntity(e);
        }
    }

    private void snapEntity(Entity entity) {
        if (entity instanceof LivingEntity && !(entity instanceof Player)) {
            if (entityIds.add(entity.getUniqueId())) {
                entities.add(new EntitySnapshot(entity));
            }
        }
    }

    public void snapshotBlock(BlockState block) {
        BlockLocation blockLoc = BlockLocation.get(block.getBlock());
        if (!blocks.containsKey(blockLoc)) {
            blocks.put(blockLoc, block);
        }
    }

    public void applySnapshot() {
        if (!init) {
            Logging.fine("Snapshot never initialized for world: " + worldName);
            return;
        }
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return;
        }
        for (Map.Entry<BlockLocation, BlockState> blockData : blocks.entrySet()) {
            Block block = blockData.getKey().getBlock();
            if (block == null) {
                Logging.finest("Could not locate block: " + blockData.getKey());
                continue;
            }
            BlockState state = blockData.getValue();
            block.setTypeIdAndData(state.getTypeId(), state.getRawData(), false);
        }
        for (Map.Entry<BlockLocation, BlockState> blockData : blocks.entrySet()) {
            blockData.getValue().update(true);
        }
        for (Entity entity : world.getEntities()) {
            if ((entity instanceof LivingEntity && !(entity instanceof Player)) || entity instanceof Item) {
                entity.remove();
            }
        }
        for (EntitySnapshot entity : entities) {
            entity.spawn(world);
        }

    }
}
