package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.zombiefight.api.GameManager;
import com.dumptruckman.minecraft.zombiefight.api.GameStatus;

public class DefaultGameManager implements GameManager {

    private GameStatus status = GameStatus.PREPARING;

    @Override
    public GameStatus getGameStatus() {
        return status;
    }

    @Override
    public void startGame() {
        status = GameStatus.IN_PROGRESS;
    }

    @Override
    public void endGame() {
        status = GameStatus.PREPARING;
    }
}
