/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.api;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public interface Disguiser {

    void terminate();

    void disguise(Player player, EntityType type);

    void undisguise(Player player);

    void updateDisguise(Player player);

    boolean isDisguised(Player player);

    void handleQuit(Player player);
}
