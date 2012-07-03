package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.InventoryHolder;

public class GameMonitor implements Listener {

    private ZombieFight plugin;

    public GameMonitor(ZombieFight plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void chunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        Game game = plugin.getGameManager().getGame(chunk.getWorld());
        if (game.isEnabled()) {
            game.snapshotChunk(chunk);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void unloadWorld(WorldUnloadEvent event) {
        if (!event.isCancelled()) {
            plugin.getGameManager().disableWorld(event.getWorld());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockMonitor(BlockBreakEvent event) {
        if (plugin.isCleaner(event.getPlayer())) {
            return;
        }
        handleBlockEvent(event.getBlock().getState(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockMonitor(BlockBurnEvent event) {
        handleBlockEvent(event.getBlock().getState(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockMonitor(BlockDispenseEvent event) {
        handleBlockEvent(event.getBlock().getState(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockMonitor(BlockFadeEvent event) {
        handleBlockEvent(event.getBlock().getState(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockMonitor(BlockFromToEvent event) {
        handleBlockEvent(event.getBlock().getState(), event);
        handleBlockEvent(event.getToBlock().getState(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockMonitor(BlockGrowEvent event) {
        handleBlockEvent(event.getBlock().getState(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockMonitor(BlockIgniteEvent event) {
        handleBlockEvent(event.getBlock().getState(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockMonitor(BlockPhysicsEvent event) {
        handleBlockEvent(event.getBlock().getState(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockMonitor(BlockPlaceEvent event) {
        if (plugin.isCleaner(event.getPlayer())) {
            return;
        }
        handleBlockEvent(event.getBlockReplacedState(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockMonitor(BrewEvent event) {
        handleBlockEvent(event.getBlock().getState(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockMonitor(FurnaceBurnEvent event) {
        handleBlockEvent(event.getBlock().getState(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockMonitor(FurnaceSmeltEvent event) {
        handleBlockEvent(event.getBlock().getState(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockMonitor(LeavesDecayEvent event) {
        handleBlockEvent(event.getBlock().getState(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockMonitor(SignChangeEvent event) {
        if (plugin.isCleaner(event.getPlayer())) {
            return;
        }
        handleBlockEvent(event.getBlock().getState(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void inventoryMonitor(InventoryOpenEvent event) {
        Player player = Bukkit.getPlayerExact(event.getPlayer().getName());
        if (player != null && plugin.isCleaner(player)) {
            return;
        }
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Chest) {
            handleBlockEvent((Chest) holder, event);
        } else if (holder instanceof Dispenser) {
            handleBlockEvent((Dispenser) holder, event);
        } else if (holder instanceof BrewingStand) {
            handleBlockEvent((BrewingStand) holder, event);
        } else if (holder instanceof Furnace) {
            handleBlockEvent((Furnace) holder, event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void explosionMonitor(EntityExplodeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        for (Block block : event.blockList()) {
            handleBlockEvent(block.getState(), event);
        }
    }

    private void handleBlockEvent(BlockState block, Cancellable event) {
        if (event.isCancelled()) {
            return;
        }
        Game game = plugin.getGameManager().getGame(block.getWorld());
        if (game.isEnabled()) {
            game.snapshotBlock(block);
        }
    }
}
