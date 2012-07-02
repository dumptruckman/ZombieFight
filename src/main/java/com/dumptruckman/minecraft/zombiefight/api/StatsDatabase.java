package com.dumptruckman.minecraft.zombiefight.api;

import org.bukkit.entity.Player;

public interface StatsDatabase {

    boolean connect();

    void gameStarted(Game game);

    void playerJoinedGame(Player player, Game game);

    int getPlayer(String player);
}
