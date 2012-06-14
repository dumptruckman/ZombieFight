package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GameManager;
import com.dumptruckman.minecraft.zombiefight.api.GameStatus;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

class DefaultGameManager implements GameManager {

    private ZombieFight plugin;
    private Map<String, Game> gameMap = new HashMap<String, Game>(1);
    private Set<String> disabledWorlds = null;

    DefaultGameManager(ZombieFight plugin) {
        this.plugin = plugin;
    }

    @Override
    public Game getGame(String worldName) {
        if (disabledWorlds == null) {
            disabledWorlds = new CopyOnWriteArraySet<String>(plugin.config().getList(ZFConfig.DISABLED_WORLDS));
        }
        if (disabledWorlds.contains(worldName)) {
            return null;
        }
        Game game = gameMap.get(worldName);
        if (game == null) {
            game = new DefaultGame(plugin, worldName);
            gameMap.put(worldName, game);
        }
        return game;
    }

    @Override
    public Game newGame(String worldName) {
        return null;
    }
}
