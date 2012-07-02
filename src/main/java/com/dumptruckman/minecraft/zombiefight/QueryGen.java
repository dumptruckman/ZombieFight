package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.zombiefight.api.PlayerType;

import java.sql.Timestamp;

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

    static String createPlayerTypeTable() {
        return "CREATE TABLE `" + PLAYER_TYPE_TABLE + "` ("
                + "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT"
                + ",`type_name` VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL"

                + ",PRIMARY KEY(`id`)"
                + ",UNIQUE KEY(`type_name`)"
                + ") ENGINE = InnoDB,COMMENT = 'version:" + PLAYER_TYPE_VERSION + "'";
    }

    static String createPlayersTable() {
        return "CREATE TABLE `" + PLAYERS_TABLE + "` ("
                + "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT"
                + ",`player_name` VARCHAR(64) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL"
                + ",`current_type` INT UNSIGNED"
                + ",`kit_selected` VARCHAR(255)"

                + ",PRIMARY KEY(`id`)"
                + ",UNIQUE KEY(`player_name`)"
                + ",FOREIGN KEY(`current_type`) REFERENCES `" + PLAYER_TYPE_TABLE + "`(`id`)"
                + ") ENGINE = InnoDB,COMMENT = 'version:" + PLAYERS_VERSION + "'";
    }

    static String createGamesTable() {
        return "CREATE TABLE `" + GAMES_TABLE + "` ("
                + "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT"
                + ",`world` VARCHAR(255) NOT NULL"
                + ",`create_time` TIMESTAMP NOT NULL"
                + ",`start_time` TIMESTAMP"
                + ",`end_time` TIMESTAMP"
                + ",`humans_won` TINYINT(1) NOT NULL DEFAULT '0'"

                + ",PRIMARY KEY(`id`)"
                + ",UNIQUE KEY(`world`,`create_time`)"
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
                + ",`kit_used` VARCHAR(255)"

                + ",PRIMARY KEY(`id`)"
                + ",UNIQUE KEY(`player_id`,`game_id`)"
                + ",FOREIGN KEY(`player_id`) REFERENCES `" + PLAYERS_TABLE + "`(`id`)"
                + ",FOREIGN KEY(`game_id`) REFERENCES `" + GAMES_TABLE + "`(`id`)"
                + ") ENGINE = InnoDB,COMMENT = 'version:" + STATS_VERSION + "'";
    }

    static String createTypeHistoryTable() {
        return "CREATE TABLE `" + TYPE_HISTORY_TABLE + "` ("
                + "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT"
                + ",`player_id` INT UNSIGNED NOT NULL"
                + ",`game_id` INT UNSIGNED NOT NULL"
                + ",`player_type` INT UNSIGNED NOT NULL"
                + ",`time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP"

                + ",PRIMARY KEY(`id`)"
                + ",FOREIGN KEY(`player_id`) REFERENCES `" + PLAYERS_TABLE + "`(`id`)"
                + ",FOREIGN KEY(`game_id`) REFERENCES `" + GAMES_TABLE + "`(`id`)"
                + ",FOREIGN KEY(`player_type`) REFERENCES `" + PLAYER_TYPE_TABLE + "`(`id`)"
                + ") ENGINE = InnoDB,COMMENT = 'version:" + TYPE_HISTORY_VERSION + "'";
    }

    static String createKillsTable() {
        return "CREATE TABLE `" + KILLS_TABLE + "` ("
                + "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT"
                + ",`killer_id` INT UNSIGNED"
                + ",`killer_type` INT UNSIGNED"
                + ",`victim_id` INT UNSIGNED NOT NULL"
                + ",`victim_type` INT UNSIGNED"
                + ",`game_id` INT UNSIGNED NOT NULL"
                + ",`time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                + ",`weapon` INT NOT NULL DEFAULT '0'"

                + ",PRIMARY KEY(`id`)"
                + ",FOREIGN KEY(`killer_id`) REFERENCES `" + PLAYERS_TABLE + "`(`id`)"
                + ",FOREIGN KEY(`killer_type`) REFERENCES `" + PLAYER_TYPE_TABLE + "`(`id`)"
                + ",FOREIGN KEY(`victim_id`) REFERENCES `" + PLAYERS_TABLE + "`(`id`)"
                + ",FOREIGN KEY(`victim_type`) REFERENCES `" + PLAYER_TYPE_TABLE + "`(`id`)"
                + ",FOREIGN KEY(`game_id`) REFERENCES `" + GAMES_TABLE + "`(`id`)"
                + ") ENGINE = InnoDB,COMMENT = 'version:" + KILLS_VERSION + "'";
    }

    static String createGame(Timestamp time, String world) {
        return "INSERT INTO `" + GAMES_TABLE + "` "
                + "(`world`,`create_time`) VALUES "
                + "('" + world + "','" + time + "')";
    }

    static String getGame(Timestamp time, String world) {
        return "SELECT `id` FROM `" + GAMES_TABLE + "` "
                + "WHERE `world`='" + world + "' AND `create_time`='" + time + "'";
    }

    static String startGame(int id, Timestamp startTime) {
        return "UPDATE `" + GAMES_TABLE + "` SET "
                + "`start_time`='" + startTime + "' WHERE "
                + "`id`='" + id + "'";
    }

    static String endGame(int id, Timestamp endTme, boolean humansWon) {
        return "UPDATE `" + GAMES_TABLE + "` SET "
                + "`end_time`='" + endTme + "'"
                + (humansWon ? ",`humans_won`='1'" : "") + " WHERE "
                + "`id`='" + id + "'";
    }

    static String updatePlayer(String name, Integer typeId, String kit) {
        return "INSERT IGNORE INTO `" + PLAYERS_TABLE + "` ("
                + "`player_name`"
                + (typeId != null ? ",`current_type`" : "")
                + ",`kit_selected`"
                + ") VALUES ("
                + "'" + name + "'"
                + (typeId != null ? ",'" + typeId + "'" : "")
                + (kit != null ? ",'" + kit + "'" : ",''") + ")"
                + " ON DUPLICATE KEY UPDATE "
                + (typeId != null ? "`current_type`='" + typeId + "'," : "")
                + "`kit_selected`='" + (kit != null ? kit : "") + "'";
    }

    static String getPlayer(String name) {
        return "SELECT `id` FROM `" + PLAYERS_TABLE + "` "
                + "WHERE `player_name`='" + name + "'";
    }

    static String playerStartingInGame(int playerId, int gameId, boolean isZombie, String kit) {
        return "INSERT INTO `" + STATS_TABLE + "` ("
                + "`player_id`,`game_id`,`started_in`"
                + (isZombie ? ",`is_zombie`,`first_zombie`" : "")
                + (kit != null ? ",`kit_used`" : "") + ") VALUES ("
                + "'" + playerId + "','" + gameId + "','1'"
                + (isZombie ? ",'1','1'" : "")
                + (kit != null ? ",'" + kit + "'" : "") + ")";
    }

    static String playerJoiningInGame(int playerId, int gameId, boolean isZombie, String kit) {
        return "INSERT IGNORE INTO `" + STATS_TABLE + "` ("
                + "`player_id`,`game_id`,`joined_in`"
                + (isZombie ? ",`is_zombie`" : "")
                + (kit != null ? ",`kit_used`" : "") + ") VALUES ("
                + "'" + playerId + "','" + gameId + "','1'"
                + (isZombie ? ",'1'" : "")
                + (kit != null ? ",'" + kit + "'" : "") + ") "
                + "ON DUPLICATE KEY UPDATE "
                + "`joined_in`='1'"
                + (isZombie ? ",`is_zombie`='1'" : "")
                + (kit != null ? ",`kit_used`='" + kit + "'" : "");
    }

    static String playerFinishingInGame(int playerId, int gameId, boolean isZombie) {
        return "UPDATE `" + STATS_TABLE + "` SET "
                + "`finished_in`='1'"
                + (isZombie ? ",`is_zombie`='1'" : "")
                + " WHERE `player_id`='" + playerId + "' AND `game_id`='" + gameId + "'";
    }

    static String addPlayerType(PlayerType type) {
        return "INSERT IGNORE INTO `" + PLAYER_TYPE_TABLE + "` ("
                + "`type_name`) VALUES ("
                + "'" + type + "')";
    }

    static String getPlayerTypeId(PlayerType type) {
        return "SELECT `id` FROM `" + PLAYER_TYPE_TABLE + "` WHERE "
                + "`type_name`='" + type + "'";
    }

    static String playerKilled(int killerId, Integer killerType,
                               int victimId, Integer victimType,
                               int gameId, Timestamp time, int weaponId) {
        return "INSERT INTO `" + KILLS_TABLE + "` ("
                + (killerId > -1 ? "`killer_id`,`killer_type`," : "") + "`victim_id`"
                + (victimType != null ? ",`victim_type`" : "")
                + ",`game_id`,`time`,`weapon`) VALUES ("
                + (killerId > -1 ?  "'" + killerId + "','" + killerType + "'," : "")
                + "'" + victimId + "'"
                + (victimType != null ? ",'" + victimType + "'" : "")
                + ",'" + gameId + "'"
                + ",'" + time + "'"
                + ",'" + weaponId + "')";
    }

    static String playerTypeChange(int playerId, int gameId, int typeId) {
        return "INSERT INTO `" + TYPE_HISTORY_TABLE + "` ("
                + "`player_id`,`game_id`,`player_type`) VALUES ("
                + "'" + playerId + "','" + gameId + "','" + typeId + "')";
    }
}
