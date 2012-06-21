package com.dumptruckman.minecraft.zombiefight.api;

import org.bukkit.Chunk;
import org.bukkit.block.Block;

public interface Snapshot {

    void initialize();

    void snapshotChunk(Chunk chunk);

    void snapshotBlock(Block block);

    void applySnapshot();
}
