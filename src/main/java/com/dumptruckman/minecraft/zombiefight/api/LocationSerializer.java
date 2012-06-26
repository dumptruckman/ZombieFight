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
