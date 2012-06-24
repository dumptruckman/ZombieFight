package com.dumptruckman.minecraft.zombiefight.api;

import org.bukkit.Chunk;
import org.bukkit.block.BlockState;

public interface Snapshot {

    void initialize();

    void snapshotChunk(Chunk chunk);

    void snapshotBlock(BlockState block);

    void applySnapshot();
}
