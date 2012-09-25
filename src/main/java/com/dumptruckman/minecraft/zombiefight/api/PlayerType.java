/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
