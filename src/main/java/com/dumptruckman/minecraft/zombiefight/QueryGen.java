/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight;

class QueryGen {

    static final String PLAYERS_TABLE = "zf_players";
    static final int PLAYERS_VERSION = 0;
    static final String PLAYER_TYPE_TABLE = "zf_player_type";
    static final int PLAYER_TYPE_VERSION = 0;
    static final String GAMES_TABLE = "zf_games";
    static final int GAMES_VERSION = 0;
    static final String STATS_TABLE = "zf_stats";
    static final int STATS_VERSION = 0;
    static final String TYPE_HISTORY_TABLE = "zf_type_history";
    static final int TYPE_HISTORY_VERSION = 0;
    static final String KILLS_TABLE = "zf_kills";
    static final int KILLS_VERSION = 0;

    static String createPlayerTypeTable(boolean sqlite) {
        return "CREATE TABLE `" + PLAYER_TYPE_TABLE + "` ("
                + "`id` " + (sqlite ? "INTEGER PRIMARY KEY" : "INT UNSIGNED NOT NULL AUTO_INCREMENT")
                + ",`type_name` VARCHAR(255)" + (sqlite ? "" : " CHARACTER SET latin1 COLLATE latin1_general_ci") + " NOT NULL"

                + (sqlite ? "" : ",PRIMARY KEY(`id`)")
                + ",UNIQUE" + (sqlite ? "" : " KEY") + "(`type_name`)"
                + ")" + (sqlite ? "" : "ENGINE = InnoDB,COMMENT = 'version:" + PLAYER_TYPE_VERSION + "'");
    }

    static String createPlayersTable(boolean sqlite) {
        return "CREATE TABLE `" + PLAYERS_TABLE + "` ("
                + "`id` " + (sqlite ? "INTEGER PRIMARY KEY" : "INT UNSIGNED NOT NULL AUTO_INCREMENT")
                + ",`player_name` VARCHAR(64)" + (sqlite ? "" : " CHARACTER SET latin1 COLLATE latin1_general_ci") + " NOT NULL"
                + ",`current_type` INT UNSIGNED"
                + ",`kit_selected` VARCHAR(255)"

                + (sqlite ? "" : ",PRIMARY KEY(`id`)")
                + ",UNIQUE" + (sqlite ? "" : " KEY") + "(`player_name`)"
                + ",FOREIGN KEY(`current_type`) REFERENCES `" + PLAYER_TYPE_TABLE + "`(`id`)"
                + ")" + (sqlite ? "" : "ENGINE = InnoDB,COMMENT = 'version:" + PLAYERS_VERSION + "'");
    }

    static String createGamesTable(boolean sqlite) {
        return "CREATE TABLE `" + GAMES_TABLE + "` ("
                + "`id` " + (sqlite ? "INTEGER PRIMARY KEY" : "INT UNSIGNED NOT NULL AUTO_INCREMENT")
                + ",`world` VARCHAR(255) NOT NULL"
                + ",`create_time` TIMESTAMP NOT NULL"
                + ",`start_time` TIMESTAMP"
                + ",`end_time` TIMESTAMP"
                + ",`humans_won` TINYINT(1) NOT NULL DEFAULT '0'"

                + (sqlite ? "" : ",PRIMARY KEY(`id`)")
                + ",UNIQUE" + (sqlite ? "" : " KEY") + "(`world`,`create_time`)"
                + ")" + (sqlite ? "" : "ENGINE = InnoDB,COMMENT = 'version:" + GAMES_VERSION + "'");
    }

    static String createStatsTable(boolean sqlite) {
        return "CREATE TABLE `" + STATS_TABLE + "` ("
                + "`id` " + (sqlite ? "INTEGER PRIMARY KEY" : "INT UNSIGNED NOT NULL AUTO_INCREMENT")
                + ",`player_id` INT UNSIGNED NOT NULL"
                + ",`game_id` INT UNSIGNED NOT NULL"
                + ",`started_in` TINYINT(1) NOT NULL DEFAULT '0'"
                + ",`joined_in` TINYINT(1) NOT NULL DEFAULT '0'"
                + ",`finished_in` TINYINT(1) NOT NULL DEFAULT '0'"
                + ",`is_zombie` TINYINT(1) NOT NULL DEFAULT '0'"
                + ",`first_zombie` TINYINT(1) NOT NULL DEFAULT '0'"
                + ",`last_human` TINYINT(1) NOT NULL DEFAULT '0'"
                + ",`kit_used` VARCHAR(255)"

                + (sqlite ? "" : ",PRIMARY KEY(`id`)")
                + ",UNIQUE" + (sqlite ? "" : " KEY") + "(`player_id`,`game_id`)"
                + ",FOREIGN KEY(`player_id`) REFERENCES `" + PLAYERS_TABLE + "`(`id`)"
                + ",FOREIGN KEY(`game_id`) REFERENCES `" + GAMES_TABLE + "`(`id`)"
                + ")" + (sqlite ? "" : "ENGINE = InnoDB,COMMENT = 'version:" + STATS_VERSION + "'");
    }

