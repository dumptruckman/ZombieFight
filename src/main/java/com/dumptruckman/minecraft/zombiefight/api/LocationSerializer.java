/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.api;

import com.dumptruckman.minecraft.pluginbase.config.EntrySerializer;
import org.bukkit.Location;

class LocationSerializer implements EntrySerializer<Location> {

    @Override
    public Location deserialize(Object o) {
        return DataStrings.parseLocation(o.toString());
    }

    @Override
    public Object serialize(Location location) {
        return DataStrings.valueOf(location);
    }
}
