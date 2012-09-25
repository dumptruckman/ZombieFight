/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.api;

import org.bukkit.World;

public interface GameManager {

    Game getGame(World world);

    boolean isWorldEnabled(World world);

    void enableWorld(World world);

    void disableWorld(World world);

    void unloadWorld(World world);

    void setPrimaryGame(World world);

    Game getPrimaryGame();
}
