package com.dumptruckman.minecraft.zombiefight.api;

import org.bukkit.entity.Player;

public interface GamePlayer {

    Player getPlayer();

    boolean isZombie();

    boolean isOnline();

    void joinedGame();

    void leftGame();

    void makeZombie();

    void makeHuman();
}
