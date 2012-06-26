package com.dumptruckman.minecraft.zombiefight.task;

import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class CountdownTask extends GameTask {

    private int countdown = -1;
    private Set<Integer> warnings = new HashSet<Integer>();

    public CountdownTask(Game game, ZombieFight plugin) {
        super(game, plugin, 20L, 20L);
        warnings = new HashSet<Integer>();
    }

    protected final void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    protected final void setWarnings(Collection<Integer> warnings) {
        this.warnings.addAll(warnings);
    }



    @Override
    public final void run() {
        if (!dead && countdown > 0 && !shouldEnd()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), this, 20L);
        }
        if (shouldCountdown()) {
            countdown--;
            if (countdown <= 0) {
                countdownFinished();
            } else if (warnings.contains(countdown)) {
                countdownWarning(countdown);
            }
        }
        onRun();
    }

    public abstract boolean shouldCountdown();

    public abstract void countdownWarning(int warning);

    public abstract void countdownFinished();
}
