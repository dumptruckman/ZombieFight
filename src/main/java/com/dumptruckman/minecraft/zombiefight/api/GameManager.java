package com.dumptruckman.minecraft.zombiefight.api;

public interface GameManager {

    Game getGame(String worldName);

    void newGame(String worldName);

    boolean isWorldEnabled(String worldName);

    void enableWorld(String worldName);

    void disableWorld(String worldName);
}
