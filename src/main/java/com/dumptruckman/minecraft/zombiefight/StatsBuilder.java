package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GamePlayer;
import com.dumptruckman.minecraft.zombiefight.api.GameStats;

public class StatsBuilder {

    public static StatsBuilder createStats(GamePlayer gamePlayer) {
        final StatsBuilder statsBuilder = new StatsBuilder(gamePlayer);
        if (gamePlayer.getGameStats() != null) {
            final GameStats oldStats = gamePlayer.getGameStats();
            statsBuilder.dbInfo = oldStats.getDBInfo();
            statsBuilder.startedInGame = oldStats.startedInGame();
            statsBuilder.joinedInGame = oldStats.joinedInGame();
            statsBuilder.finishedInGame = oldStats.finishedInGame();
            statsBuilder.zombie = oldStats.isZombie();
            statsBuilder.firstZombie = oldStats.isFirstZombie();
            statsBuilder.lastHuman = oldStats.isLastHuman();
            statsBuilder.kitUsed = oldStats.getKitUsed();
        }
        return statsBuilder;
    }

    private final GamePlayer gamePlayer;

    private DBInfo dbInfo = new DBInfo();
    private boolean startedInGame = false;
    private boolean joinedInGame = false;
    private boolean finishedInGame = false;
    private boolean zombie = false;
    private boolean firstZombie = false;
    private boolean lastHuman = false;
    private String kitUsed = "";

    private StatsBuilder(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public StatsBuilder startedInGame(final boolean startedInGame) {
        this.startedInGame = startedInGame;
        return this;
    }
    public StatsBuilder joinedInGame(final boolean joinedInGame) {
        this.joinedInGame = joinedInGame;
        return this;
    }
    public StatsBuilder finishedInGame(final boolean finishedInGame) {
        this.finishedInGame = finishedInGame;
        return this;
    }
    public StatsBuilder zombie(final boolean zombie) {
        this.zombie = zombie;
        return this;
    }
    public StatsBuilder firstZombie(final boolean firstZombie) {
        this.firstZombie = firstZombie;
        return this;
    }
    public StatsBuilder lastHuman(final boolean lastHuman) {
        this.lastHuman = lastHuman;
        return this;
    }
    public StatsBuilder kitUsed(final String kitUsed) {
        this.kitUsed = kitUsed;
        return this;
    }
    public GameStats build() {
        return new DefaultGameStats(gamePlayer.getGame(), gamePlayer, dbInfo, startedInGame, joinedInGame, finishedInGame, zombie, firstZombie, lastHuman, kitUsed);
    }

    private static class DefaultGameStats implements GameStats {

        private final Game game;
        private final GamePlayer gamePlayer;
        private final DBInfo dbInfo;

        private final boolean startedInGame;
        private final boolean joinedInGame;
        private final boolean finishedInGame;
        private final boolean zombie;
        private final boolean firstZombie;
        private final boolean lastHuman;
        private final String kitUsed;

        DefaultGameStats(final Game game, final GamePlayer gamePlayer, final DBInfo dbInfo, final boolean startedInGame,
                         final boolean joinedInGame, final boolean finishedInGame, final boolean zombie,
                         final boolean firstZombie, final boolean lastHuman, final String kitUsed) {
            this.game = game;
            this.gamePlayer = gamePlayer;
            this.dbInfo = dbInfo;
            this.startedInGame = startedInGame;
            this.joinedInGame = joinedInGame;
            this.finishedInGame = finishedInGame;
            this.zombie = zombie;
            this.firstZombie = firstZombie;
            this.lastHuman = lastHuman;
            this.kitUsed = kitUsed;
        }

        @Override
        public DBInfo getDBInfo() {
            return dbInfo;
        }

        @Override
        public GamePlayer getGamePlayer() {
            return gamePlayer;
        }

        @Override
        public Game getGame() {
            return game;
        }

        @Override
        public boolean startedInGame() {
            return startedInGame;
        }

        @Override
        public boolean joinedInGame() {
            return joinedInGame;
        }

        @Override
        public boolean finishedInGame() {
            return finishedInGame;
        }

        @Override
        public boolean isZombie() {
            return zombie;
        }

        @Override
        public boolean isFirstZombie() {
            return firstZombie;
        }

        @Override
        public boolean isLastHuman() {
            return lastHuman;
        }

        @Override
        public String getKitUsed() {
            return kitUsed;
        }
    }
}
