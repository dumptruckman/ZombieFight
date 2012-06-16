package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.zombiefight.api.GameManager;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;

public class GameManagerHelper {

    public static GameManager newGameManager(ZombieFight plugin) {
        return new DefaultGameManager(plugin);
    }
}
