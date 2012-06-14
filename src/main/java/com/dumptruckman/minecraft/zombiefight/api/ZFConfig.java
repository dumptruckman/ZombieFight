package com.dumptruckman.minecraft.zombiefight.api;

import com.dumptruckman.minecraft.pluginbase.config.BaseConfig;
import com.dumptruckman.minecraft.pluginbase.config.ConfigEntry;
import com.dumptruckman.minecraft.pluginbase.config.EntryBuilder;
import com.dumptruckman.minecraft.pluginbase.config.ListConfigEntry;
import com.dumptruckman.minecraft.pluginbase.config.MappedConfigEntry;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Interface for interacting with the config of this plugin.
 */
public interface ZFConfig extends BaseConfig {

    ListConfigEntry<String> ENABLED_WORLDS = new EntryBuilder<String>(String.class, "worlds.enabled").stringSerializer().comment("# Worlds game is enabled in.").buildList();
    MappedConfigEntry<Location> PRE_GAME_SPAWN = new EntryBuilder<Location>(Location.class, "spawns.pre-game_spawn").serializer(new LocationSerializer()).comment("# The location of the pre-game spawn, per world.").buildMap();
    MappedConfigEntry<Location> GAME_SPAWN = new EntryBuilder<Location>(Location.class, "spawns.game_spawn").serializer(new LocationSerializer()).comment("# The location of the game spawn, per world.").buildMap();

    ConfigEntry<Integer> MIN_PLAYERS = new EntryBuilder<Integer>(Integer.class, "game_settings.minimum_players").stringSerializer().def(15).defaultIfMissing().comment("# The minimum players required to start a game").build();
    ConfigEntry<Integer> MAX_PLAYERS = new EntryBuilder<Integer>(Integer.class, "game_settings.maximum_players").stringSerializer().def(30).defaultIfMissing().comment("# The maximum players for a game.  The game will start immediately if reached.").build();

    ConfigEntry<Integer> COUNTDOWN_TIME = new EntryBuilder<Integer>(Integer.class, "game_settings.countdown_length").stringSerializer().def(120).defaultIfMissing().comment("# The amount of time in seconds a game will countdown before starting when enough players have joined.").build();
    ConfigEntry<Integer> END_DURATION = new EntryBuilder<Integer>(Integer.class, "game_settings.end_game_duration").stringSerializer().def(30).defaultIfMissing().comment("# How long to wait (in seconds) after a game has finished before beginning a new game.").build();
    ConfigEntry<Integer> ZOMBIE_LOCK = new EntryBuilder<Integer>(Integer.class, "game_settings.zombie_lock").stringSerializer().def(30).defaultIfMissing().comment("# The amount of time in seconds that the first zombie will be locked in place and unable to damage players").build();
    ConfigEntry<Integer> ZOMBIE_DAMAGE = new EntryBuilder<Integer>(Integer.class, "game_settings.zombie_damage").stringSerializer().def(1).defaultIfMissing().comment("# This is the amount of extra damage zombies do.").build();
}
