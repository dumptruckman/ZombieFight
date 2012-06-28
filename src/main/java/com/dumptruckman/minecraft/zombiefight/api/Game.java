package com.dumptruckman.minecraft.zombiefight.api;

import com.dumptruckman.minecraft.pluginbase.locale.Message;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface Game {

    boolean isEnabled();

    Location getSpawnLocation();

    void init();

    boolean hasStarted();

    boolean hasEnded();

    boolean hasReset();

    boolean isZombie(Player player);

    void playerJoined(Player player);

    void playerQuit(Player player);

    void playerDied(Player player);

    boolean allowMove(Player player, Block fromBlock, Block toBlock);

    void handleMove(Player player, Location toLoc);

    boolean allowDamage(Player attacker, Player victim);

    World getWorld();

    void broadcast(Message message, Object...args);

    void snapshotChunk(Chunk chunk);

    void snapshotBlock(BlockState block);

    boolean start();

    boolean forceStart();

    boolean end();

    boolean forceEnd(boolean restart);

    boolean isCountdownPhase();

    boolean isZombieLockPhase();

    void unlockZombies();

    boolean isLastHumanPhase();

    void addZombieItems(Inventory inventory);

    void rightClickAbilityUse(Player player, ItemStack item);

    void leftClickAbilityUse(Player player, ItemStack item);
}
