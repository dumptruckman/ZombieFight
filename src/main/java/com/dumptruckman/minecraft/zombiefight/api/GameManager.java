package com.dumptruckman.minecraft.zombiefight.api;

public interface GameManager {

    Game getGame(String worldName);

    Game newGame(String worldName);
}
