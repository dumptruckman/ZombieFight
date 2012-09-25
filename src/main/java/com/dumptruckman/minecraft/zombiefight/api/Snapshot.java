/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.api;

import org.bukkit.Chunk;
import org.bukkit.block.BlockState;

public interface Snapshot {

    void initialize();

    void snapshotChunk(Chunk chunk);

    void snapshotBlock(BlockState block);

    void applySnapshot();
}
