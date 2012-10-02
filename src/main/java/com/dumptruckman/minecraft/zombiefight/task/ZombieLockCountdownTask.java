/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
        setWarnings(getConfig().get(ZFConfig.ZOMBIE_LOCK_WARNINGS));
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
