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

    ListConfigEntry<String> ENABLED_WORLDS = new EntryBuilder<String>(String.class, "worlds.enabled").stringSerializer().comment("# Worlds game is enabled in.").buildList();
    ConfigEntry<Null> DATA = new EntryBuilder<Null>(Null.class, "data").comment("# Options built by the plugin").build();
    MappedConfigEntry<Location> PRE_GAME_SPAWN = new EntryBuilder<Location>(Location.class, "data.pre-game_spawn").serializer(new LocationSerializer()).comment("# The location of the pre-game spawn, per world.").buildMap();
    MappedConfigEntry<Location> GAME_SPAWN = new EntryBuilder<Location>(Location.class, "data.game_spawn").serializer(new LocationSerializer()).comment("# The location of the game spawn, per world.").buildMap();
    MappedConfigEntry<Integer> BORDER_RADIUS = new EntryBuilder<Integer>(Integer.class, "data.border.radius").def(100).defaultIfMissing().stringSerializer().comment("# The radius of the border from the game spawn.").buildMap();
    MappedConfigEntry<Integer> BORDER_WARN = new EntryBuilder<Integer>(Integer.class, "data.border.warn").def(10).defaultIfMissing().stringSerializer().comment("# The distance before the border that players will be warned.").buildMap();

    ConfigEntry<Integer> MIN_PLAYERS = new EntryBuilder<Integer>(Integer.class, "game_settings.minimum_players").stringSerializer().def(15).defaultIfMissing().comment("# The minimum players required to start a game.").build();
    ConfigEntry<Integer> MAX_PLAYERS = new EntryBuilder<Integer>(Integer.class, "game_settings.maximum_players").stringSerializer().def(30).defaultIfMissing().comment("# The maximum players for a game.  The game will start immediately if reached.").build();
    ConfigEntry<Integer> COUNTDOWN_TIME = new EntryBuilder<Integer>(Integer.class, "game_settings.start_countdown_duration").stringSerializer().def(120).defaultIfMissing().comment("# The amount of time in seconds a game will countdown before starting when enough players have joined.").build();
    ConfigEntry<Integer> END_DURATION = new EntryBuilder<Integer>(Integer.class, "game_settings.end_game_duration").stringSerializer().def(30).defaultIfMissing().comment("# How long to wait (in seconds) after a game has finished before beginning a new game.").build();
    ConfigEntry<Integer> FINAL_HUMAN = new EntryBuilder<Integer>(Integer.class, "game_settings.last_human_duration").stringSerializer().def(30).defaultIfMissing().comment("# How long the last human alive muts survive (in seconds) before they are declared winner.").build();
    ConfigEntry<Integer> ZOMBIE_LOCK = new EntryBuilder<Integer>(Integer.class, "game_settings.zombie_lock_duration").stringSerializer().def(30).defaultIfMissing().comment("# The amount of time in seconds that the first zombie will be locked in place and unable to damage players.").build();
    ConfigEntry<Integer> ZOMBIE_DAMAGE = new EntryBuilder<Integer>(Integer.class, "game_settings.zombie_damage").stringSerializer().def(1).defaultIfMissing().comment("# This is the amount of extra damage zombies do.").build();
    ConfigEntry<Integer> BORDER_DAMAGE = new EntryBuilder<Integer>(Integer.class, "game_settings.border.damage").def(2).defaultIfMissing().stringSerializer().comment("# The damage taken for every specified amount of time while outside the border.").build();
    ConfigEntry<Integer> BORDER_TIME = new EntryBuilder<Integer>(Integer.class, "game_settings.border.time").def(1).defaultIfMissing().stringSerializer().comment("# How often (in seconds) that damage is applied while outside of the border.").build();
    ConfigEntry<Integer> INSTA_BREAK = new EntryBuilder<Integer>(Integer.class, "game_settings.zombie_insta_break").def(30).defaultIfMissing().stringSerializer().comment("# Percent chance for zombie to instantly break blocks on hit.").build();
    ConfigEntry<Integer> HUMAN_FINDER_START = new EntryBuilder<Integer>(Integer.class, "game_settings.human_finder.start_after").def(120).defaultIfMissing().stringSerializer().comment("# How long (in seconds) after a zombie last attacks a human (or vice versa) that lightning beacons will start locating humans.").build();
    ConfigEntry<Integer> HUMAN_FINDER_TICK = new EntryBuilder<Integer>(Integer.class, "game_settings.human_finder.period").def(15).defaultIfMissing().stringSerializer().comment("# How often lightning beacons will appear once they start (until zombies hit humans)").build();
}
