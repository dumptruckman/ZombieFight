/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.api;

import com.dumptruckman.minecraft.zombiefight.DBEntry;
import org.bukkit.entity.Player;

public interface GamePlayer extends DBEntry {

    Player getPlayer();

    String getName();

    boolean isZombie();

    PlayerType getType();

    boolean isOnline();

    void joinedGame();

    void leftGame();

    Game getGame();

    GameStats getGameStats();

    void makeZombie(final boolean broadcast);

    void makeHuman();

    Player getTrackedPlayer();

    void setTrackedPlayer(final Player player);

    boolean isTracking(final Player player);
}
