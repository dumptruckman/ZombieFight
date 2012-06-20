package com.dumptruckman.minecraft.zombiefight.api;

import com.dumptruckman.minecraft.pluginbase.locale.Message;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface Game {

    boolean hasStarted();

    boolean hasEnded();

    boolean isZombie(Player player);

    void playerJoined(Player player);

    void playerQuit(Player player);

    boolean allowMove(Player player, Location toLoc);

    boolean allowDamage(Player attacker, Player victim);

    World getWorld();

    void broadcast(Message message, Object...args);

    void snapshotChunk(Chunk chunk);

    void snapshotBlock(Block block);
}
