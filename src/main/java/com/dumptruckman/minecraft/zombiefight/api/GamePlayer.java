/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.api;

import org.bukkit.entity.Player;

public interface GamePlayer {

    int getId();

    Player getPlayer();

    String getName();

    boolean isZombie();

    PlayerType getType();

    boolean isOnline();

    void joinedGame();

    void leftGame();

    void makeZombie(boolean broadcast);

    void makeHuman();

    Player getTrackedPlayer();

    void setTrackedPlayer(Player player);

    boolean isTracking(Player player);
}
