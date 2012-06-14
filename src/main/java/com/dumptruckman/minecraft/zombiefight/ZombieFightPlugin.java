package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.GameManager;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.command.DisableGameCommand;
import com.dumptruckman.minecraft.zombiefight.command.EnableGameCommand;
import com.dumptruckman.minecraft.zombiefight.command.EndGameCommand;
import com.dumptruckman.minecraft.zombiefight.command.GameSpawnCommand;
import com.dumptruckman.minecraft.zombiefight.command.PreGameSpawnCommand;
import com.dumptruckman.minecraft.zombiefight.command.StartGameCommand;
import com.dumptruckman.minecraft.zombiefight.util.CommentedConfig;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.pluginbase.plugin.AbstractBukkitPlugin;
import com.dumptruckman.minecraft.pluginbase.plugin.command.HelpCommand;
import me.desmin88.mobdisguise.MobDisguise;
import me.desmin88.mobdisguise.api.MobDisguiseAPI;
import net.minecraft.server.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ZombieFightPlugin extends AbstractBukkitPlugin<ZFConfig> implements ZombieFight {

    private final List<String> cmdPrefixes = Arrays.asList("zf");

    private GameManager gameManager = null;
    private boolean mobDisguise = false;

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
        getCommandHandler().registerCommand(new PreGameSpawnCommand(this));
        getCommandHandler().registerCommand(new GameSpawnCommand(this));
        getCommandHandler().registerCommand(new StartGameCommand(this));
        getCommandHandler().registerCommand(new EndGameCommand(this));
        getCommandHandler().registerCommand(new EnableGameCommand(this));
        getCommandHandler().registerCommand(new DisableGameCommand(this));
        Plugin plugin = pm.getPlugin("MobDisguise");
        if (plugin != null) {
            Logging.info("Hooked MobDisguise!");
            mobDisguise = true;
        }
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
            getMessager().sendMessage(player, message);
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
        player.setFoodLevel(20);
        player.setHealth(20);
        disguiseAsZombie(player);
        reddenName(player);
    }

    @Override
    public void unZombifyPlayer(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            Bukkit.broadcastMessage("Could not un-zombify: " + name);
            return;
        }
        unDisguiseAsZombie(player);
        unReddenName(player);
    }

    public void reddenName(Player player) {
        /*
        EntityPlayer ePlayer = ((CraftPlayer) player).getHandle();
        ePlayer.name = ChatColor.RED + player.getName();
        */
    }

    public void unReddenName(Player player) {
        /*
        EntityPlayer ePlayer = ((CraftPlayer) player).getHandle();
        ePlayer.name = ChatColor.stripColor(player.getName());
        */
    }

    private void disguiseAsZombie(Player player) {
        if (mobDisguise) {
            MobDisguiseAPI.disguisePlayer(player, "zombie");
        }
    }

    private void unDisguiseAsZombie(Player player) {
        if (mobDisguise) {
            MobDisguiseAPI.undisguisePlayer(player);
        }
    }
}
