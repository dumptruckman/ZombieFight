package com.dumptruckman.minecraft.zombiefight.util;

import com.dumptruckman.minecraft.pluginbase.locale.Message;

public class Language {

    public static final Message CMD_CHECK_NAME = new Message("cmd.check.name",
            "Checks a chest and gives info relating to ZombieFight");
    public static final Message JOIN_WHILE_GAME_IN_PROGRESS = new Message("joining.game_in_progress",
            "A game is in progress.  Please wait in the waiting area for the next game.");
    public static final Message JOIN_WHILE_GAME_PREPARING = new Message("joining.game_preparing",
            "A new game is setting up.  Please wait in the waiting area until enough players join to start the game.");
    public static final Message JOIN_WHILE_GAME_STARTING = new Message("joining.game_starting",
            "A new game will be starting momentarily...");

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
    public static final Message RUN_FROM_ZOMBIE = new Message("game.run_from_zombie",
            "You have %1 seconds to run away from the zombie!");
    public static final Message PLAYER_ZOMBIFIED = new Message("game.player_zombified",
            "%1 has been turned into a zombie!");

    public static final Message COULD_NOT_COUNTDOWN = new Message("error.could_not_countdown",
            "Could not start countdown! Game will start immediately!");

    public static void init() { }
}
