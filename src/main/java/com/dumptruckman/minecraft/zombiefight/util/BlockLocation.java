package com.dumptruckman.minecraft.zombiefight.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BlockLocation {

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
    }

    public static BlockLocation get(Block block) {
        return new BlockLocation(block);
        //BlockLocation blockLoc = new BlockLocation(block);
        //return blockLoc;
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
        return "Block at World:" + world + " X:" + x + " Y:" + y + " Z:" + z;
    }
}
