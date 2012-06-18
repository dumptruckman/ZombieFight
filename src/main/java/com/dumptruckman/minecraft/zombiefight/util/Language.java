package com.dumptruckman.minecraft.zombiefight.util;

import com.dumptruckman.minecraft.pluginbase.locale.Message;

public class Language {

    public static final Message IN_GAME_ONLY = new Message("cmd.in_game_only",
            "You may only use this command in game!");
    public static final Message CMD_CONSOLE_REQUIRES_WORLD = new Message("cmd.console_requires_world",
            "Must use world flag to use from console.");

    public static final Message CMD_PGSPAWN_NAME = new Message("cmd.pgspawn.name",
            "Warps you to the pre-game spawn or sets one.");
    public static final Message CMD_PGSPAWN_FAIL = new Message("cmd.pgspawn.failure",
            "A pre-game spawn has not been set yet!");
    public static final Message CMD_PGSPAWN_SET_SUCCESS = new Message("cmd.pgspawn.set.success",
            "You have changed the pre-game spawn to your current location.");
    public static final Message CMD_PGSPAWN_SET_NO_PERM = new Message("cmd.pgspawn.set.no_perm",
            "You do not have the required permission to change pre-game spawn.");

    public static final Message CMD_GSPAWN_NAME = new Message("cmd.gspawn.name",
            "Warps you to the game spawn or sets one.");
    public static final Message CMD_GSPAWN_FAIL = new Message("cmd.gspawn.failure",
            "A game spawn has not been set yet!");
    public static final Message CMD_GSPAWN_SET_SUCCESS = new Message("cmd.gspawn.set.success",
            "You have changed the game spawn to your current location.");
    public static final Message CMD_GSPAWN_SET_NO_PERM = new Message("cmd.gspawn.set.no_perm",
            "You do not have the required permission to change game spawn.");

    public static final Message CMD_START_GAME_NAME = new Message("cmd.start.name",
            "Starts the game.");
    public static final Message CMD_START_ALREADY_STARTED = new Message("cmd.start.already_started",
            "The game for this world has already started!");
    public static final Message CMD_START_FORCE_SUCCESS = new Message("cmd.start.force_success",
            "You have force started the game.");
    public static final Message CMD_START_SUCCESS = new Message("cmd.start.success",
            "You have begun the countdown for the game.");

    public static final Message CMD_END_GAME_NAME = new Message("cmd.end.name",
            "Ends the game.");
    public static final Message CMD_END_NOT_STARTED = new Message("cmd.end.not_started",
            "The game for this world has not yet started!");
    public static final Message CMD_END_SUCCESS = new Message("cmd.end.success",
            "You have ended the game.");

    public static final Message CMD_ENABLE_NAME = new Message("cmd.enable.name",
            "Enables the game for the world.");
    public static final Message CMD_ENABLE_ALREADY = new Message("cmd.enable.already_enabled",
            "The game is already enabled for the world!");
    public static final Message CMD_ENABLE_SUCCESS = new Message("cmd.enable.success",
            "You have enabled the game for the world.");

    public static final Message CMD_DISABLE_NAME = new Message("cmd.disable.name",
            "Disables the game for the world.");
    public static final Message CMD_DISABLE_ALREADY = new Message("cmd.disable.already_disabled",
            "The game is already disabled for the world!");
    public static final Message CMD_DISABLE_SUCCESS = new Message("cmd.disable.success",
            "You have disabled the game for the world.");

    public static final Message JOIN_WHILE_GAME_IN_PROGRESS = new Message("joining.game_in_progress",
            "A game is in progress.  Please wait in the waiting area for the next game.");
    public static final Message JOIN_WHILE_GAME_PREPARING = new Message("joining.game_preparing",
            "A new game is setting up.  Please wait in the waiting area until enough players join to start the game.");
    public static final Message JOIN_WHILE_GAME_STARTING = new Message("joining.game_starting",
            "A new game will be starting momentarily...");

    public static final Message LEAVE_WORLD = new Message("world_change.left_world",
            "%1 has left the game world!");
    public static final Message JOIN_WORLD = new Message("world_change.join_world",
            "%1 has joined the game world!");

    public static final Message ENOUGH_FOR_QUICK_START = new Message("game.starting.quick_start",
            "Enough players have joined to start the game immediately!");
    public static final Message ENOUGH_FOR_COUNTDOWN_START = new Message("game.starting.countdown_start",
            "Enough players have joined to start the countdown for game to begin!");
    public static final Message TOO_FEW_PLAYERS = new Message("game.starting.too_few_players",
            "Too many players have left.  Start countdown halted until more players join.");
    public static final Message GAME_STARTING = new Message("game.starting.starting",
            "The game has begun!");
    public static final Message GAME_ENDED = new Message("game.ended.ended",
            "The game has ended!");
    public static final Message ALL_HUMANS_DEAD = new Message("game.ended.all_humans_dead",
            "All humans are dead.");
    public static final Message RUN_FROM_ZOMBIE = new Message("game.run_from_zombie",
            "You have %1 seconds to run away from the zombie!");
    public static final Message PLAYER_ZOMBIFIED = new Message("game.player_zombified",
            "%1 has been turned into a zombie!");
    public static final Message ZOMBIE_TAG = new Message("game.zombie_tag",
            "[ZOMBIE] ");
    public static final Message ZOMBIE_RELEASE = new Message("game.zombie_released",
            "The zombie has been released!!");
    public static final Message GAME_RESETTING = new Message("game.resetting_soon",
            "The game will be resetting in %1 seconds...");
    public static final Message ONE_HUMAN_LEFT = new Message("game.last_human.left",
            "Only one human remains... Can they survive the zombie horde for %1 seconds?");
    public static final Message LAST_HUMAN_WON = new Message("game.last_human.won",
            "The last human survived the final onslaught. Congratulations &6%1!");
    public static final Message APPROACHING_BORDER = new Message("game.border.approaching",
            "&cYou are approaching the border, turn back now or you will start to die!");

    public static final Message FORCE_END = new Message("game.force_end",
            "The game was ended abruptly!");

    public static final Message COULD_NOT_COUNTDOWN = new Message("error.could_not_countdown",
            "Could not start countdown! Game will start immediately!");
    public static final Message NO_WORLD = new Message("error.no_world",
            "There is no world named: %1");
    public static final Message NOT_GAME_WORLD = new Message("error.not_game_world",
            "'%1' is not a game world!");

    public static void init() { }
}
