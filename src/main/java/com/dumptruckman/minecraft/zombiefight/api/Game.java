package com.dumptruckman.minecraft.zombiefight.api;

public interface Game {

    GameStatus getStatus();

    void forceStart();

    void countdown();

    void haltCountdown();

    void endGame();

    boolean isPlaying(String playerName);

    boolean isZombie(String playerName);

    boolean hasOnlineZombies();

    boolean hasOnlineHumans();

    int onlinePlayerCount();

    void playerQuit(String playerName);

    void playerJoined(String playerName);

    String randomZombie();

    void makeZombie(String name);

    String getFirstZombie();

    String getWorld();

    void checkGameStart();

    void checkGameEnd();

    void humanFound();
}
