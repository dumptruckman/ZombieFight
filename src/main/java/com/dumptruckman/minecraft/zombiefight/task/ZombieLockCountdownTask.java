package com.dumptruckman.minecraft.zombiefight.task;

import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.zombiefight.util.TimeTools;

public class ZombieLockCountdownTask extends CountdownTask {

    public ZombieLockCountdownTask(Game game, ZombieFight plugin) {
        super(game, plugin);
        setCountdown(getConfig().get(ZFConfig.ZOMBIE_LOCK));
        setWarnings(getConfig().getList(ZFConfig.ZOMBIE_LOCK_WARNINGS));
    }

    @Override
    public boolean shouldEnd() {
        return getGame().hasEnded();
    }

    @Override
    public boolean shouldCountdown() {
        return getGame().isZombieLockPhase();
    }

    @Override
    public void countdownWarning(int warning) {
        getGame().broadcast(Language.ZOMBIE_LOCK_COUNTDOWN, TimeTools.toLongForm(warning));
    }

    @Override
    public void countdownFinished() {
        getGame().unlockZombies();
    }
}
