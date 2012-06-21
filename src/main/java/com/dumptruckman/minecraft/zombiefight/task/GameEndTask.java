package com.dumptruckman.minecraft.zombiefight.task;

import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;

public class GameEndTask extends CountdownTask {

    public GameEndTask(Game game, ZombieFight plugin) {
        super(game, plugin);
        setCountdown(getConfig().get(ZFConfig.END_DURATION));
    }

    @Override
    public boolean shouldEnd() {
        return getGame().hasReset();
    }

    @Override
    public boolean shouldCountdown() {
        return true;
    }

    @Override
    public void countdownWarning(int warning) {

    }

    @Override
    public void countdownFinished() {
        getGame().forceEnd(true);
    }
}