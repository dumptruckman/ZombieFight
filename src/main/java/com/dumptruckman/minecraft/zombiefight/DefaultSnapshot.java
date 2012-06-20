package com.dumptruckman.minecraft.zombiefight;

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
    private Map<BlockLocation, BlockState> blocks = new HashMap<BlockLocation, BlockState>();
    private List<EntitySnapshot> entities = new LinkedList<EntitySnapshot>();
    private Set<UUID> entityIds = new HashSet<UUID>();

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
        for (Entity e : world.getEntities()) {
            snapEntity(e);
        }
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

    public void snapshotBlock(Block block) {
        if (!blocks.containsKey(block)) {
            blocks.put(BlockLocation.get(block), block.getState());
        }
    }

    public void applySnapshot() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return;
        }
        for (Map.Entry<BlockLocation, BlockState> blockData : blocks.entrySet()) {
            Block block = blockData.getKey().getBlock();
            if (block == null) {
                continue;
            }
            BlockState state = blockData.getValue();
            block.setTypeIdAndData(state.getTypeId(), state.getRawData(), false);
        }
        for (Map.Entry<BlockLocation, BlockState> blockData : blocks.entrySet()) {
            blockData.getValue().update(true);
        }
        for (Entity entity : world.getEntities()) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                entity.remove();
            }
        }
        for (EntitySnapshot entity : entities) {
            entity.spawn(world);
        }

    }
}
