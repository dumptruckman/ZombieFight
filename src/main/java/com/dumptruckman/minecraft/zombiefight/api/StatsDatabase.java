/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.api;

import org.bukkit.World;

import java.sql.Timestamp;

public interface StatsDatabase {

    int newGame(Timestamp createTime, World world);

    void gameStarted(Game game, Timestamp startTime);

    void gameEnded(Game game, Timestamp endTime);

    void playerJoinedGame(Game game, GamePlayer player);

    int getPlayer(String player);

    void playerUpdate(GamePlayer player);

    void playerKilled(GamePlayer killer, GamePlayer victim, Game game, Timestamp time, int weapon);

    void playerTypeChange(Game game, GamePlayer player, PlayerType type);
}
