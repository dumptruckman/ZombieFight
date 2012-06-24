package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.zombiefight.api.Disguiser;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;

class DefaultDisguiser implements Disguiser {

    private ZombieFight plugin;

    DefaultDisguiser(ZombieFight plugin) {
        this.plugin = plugin;
    }


}
