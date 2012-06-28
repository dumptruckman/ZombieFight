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
    static final String KILLS_TABLE = "zf_kills";
    static final int KILLS_VERSION = 0;

    static String createPlayersTable() {
        return "CREATE TABLE `" + PLAYERS_TABLE + "` ("
                + "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT"
                + ",`player_name` VARCHAR(64) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL"
                + ",PRIMARY KEY(`id`)"
                + ") ENGINE = InnoDB,COMMENT = 'version:" + PLAYERS_VERSION + "'";
    }

    static String createPlayerTypeTable() {
        return "CREATE TABLE `" + PLAYER_TYPE_TABLE + "` ("
                + "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT"
                + ",`type_name` VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL"
                + ",PRIMARY KEY(`id`)"
                + ") ENGINE = InnoDB,COMMENT = 'version:" + PLAYER_TYPE_VERSION + "'";
    }

    static String createGamesTable() {
        return "CREATE TABLE `" + GAMES_TABLE + "` ("
                + "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT"
                + ",`start_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ",`end_time` TIMESTAMP"
                + ",`humans_won` TINYINT(1) NOT NULL DEFAULT '0'"
                + ",PRIMARY KEY(`id`)"
                + ") ENGINE = InnoDB,COMMENT = 'version:" + GAMES_VERSION + "'";
    }

    static String createStatsTable() {
        return "CREATE TABLE `" + STATS_TABLE + "` ("
                + "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT"
                + ",`player_id` INT UNSIGNED NOT NULL"
                + ",`game_id` INT UNSIGNED NOT NULL"
                + ",`started_in` TINYINT(1) NOT NULL DEFAULT '0'"
                + ",`joined_in` TINYINT(1) NOT NULL DEFAULT '0'"
                + ",`finished_in` TINYINT(1) NOT NULL DEFAULT '0'"
                + ",`is_zombie` TINYINT(1) NOT NULL DEFAULT '0'"
                + ",`first_zombie` TINYINT(1) NOT NULL DEFAULT '0'"
                + ",`zombie_kills` INT NOT NULL DEFAULT '0'"
                + ",`human_kills` INT NOT NULL DEFAULT '0'"
                + ",`zombie_deaths` INT NOT NULL DEFAULT '0'"
                + ",`human_deaths` INT NOT NULL DEFAULT '0'"
                + ",`kit_used` VARCHAR(255)"
                + ",PRIMARY KEY(`id`)"
                + ",FOREIGN KEY(`player_id`) REFERENCES `" + PLAYERS_TABLE + "`(`id`)"
                + ",FOREIGN KEY(`game_id`) REFERENCES `" + GAMES_TABLE + "`(`id`)"
                + ") ENGINE = InnoDB,COMMENT = 'version:" + STATS_VERSION + "'";
    }

    static String createKillsTable() {
        return "CREATE TABLE `" + KILLS_TABLE + "` ("
                + "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT"
                + ",`killer_id` INT UNSIGNED NOT NULL"
                + ",`killer_type` INT UNSIGNED NOT NULL"
                + ",`victim_id` INT UNSIGNED NOT NULL"
                + ",`victim_type` INT UNSIGNED NOT NULL"
                + ",`game_id` INT UNSIGNED NOT NULL"
                + ",`time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                + ",`weapon` INT UNSIGNED NOT NULL DEFAULT '0'"
                + ",`killer_is_human` TINYINT(1) NOT NULL"
                + ",PRIMARY KEY(`id`)"
                + ",FOREIGN KEY(`killer_id`) REFERENCES `" + PLAYERS_TABLE + "`(`id`)"
                + ",FOREIGN KEY(`killer_type`) REFERENCES `" + PLAYER_TYPE_TABLE + "`(`id`)"
                + ",FOREIGN KEY(`victim_id`) REFERENCES `" + PLAYERS_TABLE + "`(`id`)"
                + ",FOREIGN KEY(`victim_type`) REFERENCES `" + PLAYER_TYPE_TABLE + "`(`id`)"
                + ",FOREIGN KEY(`game_id`) REFERENCES `" + GAMES_TABLE + "`(`id`)"
                + ") ENGINE = InnoDB,COMMENT = 'version:" + KILLS_VERSION + "'";
    }
}