    static String createTypeHistoryTable(boolean sqlite) {
        return "CREATE TABLE `" + TYPE_HISTORY_TABLE + "` ("
                + "`id` " + (sqlite ? "INTEGER PRIMARY KEY" : "INT UNSIGNED NOT NULL AUTO_INCREMENT")
                + ",`player_id` INT UNSIGNED NOT NULL"
                + ",`game_id` INT UNSIGNED NOT NULL"
                + ",`player_type` INT UNSIGNED NOT NULL"
                + ",`time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP"

                + (sqlite ? "" : ",PRIMARY KEY(`id`)")
                + ",FOREIGN KEY(`player_id`) REFERENCES `" + PLAYERS_TABLE + "`(`id`)"
                + ",FOREIGN KEY(`game_id`) REFERENCES `" + GAMES_TABLE + "`(`id`)"
                + ",FOREIGN KEY(`player_type`) REFERENCES `" + PLAYER_TYPE_TABLE + "`(`id`)"
                + ")" + (sqlite ? "" : "ENGINE = InnoDB,COMMENT = 'version:" + TYPE_HISTORY_VERSION + "'");
    }

    static String createKillsTable(boolean sqlite) {
        return "CREATE TABLE `" + KILLS_TABLE + "` ("
                + "`id` " + (sqlite ? "INTEGER PRIMARY KEY" : "INT UNSIGNED NOT NULL AUTO_INCREMENT")
                + ",`killer_id` INT UNSIGNED"
                + ",`killer_type` INT UNSIGNED"
                + ",`victim_id` INT UNSIGNED NOT NULL"
                + ",`victim_type` INT UNSIGNED"
                + ",`game_id` INT UNSIGNED NOT NULL"
                + ",`time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                + ",`weapon` INT NOT NULL DEFAULT '0'"

                + (sqlite ? "" : ",PRIMARY KEY(`id`)")
                + ",FOREIGN KEY(`killer_id`) REFERENCES `" + PLAYERS_TABLE + "`(`id`)"
                + ",FOREIGN KEY(`killer_type`) REFERENCES `" + PLAYER_TYPE_TABLE + "`(`id`)"
                + ",FOREIGN KEY(`victim_id`) REFERENCES `" + PLAYERS_TABLE + "`(`id`)"
                + ",FOREIGN KEY(`victim_type`) REFERENCES `" + PLAYER_TYPE_TABLE + "`(`id`)"
                + ",FOREIGN KEY(`game_id`) REFERENCES `" + GAMES_TABLE + "`(`id`)"
                + ")" + (sqlite ? "" : "ENGINE = InnoDB,COMMENT = 'version:" + KILLS_VERSION + "'");
    }

    static String createGrandTotalKillsView() {
        return "CREATE VIEW IF NOT EXISTS `grand_total_kills` AS" +
                " SELECT " +
                "`" + KILLS_TABLE + "`.`killer_id`," +
                "`" + KILLS_TABLE + "`.`victim_type`," +
                "count(`" + KILLS_TABLE + "`.`killer_id`) AS `kill_count`" +
                " FROM `" + KILLS_TABLE + "`" +
                " GROUP BY `" + KILLS_TABLE + "`.`killer_id` ,`" + KILLS_TABLE + "`.`victim_type`" +
                " ORDER BY count(`" + KILLS_TABLE + "`.`killer_id`) DESC";
    }

    static String addPlayerType(final boolean sqlite) {
        return "INSERT " + (sqlite ? "OR " : "") + "IGNORE INTO `" + PLAYER_TYPE_TABLE + "` (`type_name`) VALUES (?)";
    }

    static String createGame() {
        return "INSERT INTO `" + GAMES_TABLE + "` "
                + "(`world`,`create_time`) VALUES "
                + "(?,?)";
    }

    static String startGame() {
        return "UPDATE `" + GAMES_TABLE + "` SET "
                + "`start_time`=? WHERE "
                + "`id`=?";
    }

    static String endGame() {
        return "UPDATE `" + GAMES_TABLE + "` SET `end_time`=?,`humans_won`=? WHERE `id`=?";
    }

    static String createPlayer() {
        return "INSERT INTO `" + PLAYERS_TABLE + "` (`player_name`) VALUES (?)";
    }

    static String updatePlayer() {
        return "UPDATE `" + PLAYERS_TABLE + "` SET "
                + "`player_name`=?,`current_type`=?,`kit_selected`=? "
                + "WHERE `id`=?";
    }

    static String getPlayer() {
        return "SELECT `id` FROM `" + PLAYERS_TABLE + "` "
                + "WHERE `player_name`=?";
    }

    static String createPlayerStats() {
        return "INSERT INTO `" + STATS_TABLE + "` ("
                + "`player_id`,`game_id`,`started_in`,`joined_in`,`finished_in`"
                + ",`is_zombie`,`first_zombie`,`last_human`,`kit_used`"
                + ") VALUES (?,?,?,?,?,?,?,?,?)";
    }

    static String updatePlayerStats() {
        return "UPDATE `" + STATS_TABLE + "` SET "
                + "`started_in`=?"
                + ",`joined_in`=?"
                + ",`finished_in`=?"
                + ",`is_zombie`=?"
                + ",`first_zombie`=?"
                + ",`last_human`=?"
                + ",`kit_used`=?"
                + " WHERE `player_id`=? AND `game_id`=?";
    }

    static String getPlayerTypeId() {
        return "SELECT `id` FROM `" + PLAYER_TYPE_TABLE + "` WHERE `type_name`=?";
    }

    static String playerKilled() {
        return "INSERT INTO `" + KILLS_TABLE + "` ("
                + "`killer_id`,`killer_type`,`victim_id`,`victim_type`,`game_id`,`time`,`weapon`) "
                + "VALUES (?,?,?,?,?,?,?)";
    }

    static String playerTypeChange() {
        return "INSERT INTO `" + TYPE_HISTORY_TABLE + "` (`player_id`,`game_id`,`player_type`,`time`) VALUES (?,?,?,?)";
    }
}
