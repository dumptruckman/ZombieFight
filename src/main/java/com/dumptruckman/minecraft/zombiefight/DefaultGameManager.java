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
    public Game getGame(String worldName) {
        if (!getEnabledWorlds().contains(worldName)) {
            return null;
        }
        Game game = gameMap.get(worldName);
        if (game == null) {
            game = new DefaultGame(plugin, worldName);
            gameMap.put(worldName, game);
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
    public void newGame(String worldName) {
        Game game = new DefaultGame(plugin, worldName);
        gameMap.put(worldName, game);
        plugin.broadcastWorld(worldName, plugin.getMessager().getMessage(Language.JOIN_WHILE_GAME_PREPARING));
        game.checkGameStart();
    }

    @Override
    public boolean isWorldEnabled(String worldName) {
        return getEnabledWorlds().contains(worldName);
    }

    @Override
    public void enableWorld(String worldName) {
        getEnabledWorlds().add(worldName);
        plugin.config().set(ZFConfig.ENABLED_WORLDS, new ArrayList<String>(getEnabledWorlds()));
        plugin.config().save();
        Game game = getGame(worldName);
        World world = Bukkit.getWorld(worldName);
        Location loc = plugin.config().get(ZFConfig.GAME_SPAWN.specific(worldName));
        if (loc == null ) {
            loc = world.getSpawnLocation();
        }
        for (Player player : world.getPlayers()) {
            player.teleport(loc);
        }
        game.checkGameStart();
    }

    @Override
    public void disableWorld(String worldName) {
        Game game = getGame(worldName);
        game.forceEnd(false);
        gameMap.remove(worldName);
        getEnabledWorlds().remove(worldName);
        plugin.config().set(ZFConfig.ENABLED_WORLDS, new ArrayList<String>(getEnabledWorlds()));
        plugin.config().save();
    }
}
