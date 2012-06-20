package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.locale.Message;
import com.dumptruckman.minecraft.pluginbase.locale.Messager;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GamePlayer;
import com.dumptruckman.minecraft.zombiefight.api.Snapshot;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

class DefaultGame2 implements Game {

    private ZombieFight plugin;

    private World world;

    private Snapshot snapshot;

    private Map<String, GamePlayer> gamePlayers = new HashMap<String, GamePlayer>();

    boolean started = false;
    boolean ended = false;

    DefaultGame2(ZombieFight plugin, World world) {
        this.plugin = plugin;
        this.world = world;
    }

    protected ZFConfig getConfig() {
        return plugin.config();
    }

    protected Messager getMessager() {
        return plugin.getMessager();
    }

    protected Player getPlayer(String name) {
        return plugin.getServer().getPlayerExact(name);
    }


    /**
     * PUBLIC METHODS FROM Game INTERFACE
     */

    /**
     *
     */
    @Override
    public boolean hasStarted() {
        return started;
    }

    @Override
    public boolean hasEnded() {
        return ended;
    }

    @Override
    public boolean isZombie(Player player) {
        boolean ret = false;
        if (hasStarted()) {
            GamePlayer gPlayer =
        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void playerJoined(Player player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void playerQuit(Player player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean allowMove(Player player, Location toLoc) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean allowDamage(Player attacker, Player victim) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public World getWorld() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void broadcast(Message message, Object... args) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void snapshotChunk(Chunk chunk) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void snapshotBlock(Block block) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
