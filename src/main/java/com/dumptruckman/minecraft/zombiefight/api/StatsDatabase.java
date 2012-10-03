/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.api;

public interface StatsDatabase {

    void newGame(final Game game);

    void gameReset(final Game game);

    void gameStarted(final Game game);

    void gameEnded(final Game game);

    void playerJoinedGame(final Game game, final GamePlayer player);

    void setupPlayer(final GamePlayer player);

    void playerUpdate(final GamePlayer player);

    void playerKilled(final GamePlayer killer, final GamePlayer victim, final Game game, final int weapon);

    void playerTypeChange(final Game game, final GamePlayer player, final PlayerType type);
}
