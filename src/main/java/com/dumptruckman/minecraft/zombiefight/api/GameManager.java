package com.dumptruckman.minecraft.zombiefight.api;

import org.bukkit.World;

public interface GameManager {

    Game getGame(World world);

    boolean isWorldEnabled(World world);

    void enableWorld(World world);

    void disableWorld(World world);

    void unloadWorld(World world);
}
