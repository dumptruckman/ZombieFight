package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GameManager;
import com.dumptruckman.minecraft.zombiefight.api.GameStatus;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

class DefaultGameManager implements GameManager {

    private ZombieFight plugin;
    private Map<String, Game> gameMap = new HashMap<String, Game>(1);
    private Set<String> enabledWorlds = null;

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
        getGame(world);
    }

    @Override
    public void disableWorld(World world) {
        Game game = getGame(world);
        game.forceEnd(false);
        getEnabledWorlds().remove(world.getName());
        plugin.config().set(ZFConfig.ENABLED_WORLDS, new ArrayList<String>(getEnabledWorlds()));
        plugin.config().save();
    }

    @Override
    public void unloadWorld(World world) {
        disableWorld(world);
        gameMap.remove(world.getName());
    }
}
