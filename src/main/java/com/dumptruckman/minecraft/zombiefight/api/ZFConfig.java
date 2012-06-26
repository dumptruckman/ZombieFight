package com.dumptruckman.minecraft.zombiefight.api;

import com.dumptruckman.minecraft.pluginbase.config.BaseConfig;
import com.dumptruckman.minecraft.pluginbase.config.ConfigEntry;
import com.dumptruckman.minecraft.pluginbase.config.EntryBuilder;
import com.dumptruckman.minecraft.pluginbase.config.ListConfigEntry;
import com.dumptruckman.minecraft.pluginbase.config.MappedConfigEntry;
import com.dumptruckman.minecraft.pluginbase.util.Null;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Interface for interacting with the config of this plugin.
 */
public interface ZFConfig extends BaseConfig {

    ConfigEntry<Boolean> TEST_MODE = new EntryBuilder<Boolean>(Boolean.class, "test_mode").stringSerializer().def(false).defaultIfMissing().comment("# Allows same username to join multiple times for testing.  Requires server restart!").build();

    ListConfigEntry<String> ENABLED_WORLDS = new EntryBuilder<String>(String.class, "worlds.enabled").stringSerializer().comment("# Worlds game is enabled in.").buildList();
    ConfigEntry<Null> DATA = new EntryBuilder<Null>(Null.class, "data").comment("# Options built by the plugin.  Most of this can be set via command!").build();
    MappedConfigEntry<Location> PRE_GAME_SPAWN = new EntryBuilder<Location>(Location.class, "data.pre-game_spawn").serializer(new LocationSerializer()).comment("# The location of the pre-game spawn, per world.").buildMap();
    MappedConfigEntry<Location> GAME_SPAWN = new EntryBuilder<Location>(Location.class, "data.game_spawn").serializer(new LocationSerializer()).comment("# The location of the game spawn, per world.").buildMap();
    MappedConfigEntry<Integer> BORDER_RADIUS = new EntryBuilder<Integer>(Integer.class, "data.border.radius").def(150).defaultIfMissing().stringSerializer().comment("# The radius of the border from the game spawn.").buildMap();
    MappedConfigEntry<Integer> BORDER_WARN = new EntryBuilder<Integer>(Integer.class, "data.border.warn").def(7).defaultIfMissing().stringSerializer().comment("# The distance before the border that players will be warned.").buildMap();

