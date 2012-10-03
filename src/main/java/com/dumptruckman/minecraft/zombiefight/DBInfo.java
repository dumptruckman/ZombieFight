package com.dumptruckman.minecraft.zombiefight;

class DBInfo {

    private volatile int id = -1;

    int getId() {
        return this.id;
    }

    void setId(final int id) {
        this.id = id;
    }
}
