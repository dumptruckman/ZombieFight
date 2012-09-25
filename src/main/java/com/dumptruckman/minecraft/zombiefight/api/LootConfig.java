/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.api;

/**
 * An interface for retrieving the loot table from last_human_reward.yml.
 */
public interface LootConfig {

    /**
     * Retrieves the loot table for the last human reward.
     *
     * @return The loot table for last human reward or null if none found.
     */
    LootTable getLastHumanReward();

    LootTable getKit(String name);

    String[] getKitNames();

    LootTable getDefaultKit();
}
