/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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

    public static final Message CMD_BORDER_NAME = new Message("cmd.border.name",
            "Checks or sets a border size.");
    public static final Message CMD_BORDER_CHECK = new Message("cmd.border.check",
            "The border for this world is a %1 block radius.");
    public static final Message CMD_BORDER_SET_SUCCESS = new Message("cmd.border.set.success",
            "You have change the border for this world to: %1");
    public static final Message CMD_BORDER_SET_FAILURE = new Message("cmd.border.set.failure",
            "That is not a valid value for border size!");

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
    public static final Message CMD_END_ALREADY_ENDED = new Message("cmd.end.already_ended",
            "The game for this world has already ended!");
    public static final Message CMD_END_SUCCESS = new Message("cmd.end.success",
            "You have ended the game.");
    public static final Message CMD_END_BROADCAST = new Message("cmd.end.broadcast",
            "&c%1&f ended the game by command.");

    public static final Message CMD_ENABLE_NAME = new Message("cmd.enable.name",
            "Enables the game for the world.");
    public static final Message CMD_ENABLE_ALREADY = new Message("cmd.enable.already_enabled",
            "The game is already enabled for the world!");
    public static final Message CMD_ENABLE_SUCCESS = new Message("cmd.enable.success",
            "You have enabled the game for the world.");

    public static final Message CMD_SELECT_NAME = new Message("cmd.select.name",
            "Selects the active game world.");
    public static final Message CMD_SELECT_NOT_ENABLED = new Message("cmd.select.not_enabled",
            "The world is not enabled! Use /zf enable first.");
    public static final Message CMD_SELECT_SUCCESS = new Message("cmd.select.success",
            "You have select '%1' as the active game world.");

    public static final Message CMD_DESELECT_NAME = new Message("cmd.deselect.name",
            "Disables primary game selection.");
    public static final Message CMD_DESELECT_NONE_SELECTED = new Message("cmd.deselect.none_selected",
            "There is no primary game selection!");
    public static final Message CMD_DESELECT_SUCCESS = new Message("cmd.deselect.success",
            "You have disabled primary game selection.");

    public static final Message CMD_CLEANUP_NAME = new Message("cmd.cleanup.name",
            "Allows user to clean up during a game.");
    public static final Message CMD_CLEANUP_DISABLE = new Message("cmd.cleanup.disable",
            "You have disabled cleanup mode.");
    public static final Message CMD_CLEANUP_ENABLE = new Message("cmd.cleanup.enable",
            "You have enabled cleanup mode.  Type again to disable.");

    public static final Message CMD_KIT_NAME = new Message("cmd.kit.name",
            "Lists kits available to you or selects a kit for the next game.");
    public static final Message CMD_KIT_LIST = new Message("cmd.kit.list",
            "Select kit with &6/kit [kitname]", "Available kits: %1");
    public static final Message CMD_KIT_NO_ACCESS = new Message("cmd.kit.no_access",
            "You do not have access to kit: %1");
    public static final Message CMD_KIT_SUCCESS = new Message("cmd.kit.success",
            "You will begin the next game with the kit: %1");

    public static final Message CMD_DISABLE_NAME = new Message("cmd.disable.name",
            "Disables the game for the world.");
    public static final Message CMD_DISABLE_ALREADY = new Message("cmd.disable.already_disabled",
            "The game is already disabled for the world!");
    public static final Message CMD_DISABLE_SUCCESS = new Message("cmd.disable.success",
            "You have disabled the game for the world.");

    public static final Message JOIN_WHILE_GAME_IN_PROGRESS = new Message("joining.game_in_progress_zombie",
            "&8Game in progress... &l&cYou will join the game as a zombie!");
    public static final Message JOIN_WHILE_GAME_PREPARING = new Message("joining.game_preparing",
            "&8A new game is setting up.  Please wait in the waiting area until enough players join to start the game.");
    public static final Message JOIN_WHILE_GAME_STARTING = new Message("joining.game_starting",
            "&8A new game will be starting momentarily...");

    public static final Message LEAVE_WORLD = new Message("world_change.left_world",
            "%1 has left the game world!");
    public static final Message JOIN_WORLD = new Message("world_change.join_world",
            "%1 has joined the game world!");

    public static final Message ENOUGH_FOR_QUICK_START = new Message("game.starting.quick_start",
            "&bEnough players have joined to start the game immediately!");
    public static final Message ENOUGH_FOR_COUNTDOWN_START = new Message("game.starting.countdown_start",
            "&bEnough players have joined to start the countdown for game to begin!");
    public static final Message TOO_FEW_PLAYERS = new Message("game.starting.too_few_players",
            "&7Too many players have left.  Start countdown halted until more players join.");
    public static final Message GAME_STARTING = new Message("game.starting.beginning",
            "&a&lThe game has begun!");
    public static final Message GAME_ENDED = new Message("game.ended.game_over",
            "&4&lThe game is over!");
    public static final Message GAME_ENDED_TOO_FEW_PLAYERS = new Message("game.ended.too_few_players",
            "&8The game has ended due to too few players...");
    public static final Message ALL_HUMANS_DEAD = new Message("game.ended.all_humans_dead",
            "&cAll humans are dead.");
    public static final Message RUN_FROM_ZOMBIE = new Message("game.run_from_zombie",
            "&7You have &f%1&7 to run away from the zombie!");
    public static final Message ZOMBIE_LOCK_COUNTDOWN = new Message("game.zombie_lock_countdown",
            "&7Zombie releasing in &f%1&7...");
    public static final Message PLAYER_ZOMBIFIED = new Message("game.player_zombified",
            "&4&l%1 has been turned into a zombie!");
    public static final Message PLAYER_JOINED_AS_ZOMBIE = new Message("game.player_joined.zombie",
            "&e%1 joined the game...&4 as a ZOMBIE!");
    public static final Message PLAYER_JOINED_AS_HUMAN = new Message("game.player_joined.human",
            "&e%1 joined the game...&2 as a human!");
    public static final Message ZOMBIE_NAME = new Message("game_settings.zombie_name_change",
            "&4%1");
    public static final Message HUMAN_NAME = new Message("game_settings.human_name_change",
            "&2%1");
    public static final Message ZOMBIE_RELEASE = new Message("game.zombie_released",
            "&c&lThe zombie has been released!!");
    public static final Message GAME_RESETTING = new Message("game.resetting_soon",
            "&7The game will be resetting in &f%1&7...");
    public static final Message ONE_HUMAN_LEFT = new Message("game.last_human.left",
            "&bOnly one human remains... Can they survive the zombie horde for &7%1&b?");
    public static final Message LAST_HUMAN_COUNTDOWN = new Message("game.last_human.countdown",
            "&7Last human wins in &f%1&7...");
    public static final Message LAST_HUMAN_REWARD = new Message("game.last_human.reward",
            "&6&lTake these items and defend your life, you are the last human!");
    public static final Message LAST_HUMAN_WON = new Message("game.last_human.won",
            "&aThe last human survived the final onslaught. Congratulations &6%1!");
    public static final Message APPROACHING_BORDER = new Message("game.border.approaching",
            "&cYou are approaching the border, turn back now or you will start to die!");
    public static final Message ROLLBACK = new Message("game.rollback",
            "&8Rolling back the game world.. This will take a moment and may cause lag.");
    public static final Message GAME_STARTING_IN = new Message("game.starting_in",
            "&7Game starting in &f%1&7...");
    public static final Message PLUGIN_RELOAD = new Message("plugin.reload",
            "&cPlugin reloading, games terminated.");
    public static final Message YOU_ARE_ZOMBIE = new Message("game.you_are_zombie",
            "&4&lYou have become a zombie!! &oBrraaainnsss...");

    public static final Message ZOMBIE_SMELL_LOCK = new Message("game.zombie.ability.smell.lock",
            "&7&oYou catch the scent of a human...");
    public static final Message ZOMBIE_SMELL_FACE = new Message("game.zombie.ability.smell.face",
            "&7&oYou turn to face the human scent...");
    public static final Message ZOMBIE_SMELL_NOT_LOCKED = new Message("game.zombie.ability.smell.not_locked",
            "&8&oYou are not following a human's scent... (Left-click)");

    public static final Message NO_WORLD = new Message("error.no_world",
            "There is no world named: %1");
    public static final Message NOT_GAME_WORLD = new Message("error.not_game_world",
            "'%1' is not a game world!");
    public static final Message KIT_ERROR = new Message("error.kit_missing",
            "The kit '%1' could not be found!");
    public static final Message KIT_ERROR_DEFAULT = new Message("error.kit_defaulted",
            "The kit '%1' could not be found! Using default...");

    public static void init() { }
}
