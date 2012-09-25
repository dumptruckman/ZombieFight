/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.task;

import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class HumanFinderTask extends GameTask {

    private long beaconTick = 0;
    private long humanFinder = 0;

    public HumanFinderTask(Game game, ZombieFight plugin) {
        super(game, plugin, 20L, 20L);
    }

    public void humanFound() {
        humanFinder = 0;
    }

    @Override
    protected void onRun() {
        long lastHit = humanFinder - getConfig().get(ZFConfig.HUMAN_FINDER_START);
        if (lastHit == 0) {
            strike();
            tick();
        } else if (lastHit > 0) {
            if (beaconTick >= getConfig().get(ZFConfig.HUMAN_FINDER_TICK)) {
                strike();
                beaconTick = 0;
            }
            tick();
        } else {
            beaconTick = 0;
            humanFinder++;
        }
    }

    private void tick() {
        humanFinder++;
        beaconTick++;
    }

    private void strike() {
        for (Player player : getGame().getWorld().getPlayers()) {
            if (!getGame().isZombie(player)) {
                Location loc = player.getLocation().getBlock().getRelative(BlockFace.UP).getLocation();
                loc.getWorld().strikeLightningEffect(loc);
            }
        }
    }

    @Override
    public boolean shouldEnd() {
        return !getGame().hasStarted() || getGame().hasEnded();
    }
}
