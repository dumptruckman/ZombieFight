package com.dumptruckman.minecraft.zombiefight.api;

import com.dumptruckman.minecraft.pluginbase.locale.Message;
import org.bukkit.Chunk;

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

    void broadcast(Message message, Object...args);

    void forceEnd(boolean restart);

    void snapshotChunk(Chunk chunk);
}
