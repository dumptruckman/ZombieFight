package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.zombiefight.api.Snapshot;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
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
    private Map<Block, BlockState> blocks = new HashMap<Block, BlockState>();
    private List<EntitySnapshot> entities = new LinkedList<EntitySnapshot>();
    private Set<UUID> entityIds = new HashSet<UUID>();

    private static class EntitySnapshot {
        private int x, y, z;
        private Class type;
        private EntitySnapshot(Entity entity) {
            Location loc = entity.getLocation();
            x = loc.getBlockX();
            y = loc.getBlockY();
            z = loc.getBlockZ();
            type = entity.getClass();
        }
        void spawn(World world) {
            if (!Item.class.isAssignableFrom(type)) {
                world.spawn(new Location(world, x, y, z), type);
            }
        }
    }

    DefaultSnapshot(World world) {
        worldName = world.getName();
        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof Player)) {
                entityIds.add(entity.getUniqueId());
                entities.add(new EntitySnapshot(entity));
            }
        }
    }

    @Override
    public void snapshotChunk(Chunk chunk) {
        for (Entity entity : chunk.getEntities()) {
            if (!(entity instanceof Player)) {
                if (entityIds.add(entity.getUniqueId())) {
                    entities.add(new EntitySnapshot(entity));
                }
            }
        }
    }

    public void snapshotBlock(Block block) {
        if (!blocks.containsKey(block)) {
            blocks.put(block, block.getState());
        }
    }

    public void applySnapshot() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return;
        }
        for (Map.Entry<Block, BlockState> block : blocks.entrySet()) {
            BlockState state = block.getValue();
            block.getKey().setTypeIdAndData(state.getTypeId(), state.getRawData(), false);
            state.update(true);
        }
        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof Player)) {
                entity.remove();
            }
        }
        for (EntitySnapshot entity : entities) {
            entity.spawn(world);
        }

    }
}