    ConfigEntry<Integer> MIN_PLAYERS = new EntryBuilder<Integer>(Integer.class, "game_settings.players.minimum_players").stringSerializer().def(10).defaultIfMissing().comment("# The minimum players required to start a game.").build();
    ConfigEntry<Integer> MAX_PLAYERS = new EntryBuilder<Integer>(Integer.class, "game_settings.players.maximum_players").stringSerializer().def(30).defaultIfMissing().comment("# The maximum players for a game.  The game will start immediately if reached.").build();
    ConfigEntry<Integer> COUNTDOWN_TIME = new EntryBuilder<Integer>(Integer.class, "game_settings.times.start_countdown.duration").stringSerializer().def(120).defaultIfMissing().comment("# The amount of time in seconds a game will countdown before starting when enough players have joined.").build();
    ListConfigEntry<Integer> COUNTDOWN_WARNINGS = new EntryBuilder<Integer>(Integer.class, "game_settings.times.start_countdown.warnings").defList(Arrays.asList(120, 90, 60, 30, 15, 10, 5)).defaultIfMissing().stringSerializer().comment("# The seconds at which a countdown warning will display.").buildList();
    ConfigEntry<Integer> END_DURATION = new EntryBuilder<Integer>(Integer.class, "game_settings.times.end_game_duration").stringSerializer().def(20).defaultIfMissing().comment("# How long to wait (in seconds) after a game has finished before beginning a new game.").build();
    ConfigEntry<Integer> LAST_HUMAN = new EntryBuilder<Integer>(Integer.class, "game_settings.times.last_human.duration").stringSerializer().def(180).defaultIfMissing().comment("# How long the last human alive muts survive (in seconds) before they are declared winner.").build();
    ListConfigEntry<Integer> LAST_HUMAN_WARNINGS = new EntryBuilder<Integer>(Integer.class, "game_settings.times.last_human.warnings").defList(Arrays.asList(120, 90, 60, 30, 15, 10, 5)).defaultIfMissing().stringSerializer().comment("# The seconds at which a countdown warning will display.").buildList();
    ConfigEntry<Integer> ZOMBIE_LOCK = new EntryBuilder<Integer>(Integer.class, "game_settings.times.zombie_lock.duration").stringSerializer().def(60).defaultIfMissing().comment("# The amount of time in seconds that the first zombie will be locked in place and unable to damage players.").build();
    ListConfigEntry<Integer> ZOMBIE_LOCK_WARNINGS = new EntryBuilder<Integer>(Integer.class, "game_settings.times.zombie_lock.warnings").defList(Arrays.asList(30, 15, 10, 5)).defaultIfMissing().stringSerializer().comment("# The seconds at which a countdown warning will display.").buildList();
    ConfigEntry<Integer> ZOMBIE_DAMAGE = new EntryBuilder<Integer>(Integer.class, "game_settings.zombie.damage_bonus").stringSerializer().def(1).defaultIfMissing().comment("# This is the amount of extra damage zombies do.").build();
    ConfigEntry<Integer> ZOMBIE_HUNGER_CHANCE = new EntryBuilder<Integer>(Integer.class, "game_settings.zombie.hunger_effect.chance").stringSerializer().def(5).defaultIfMissing().comment("# This is the chance zombies will afflict hunger effect on damage (food poisoning effect).").build();
    ConfigEntry<Integer> ZOMBIE_HUNGER_DURATION = new EntryBuilder<Integer>(Integer.class, "game_settings.zombie.hunger_effect.duration").stringSerializer().def(30).defaultIfMissing().comment("# This is the duration of hunger effect.").build();
    ConfigEntry<Integer> ZOMBIE_HUNGER_STRENGTH = new EntryBuilder<Integer>(Integer.class, "game_settings.zombie.hunger_effect.strength").stringSerializer().def(1).defaultIfMissing().comment("# This is the strength of the hunger effect.").build();
    ConfigEntry<Integer> BORDER_DAMAGE = new EntryBuilder<Integer>(Integer.class, "game_settings.border.damage").def(2).defaultIfMissing().stringSerializer().comment("# The damage taken for every specified amount of time while outside the border.").build();
    ConfigEntry<Integer> BORDER_TIME = new EntryBuilder<Integer>(Integer.class, "game_settings.border.time").def(1).defaultIfMissing().stringSerializer().comment("# How often (in seconds) that damage is applied while outside of the border.").build();
    ConfigEntry<Integer> INSTA_BREAK = new EntryBuilder<Integer>(Integer.class, "game_settings.zombie.insta_break_chance").def(30).defaultIfMissing().stringSerializer().comment("# Percent chance for zombie to instantly break blocks on hit.").build();
    ConfigEntry<Integer> ZOMBIE_BREAK_SPEED_STRENGTH = new EntryBuilder<Integer>(Integer.class, "game_settings.zombie.break_speed.strength").stringSerializer().def(1).defaultIfMissing().comment("# This is the strength of the break speed effect.").build();
    ConfigEntry<Integer> HUMAN_FINDER_START = new EntryBuilder<Integer>(Integer.class, "game_settings.human_finder.start_after").def(60).defaultIfMissing().stringSerializer().comment("# How long (in seconds) after a zombie last attacks a human (or vice versa) that lightning beacons will start locating humans.").build();
    ConfigEntry<Integer> HUMAN_FINDER_TICK = new EntryBuilder<Integer>(Integer.class, "game_settings.human_finder.period").def(20).defaultIfMissing().stringSerializer().comment("# How often lightning beacons will appear once they start (until zombies hit humans)").build();
    ConfigEntry<Integer> SMELL_RANGE = new EntryBuilder<Integer>(Integer.class, "game_settings.zombie.ability.smell.radius").def(75).defaultIfMissing().stringSerializer().comment("# The radius for how far zombies can smell humans").build();
}
