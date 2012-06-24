package com.dumptruckman.minecraft.zombiefight.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Map;
import java.util.WeakHashMap;

public class BlockLocation {

    private static Map<String, BlockLocation> storedLocs = new WeakHashMap<String, BlockLocation>();

    private int x, y, z;
    private String world;
    private int hashCode;
    private String stringForm;

    private BlockLocation(Block block, String stringForm) {
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
        this.world = block.getWorld().getName();
        hashCode = block.hashCode();
        this.stringForm = stringForm;
    }

    public static BlockLocation get(Block block) {
        String stringForm = "Block at World:" + block.getWorld().getName() + " X:" + block.getX() + " Y:" + block.getY() + " Z:" + block.getZ();
        if (storedLocs.containsKey(stringForm)) {
            return storedLocs.get(stringForm);
        }
        BlockLocation blockLoc = new BlockLocation(block, stringForm);
        storedLocs.put(stringForm, blockLoc);
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
