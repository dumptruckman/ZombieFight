package com.dumptruckman.minecraft.zombiefight.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Map;
import java.util.WeakHashMap;

public class BlockLocation {

    private static Map<Integer, BlockLocation> storedLocs = new WeakHashMap<Integer, BlockLocation>();

    private int x, y, z;
    private String world;
    private int hashCode;
    private String stringForm;

    private BlockLocation(Block block) {
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
        this.world = block.getWorld().getName();
        hashCode = block.hashCode();
        this.stringForm = "Block at World:" + world + " X:" + x + " Y:" + y + " Z:" + z;
    }

    public static BlockLocation get(Block block) {
        if (storedLocs.containsKey(block.hashCode())) {
            return storedLocs.get(block.hashCode());
        }
        BlockLocation blockLoc = new BlockLocation(block);
        storedLocs.put(block.hashCode(), blockLoc);
        return blockLoc;
    }

    public Block getBlock() {
        World world = Bukkit.getWorld(this.world);
        if (world == null) {
            return null;
        }
        return world.getBlockAt(x, y, z);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BlockLocation) {
            BlockLocation block = (BlockLocation) o;
            return block.x == this.x && block.y == this.y && block.z == this.z && block.world.equals(this.world);
        }
        return false;
    }

    public String toString() {
        return stringForm;
    }
}
