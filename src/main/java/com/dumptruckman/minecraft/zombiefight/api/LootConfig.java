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
}
