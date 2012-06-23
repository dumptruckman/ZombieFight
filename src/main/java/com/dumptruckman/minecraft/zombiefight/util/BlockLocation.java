package com.dumptruckman.minecraft.zombiefight.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockLocation {

    private static Map<BlockLocation, BlockLocation> storedLocs = new HashMap<BlockLocation,BlockLocation>();

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
        if (storedLocs.containsKey(block)) {
            return storedLocs.get(block);
        }
        BlockLocation blockLoc = new BlockLocation(block);
        storedLocs.put(blockLoc, blockLoc);
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
        if (o instanceof Block) {
            Block block = (Block) o;
            return block.getX() == x && block.getY() == y && block.getZ() == z && block.getWorld().getName().equals(world);
        } else if (o instanceof BlockLocation) {
            BlockLocation block = (BlockLocation) o;
            return block.x == this.x && block.y == this.y && block.z == this.z && block.world.equals(this.world);
        }
        return false;
    }

    public String toString() {
        return stringForm;
    }
}
