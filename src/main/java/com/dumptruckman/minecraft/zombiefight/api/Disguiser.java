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
