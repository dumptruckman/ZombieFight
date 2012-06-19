package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GameManager;
import com.dumptruckman.minecraft.zombiefight.api.LootConfig;
import com.dumptruckman.minecraft.zombiefight.api.LootTable;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.command.BorderCommand;
import com.dumptruckman.minecraft.zombiefight.command.DisableGameCommand;
import com.dumptruckman.minecraft.zombiefight.command.EnableGameCommand;
import com.dumptruckman.minecraft.zombiefight.command.EndGameCommand;
import com.dumptruckman.minecraft.zombiefight.command.GameSpawnCommand;
import com.dumptruckman.minecraft.zombiefight.command.KitCommand;
import com.dumptruckman.minecraft.zombiefight.command.PreGameSpawnCommand;
import com.dumptruckman.minecraft.zombiefight.command.StartGameCommand;
import com.dumptruckman.minecraft.zombiefight.util.CommentedConfig;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.pluginbase.plugin.AbstractBukkitPlugin;
import com.dumptruckman.minecraft.pluginbase.plugin.command.HelpCommand;
import com.dumptruckman.minecraft.zombiefight.util.Perms;
import me.desmin88.mobdisguise.api.MobDisguiseAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ZombieFightPlugin extends AbstractBukkitPlugin<ZFConfig> implements ZombieFight {

    private final List<String> cmdPrefixes = Arrays.asList("zf");

    private GameManager gameManager = null;
    private LootConfig lootConfig = null;
    private boolean mobDisguise = false;
    private ZombieFightListener listener;
    private Set<Integer> countdownWarnings = new HashSet<Integer>();
    private Map<String, String> playerKits = new HashMap<String, String>();

    @Override
    protected ZFConfig newConfigInstance() throws IOException {
        return new CommentedConfig(this, true, new File(getDataFolder(), "config.yml"), ZFConfig.class);
    }

    @Override
    public void preEnable() {
        Language.init();
        HelpCommand.addStaticPrefixedKey("");
        listener = new ZombieFightListener(this);
    }

    @Override
    public void postEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(listener, this);
        getCommandHandler().registerCommand(new PreGameSpawnCommand(this));
        getCommandHandler().registerCommand(new GameSpawnCommand(this));
        getCommandHandler().registerCommand(new StartGameCommand(this));
        getCommandHandler().registerCommand(new EndGameCommand(this));
        getCommandHandler().registerCommand(new EnableGameCommand(this));
        getCommandHandler().registerCommand(new DisableGameCommand(this));
        getCommandHandler().registerCommand(new KitCommand(this));
        getCommandHandler().registerCommand(new BorderCommand(this));
        Plugin plugin = pm.getPlugin("MobDisguise");
        if (plugin != null) {
            Logging.info("Hooked MobDisguise!");
            mobDisguise = true;
        }
        getLootConfig();
    }

    @Override
    public void preReload() {
        for (World world : Bukkit.getWorlds()) {
            Game game = getGameManager().getGame(world.getName());
            if (game != null) {
                game.broadcast(Language.PLUGIN_RELOAD);
                game.forceEnd(false);
            }
        }
        gameManager = null;
        lootConfig = null;
    }

    @Override
    public void postReload() {
        listener.resetBorderDamager();
        for (World world : Bukkit.getWorlds()) {
            if (!getGameManager().isWorldEnabled(world.getName())) {
                continue;
            }
            getGameManager().newGame(world.getName());
        }
        countdownWarnings = new HashSet<Integer>(config().getList(ZFConfig.COUNTDOWN_WARNINGS));
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
        Logging.fine("'" + worldName + "' broadcast: " + message);
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
        Logging.finer("Zombifying " + name);
        Player player = getServer().getPlayerExact(name);
        if (player == null) {
            Bukkit.broadcastMessage("Could not zombify: " + name);
            return;
        }
        World world = player.getWorld();
        broadcastWorld(world.getName(), getMessager().getMessage(Language.PLAYER_ZOMBIFIED, player.getName()));
        disguiseAsZombie(player);
        player.getInventory().clear();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(5F);
        player.setExhaustion(0F);
        getMessager().normal(Language.YOU_ARE_ZOMBIE, player);
    }

    @Override
    public void unZombifyPlayer(String name) {
        Logging.finer("Unzombifying " + name);
        Player player = getServer().getPlayerExact(name);
        if (player == null) {
            Bukkit.broadcastMessage("Could not un-zombify: " + name);
            return;
        }
        unDisguiseAsZombie(player);
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

    public LootConfig getLootConfig() {
        if (lootConfig == null) {
            lootConfig = new DefaultLootConfig(this);
        }
        return lootConfig;
    }

    @Override
    public boolean shouldWarn(int time) {
        return countdownWarnings.contains(time);
    }

    @Override
    public void setPlayerKit(String name, String kit) {
        playerKits.put(name, kit);
    }

    @Override
    public String getPlayerKit(String name) {
        return playerKits.get(name);
    }

    public void displayKits(Player player) {
        String[] kits = getLootConfig().getKitNames();
        StringBuilder kitList = new StringBuilder();
        for (String kit : kits) {
            if (!Perms.KIT.specific(kit).hasPermission(player)) {
                continue;
            }
            if (!kitList.toString().isEmpty()) {
                kitList.append(", ");
            }
            kitList.append(ChatColor.AQUA).append(kit).append(ChatColor.WHITE);
        }
        getMessager().normal(Language.CMD_KIT_LIST, player, kitList.toString());
    }
}
