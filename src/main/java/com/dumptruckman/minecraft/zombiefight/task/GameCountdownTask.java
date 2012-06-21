package com.dumptruckman.minecraft.zombiefight.task;

import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;

public class GameCountdownTask extends CountdownTask {

    public GameCountdownTask(Game game, ZombieFight plugin) {
        super(game, plugin);
        setCountdown(getConfig().get(ZFConfig.COUNTDOWN_TIME));
        setWarnings(getConfig().getList(ZFConfig.COUNTDOWN_WARNINGS));
    }

    @Override
    public boolean shouldEnd() {
        return getGame().hasStarted();
    }

    @Override
    public boolean shouldCountdown() {
        return getGame().isCountdownPhase();
    }

    @Override
    public void countdownWarning(int warning) {
        getGame().broadcast(Language.GAME_STARTING_IN, warning);
    }

    @Override
    public void countdownFinished() {
        getGame().forceStart();
    }
}
