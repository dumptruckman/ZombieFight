package com.dumptruckman.minecraft.zombiefight.api;

import com.dumptruckman.minecraft.pluginbase.config.BaseConfig;
import com.dumptruckman.minecraft.pluginbase.config.ConfigEntry;
import com.dumptruckman.minecraft.pluginbase.config.EntryBuilder;
import com.dumptruckman.minecraft.pluginbase.config.MappedConfigEntry;
import org.bukkit.Location;

/**
 * Interface for interacting with the config of this plugin.
 */
public interface ZFConfig extends BaseConfig {

    MappedConfigEntry<Location> PRE_GAME_SPAWN = new EntryBuilder<Location>(Location.class, "spawns.pre-game_spawn").serializer(new LocationSerializer()).comment("The location of the pre-game spawn, per world.").buildMap();
    MappedConfigEntry<Location> GAME_SPAWN = new EntryBuilder<Location>(Location.class, "spawns.game_spawn").serializer(new LocationSerializer()).comment("The location of the game spawn, per world.").buildMap();

    ConfigEntry<Integer> MIN_PLAYERS = new EntryBuilder<Integer>(Integer.class, "game_settings.minimum_players").stringSerializer().def(15).defaultIfMissing().comment("The minimum players required to start a game").build();
    ConfigEntry<Integer> MAX_PLAYERS = new EntryBuilder<Integer>(Integer.class, "game_settings.maximum_players").stringSerializer().def(30).defaultIfMissing().comment("The maximum players for a game.  The game will start immediately if reached.").build();

    ConfigEntry<Integer> COUNTDOWN_TIME = new EntryBuilder<Integer>(Integer.class, "game_settings.countdown_length").stringSerializer().def(120).defaultIfMissing().comment("The amount of time in seconds a game will countdown before starting when enough players have joined.").build();
}
