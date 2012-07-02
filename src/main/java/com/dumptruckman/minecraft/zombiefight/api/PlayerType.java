package com.dumptruckman.minecraft.zombiefight.api;

public enum PlayerType {
    HUMAN,
    ZOMBIE;

    private int id = -1;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
