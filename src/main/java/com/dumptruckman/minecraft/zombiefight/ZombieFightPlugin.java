package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.GameManager;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.command.CheckCommand;
import com.dumptruckman.minecraft.zombiefight.util.CommentedConfig;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.pluginbase.plugin.AbstractBukkitPlugin;
import com.dumptruckman.minecraft.pluginbase.plugin.command.HelpCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ZombieFightPlugin extends AbstractBukkitPlugin<ZFConfig> implements ZombieFight {

    private final List<String> cmdPrefixes = Arrays.asList("zf");

    private GameManager gameManager = null;

    @Override
    protected ZFConfig newConfigInstance() throws IOException {
        return new CommentedConfig(this, true, new File(getDataFolder(), "config.yml"), ZFConfig.class);
    }

    @Override
    public void preEnable() {
        Language.init();
        HelpCommand.addStaticPrefixedKey("");
    }

    @Override
    public void postEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ZombieFightListener(this), this);
        getCommandHandler().registerCommand(new CheckCommand(this));
    }

    @Override
    public void preReload() {
        gameManager = null;
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        super.onDisable();
    }

    @Override
    public List<String> getCommandPrefixes() {
        return cmdPrefixes;
    }

    @Override
    public List<String> dumpVersionInfo() {
        List<String> versionInfo = new LinkedList<String>();
        return versionInfo;
    }

    @Override
    public GameManager getGameManager() {
        if (gameManager == null) {
            gameManager = new DefaultGameManager(this);
        }
        return gameManager;
    }

    @Override
    public void broadcastWorld(String worldName, String message) {
        World world = Bukkit.getWorld(worldName);
        if (worldName == null) {
            Logging.warning("World '" + worldName + "' missing!  (This shouldn't happen)");
            Logging.warning("Could not broadcast: " + message);
            return;
        }
        for (Player player : world.getPlayers()) {
            player.sendMessage(message);
        }
    }

    @Override
    public Collection<String> getPlayersForWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        Collection<String> players = new LinkedList<String>();
        for (Player player : world.getPlayers()) {
            players.add(player.getName());
        }
        return players;
    }

    @Override
    public void zombifyPlayer(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            Bukkit.broadcastMessage("Could not zombify: " + name);
            return;
        }
        World world = player.getWorld();
        broadcastWorld(world.getName(), getMessager().getMessage(Language.PLAYER_ZOMBIFIED, player.getName()));
    }
}
