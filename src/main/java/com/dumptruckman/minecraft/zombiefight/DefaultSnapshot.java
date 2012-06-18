package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.util.Logging;
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

    private int x, z;
    private String worldName;
    private Map<Integer, Map<Integer, Map<Integer, BlockState>>> blocks = new HashMap<Integer, Map<Integer, Map<Integer, BlockState>>>();
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

    DefaultSnapshot(Chunk chunk) {
        worldName = chunk.getWorld().getName();
        x = chunk.getX();
        z = chunk.getZ();
        for (Entity entity : chunk.getEntities()) {
            if (!(entity instanceof Player)) {
                entityIds.add(entity.getUniqueId());
                entities.add(new EntitySnapshot(entity));
            }
        }
    }

    public void snapshotBlock(Block block) {
        if (blocks.containsKey(block.getX())) {
            Map<Integer, Map<Integer, BlockState>> yMap = blocks.get(block.getX());
            if (yMap.containsKey(block.getY())) {
                Map<Integer, BlockState> zMap = yMap.get(block.getY());
                if (!zMap.containsKey(block.getZ())) {
                    zMap.put(block.getZ(), block.getState());
                }
            } else {
                Map<Integer, BlockState> zMap = new HashMap<Integer, BlockState>();
                zMap.put(block.getZ(), block.getState());
                yMap.put(block.getY(), zMap);
            }
        } else {
            Map<Integer, Map<Integer, BlockState>> yMap = new HashMap<Integer, Map<Integer, BlockState>>();
            Map<Integer, BlockState> zMap = new HashMap<Integer, BlockState>();
            zMap.put(block.getZ(), block.getState());
            yMap.put(block.getY(), zMap);
            blocks.put(block.getX(), yMap);
        }
    }

    public void applySnapshot() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return;
        }
        Chunk chunk = world.getChunkAt(x, z);
        if (chunk == null) {
            Logging.finer("Could not locate chunk!");
            return;
        }
        if (!chunk.isLoaded()) {
            chunk.load(false);
        }
        if (!chunk.isLoaded()) {
            return;
        }
        for (Entity entity : world.getEntities()) {
            if (entityIds.contains(entity.getUniqueId())) {
                entity.remove();
            }
        }
        for (EntitySnapshot entity : entities) {
            entity.spawn(world);
        }
        for (Map.Entry<Integer, Map<Integer, Map<Integer, BlockState>>> xEntry : blocks.entrySet()) {
            for (Map.Entry<Integer, Map<Integer, BlockState>> yEntry : xEntry.getValue().entrySet()) {
                for (Map.Entry<Integer, BlockState> zEntry : yEntry.getValue().entrySet()) {
                    BlockState state = zEntry.getValue();
                    state.getBlock().setTypeIdAndData(state.getTypeId(), state.getRawData(), false);
                    zEntry.getValue().update(true);
                }
            }
        }
    }
}
