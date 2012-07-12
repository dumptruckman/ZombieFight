package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GameManager;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

class DefaultGameManager implements GameManager {

    private ZombieFight plugin;
    private Map<String, Game> gameMap = new HashMap<String, Game>(1);
    private Set<String> enabledWorlds = null;
    private String primaryGame = null;

    DefaultGameManager(ZombieFight plugin) {
        this.plugin = plugin;
    }

    @Override
    public Game getGame(World world) {
        Game game = gameMap.get(world.getName());
        if (game == null) {
            game = new DefaultGame(plugin, world);
            gameMap.put(world.getName(), game);
        }
        return game;
    }

    private Set<String> getEnabledWorlds() {
        if (enabledWorlds == null) {
            enabledWorlds = new CopyOnWriteArraySet<String>(plugin.config().getList(ZFConfig.ENABLED_WORLDS));
        }
        return enabledWorlds;
    }

    @Override
    public boolean isWorldEnabled(World world) {
        return getEnabledWorlds().contains(world.getName());
    }

    @Override
    public void enableWorld(World world) {
        getEnabledWorlds().add(world.getName());
        plugin.config().set(ZFConfig.ENABLED_WORLDS, new ArrayList<String>(getEnabledWorlds()));
        plugin.config().save();
        Game game = getGame(world);
        game.forceEnd(true);
    }

    @Override
    public void disableWorld(World world) {
        Game game = getGame(world);
        game.forceEnd(false);
        getEnabledWorlds().remove(world.getName());
        if (primaryGame != null && primaryGame.equals(world.getName())) {
            primaryGame = null;
        }
        plugin.config().set(ZFConfig.ENABLED_WORLDS, new ArrayList<String>(getEnabledWorlds()));
        plugin.config().save();
    }

    @Override
    public void unloadWorld(World world) {
        disableWorld(world);
        gameMap.remove(world.getName());
    }

    @Override
    public void setPrimaryGame(World world) {
        if (world != null) {
            primaryGame = world.getName();
        } else {
            primaryGame = null;
        }
    }

    @Override
    public Game getPrimaryGame() {
        return primaryGame != null ? gameMap.get(primaryGame) : null;
    }
}
