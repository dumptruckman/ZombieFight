package com.dumptruckman.minecraft.zombiefight.task;

import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GamePlayer;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;

public class LastHumanCountdownTask extends CountdownTask {

    private GamePlayer lastHuman;

    public LastHumanCountdownTask(Game game, ZombieFight plugin, GamePlayer lastHuman) {
        super(game, plugin);
        setCountdown(getConfig().get(ZFConfig.LAST_HUMAN));
        setWarnings(getConfig().getList(ZFConfig.LAST_HUMAN_WARNINGS));
    }

    @Override
    public boolean shouldEnd() {
        return getGame().hasEnded();
    }

    @Override
    public boolean shouldCountdown() {
        return getGame().isLastHumanPhase();
    }

    @Override
    public void countdownWarning(int warning) {
        getGame().broadcast(Language.LAST_HUMAN_COUNTDOWN, warning);
    }

    @Override
    public void countdownFinished() {
        getGame().broadcast(Language.LAST_HUMAN_WON, lastHuman.getPlayer().getName());
        getGame().end();
    }
}
