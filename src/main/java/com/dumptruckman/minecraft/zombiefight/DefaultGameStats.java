package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GamePlayer;
import com.dumptruckman.minecraft.zombiefight.api.GameStats;

class DefaultGameStats implements GameStats {

    private final Game game;
    private final GamePlayer gamePlayer;
    private final DBInfo dbInfo;

    private volatile boolean startedInGame = false;
    private volatile boolean joinedInGame = false;
    private volatile boolean finishedInGame = false;
    private volatile boolean zombie = false;
    private volatile boolean firstZombie = false;
    private volatile boolean lastHuman = false;
    private volatile String kitUsed = "";

    DefaultGameStats(final Game game, final GamePlayer gamePlayer) {
        this.game = game;
        this.gamePlayer = gamePlayer;
        this.dbInfo = new DBInfo();
    }

    @Override
    public DBInfo getDBInfo() {
        return dbInfo;
    }

    @Override
    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public boolean startedInGame() {
        return startedInGame;
    }

    void setStartedInGame(boolean startedInGame) {
        this.startedInGame = startedInGame;
    }

    @Override
    public boolean joinedInGame() {
        return joinedInGame;
    }

    void setJoinedInGame(boolean joinedInGame) {
        this.joinedInGame = joinedInGame;
    }

    @Override
    public boolean finishedInGame() {
        return finishedInGame;
    }

    void setFinishedInGame(boolean endedInGame) {
        this.finishedInGame = endedInGame;
    }

    @Override
    public boolean isZombie() {
        return zombie;
    }

    void setZombie(boolean zombie) {
        this.zombie = zombie;
    }

    @Override
    public boolean isFirstZombie() {
        return firstZombie;
    }

    void setFirstZombie(boolean firstZombie) {
        this.firstZombie = firstZombie;
    }

    @Override
    public boolean isLastHuman() {
        return lastHuman;
    }

    void setLastHuman(boolean lastHuman) {
        this.lastHuman = lastHuman;
    }

    @Override
    public String getKitUsed() {
        return kitUsed;
    }

    void setKitUsed(String kitUsed) {
        if (kitUsed != null) {
            this.kitUsed = kitUsed;
        } else {
            this.kitUsed = "";
        }
    }
}
