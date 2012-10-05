package com.dumptruckman.minecraft.zombiefight.api;

import com.dumptruckman.minecraft.zombiefight.DBEntry;

public interface GameStats extends DBEntry {

    GamePlayer getGamePlayer();

    Game getGame();

    boolean startedInGame();

    boolean joinedInGame();

    boolean finishedInGame();

    boolean isZombie();

    boolean isFirstZombie();

    boolean isLastHuman();

    String getKitUsed();
}
