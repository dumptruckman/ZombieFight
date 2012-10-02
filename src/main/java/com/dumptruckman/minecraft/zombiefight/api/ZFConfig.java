/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.api;

import com.dumptruckman.minecraft.pluginbase.config.BaseConfig;
import com.dumptruckman.minecraft.pluginbase.config.EntryBuilder;
import com.dumptruckman.minecraft.pluginbase.config.ListConfigEntry;
import com.dumptruckman.minecraft.pluginbase.config.MappedConfigEntry;
import com.dumptruckman.minecraft.pluginbase.config.SimpleConfigEntry;
import com.dumptruckman.minecraft.pluginbase.util.Null;
import org.bukkit.Location;

import java.util.Arrays;

/**
 * Interface for interacting with the config of this plugin.
 */
public interface ZFConfig extends BaseConfig {

    SimpleConfigEntry<Boolean> TEST_MODE = new EntryBuilder<Boolean>(Boolean.class, "test_mode")
            .def(false)
            .comment("# Allows same username to join multiple times for testing.  Requires server restart!")
            .build();

    ListConfigEntry<String> ENABLED_WORLDS = new EntryBuilder<String>(String.class, "worlds.enabled")
            .comment("# Worlds game is enabled in.")
            .buildList();
    SimpleConfigEntry<Null> DATA = new EntryBuilder<Null>(Null.class, "data")
            .comment("# Options built by the plugin.  Most of this can be set via command!")
            .build();
    MappedConfigEntry<Location> PRE_GAME_SPAWN = new EntryBuilder<Location>(Location.class, "data.pre-game_spawn")
            .allowNull()
            .serializer(new LocationSerializer())
            .comment("# The location of the pre-game spawn, per world.")
            .buildMap();
    MappedConfigEntry<Location> GAME_SPAWN = new EntryBuilder<Location>(Location.class, "data.game_spawn")
            .allowNull().serializer(new LocationSerializer())
            .comment("# The location of the game spawn, per world.")
            .buildMap();
    MappedConfigEntry<Integer> BORDER_RADIUS = new EntryBuilder<Integer>(Integer.class, "data.border.radius")
            .def(250)
            .comment("# The radius of the border from the game spawn.")
            .buildMap();
    MappedConfigEntry<Integer> BORDER_WARN = new EntryBuilder<Integer>(Integer.class, "data.border.warn")
            .def(7)
            .comment("# The distance before the border that players will be warned.")
            .buildMap();
    SimpleConfigEntry<Boolean> TRACK_STATS = new EntryBuilder<Boolean>(Boolean.class, "database.track_stats")
            .def(false)
            .comment("# Whether or not to track stats... DB must be setup properly!")
            .build();

    SimpleConfigEntry<Integer> MIN_PLAYERS = new EntryBuilder<Integer>(Integer.class, "game_settings.players.minimum_players")
            .def(4)
            .comment("# The minimum players required to start a game.")
            .build();
    SimpleConfigEntry<Integer> MAX_PLAYERS = new EntryBuilder<Integer>(Integer.class, "game_settings.players.maximum_players")
            .def(30)
            .comment("# The maximum players for a game.  The game will start immediately if reached.")
            .build();
    SimpleConfigEntry<Integer> COUNTDOWN_TIME = new EntryBuilder<Integer>(Integer.class, "game_settings.times.start_countdown.duration")
            .def(120)
            .comment("# The amount of time in seconds a game will countdown before starting when enough players have joined.")
            .build();
    ListConfigEntry<Integer> COUNTDOWN_WARNINGS = new EntryBuilder<Integer>(Integer.class, "game_settings.times.start_countdown.warnings")
            .defList(Arrays.asList(120, 90, 60, 30, 15, 10, 5))
            .comment("# The seconds at which a countdown warning will display.")
            .buildList();
    SimpleConfigEntry<Integer> END_DURATION = new EntryBuilder<Integer>(Integer.class, "game_settings.times.end_game_duration")
            .def(20)
            .comment("# How long to wait (in seconds) after a game has finished before beginning a new game.")
            .build();
    SimpleConfigEntry<Integer> LAST_HUMAN = new EntryBuilder<Integer>(Integer.class, "game_settings.times.last_human.duration")
            .def(180)
            .comment("# How long the last human alive muts survive (in seconds) before they are declared winner.")
            .build();
    ListConfigEntry<Integer> LAST_HUMAN_WARNINGS = new EntryBuilder<Integer>(Integer.class, "game_settings.times.last_human.warnings")
            .defList(Arrays.asList(120, 90, 60, 30, 15, 10, 5))
            .comment("# The seconds at which a countdown warning will display.")
            .buildList();
    SimpleConfigEntry<Integer> ZOMBIE_LOCK = new EntryBuilder<Integer>(Integer.class, "game_settings.times.zombie_lock.duration")
            .def(60)
            .comment("# The amount of time in seconds that the first zombie will be locked in place and unable to damage players.")
            .build();
    ListConfigEntry<Integer> ZOMBIE_LOCK_WARNINGS = new EntryBuilder<Integer>(Integer.class, "game_settings.times.zombie_lock.warnings")
            .defList(Arrays.asList(30, 15, 10, 5))
            .comment("# The seconds at which a countdown warning will display.")
            .buildList();
    SimpleConfigEntry<Integer> ZOMBIE_DAMAGE = new EntryBuilder<Integer>(Integer.class, "game_settings.zombie.damage_bonus")
            .def(3)
            .comment("# This is the amount of extra damage zombies do.")
            .build();
    SimpleConfigEntry<Integer> ZOMBIE_HUNGER_CHANCE = new EntryBuilder<Integer>(Integer.class, "game_settings.zombie.hunger_effect.chance")
            .def(5)
            .comment("# This is the chance zombies will afflict hunger effect on damage (food poisoning effect).")
            .build();
    SimpleConfigEntry<Integer> ZOMBIE_HUNGER_DURATION = new EntryBuilder<Integer>(Integer.class, "game_settings.zombie.hunger_effect.duration")
            .def(30)
            .comment("# This is the duration of hunger effect.")
            .build();
    SimpleConfigEntry<Integer> ZOMBIE_HUNGER_STRENGTH = new EntryBuilder<Integer>(Integer.class, "game_settings.zombie.hunger_effect.strength")
            .def(1)
            .comment("# This is the strength of the hunger effect.")
            .build();
    SimpleConfigEntry<Integer> BORDER_DAMAGE = new EntryBuilder<Integer>(Integer.class, "game_settings.border.damage")
            .def(2)
            .comment("# The damage taken for every specified amount of time while outside the border.")
            .build();
    SimpleConfigEntry<Integer> BORDER_TIME = new EntryBuilder<Integer>(Integer.class, "game_settings.border.time")
            .def(1)
            .comment("# How often (in seconds) that damage is applied while outside of the border.")
            .build();
    SimpleConfigEntry<Integer> HUMAN_FINDER_START = new EntryBuilder<Integer>(Integer.class, "game_settings.human_finder.start_after")
            .def(60)
            .comment("# How long (in seconds) after a zombie last attacks a human (or vice versa) that lightning beacons will start locating humans.")
            .build();
    SimpleConfigEntry<Integer> HUMAN_FINDER_TICK = new EntryBuilder<Integer>(Integer.class, "game_settings.human_finder.period")
            .def(20)
            .comment("# How often lightning beacons will appear once they start (until zombies hit humans)")
            .build();

    SimpleConfigEntry<String> PRIMARY_WORLD = new EntryBuilder<String>(String.class, "worlds.primary")
            .allowNull()
            .comment("The world where the active game is.")
            .build();
}
