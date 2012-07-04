package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.plugin.AbstractBukkitPlugin;
import com.dumptruckman.minecraft.pluginbase.plugin.command.HelpCommand;
import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Disguiser;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GameManager;
import com.dumptruckman.minecraft.zombiefight.api.LootConfig;
import com.dumptruckman.minecraft.zombiefight.api.StatsDatabase;
import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.command.BorderCommand;
import com.dumptruckman.minecraft.zombiefight.command.CleanupCommand;
import com.dumptruckman.minecraft.zombiefight.command.DisableGameCommand;
import com.dumptruckman.minecraft.zombiefight.command.EnableGameCommand;
import com.dumptruckman.minecraft.zombiefight.command.EndGameCommand;
import com.dumptruckman.minecraft.zombiefight.command.GameSpawnCommand;
import com.dumptruckman.minecraft.zombiefight.command.KitCommand;
import com.dumptruckman.minecraft.zombiefight.command.PreGameSpawnCommand;
import com.dumptruckman.minecraft.zombiefight.command.StartGameCommand;
import com.dumptruckman.minecraft.zombiefight.util.CommentedConfig;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import com.dumptruckman.minecraft.zombiefight.util.Perms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
    private Disguiser disguiser = null;
    private ZombieFightListener listener;
    private TestModeListener testListener = new TestModeListener(this);
    private Map<String, String> playerKits = new HashMap<String, String>();
    private Set<String> cleaners = new HashSet<String>();
    private StatsDatabase statsDatabase = null;

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
        pm.registerEvents(new GameMonitor(this), this);
        if (config().get(ZFConfig.TEST_MODE)) {
            pm.registerEvents(testListener, this);
        }
        getCommandHandler().registerCommand(new PreGameSpawnCommand(this));
        getCommandHandler().registerCommand(new GameSpawnCommand(this));
        getCommandHandler().registerCommand(new StartGameCommand(this));
        getCommandHandler().registerCommand(new EndGameCommand(this));
        getCommandHandler().registerCommand(new EnableGameCommand(this));
        getCommandHandler().registerCommand(new DisableGameCommand(this));
        getCommandHandler().registerCommand(new KitCommand(this));
        getCommandHandler().registerCommand(new BorderCommand(this));
        getCommandHandler().registerCommand(new CleanupCommand(this));
        getLootConfig();
    }

    @Override
    public void preReload() {
        for (World world : Bukkit.getWorlds()) {
            Logging.finest("Shutting down games for world " + world);
            Game game = getGameManager().getGame(world);
            if (game.isEnabled()) {
                game.broadcast(Language.PLUGIN_RELOAD);
                game.forceEnd(false);
            }
        }
        if (disguiser != null) {
            disguiser.terminate();
        }
        statsDatabase = null;
        gameManager = null;
        lootConfig = null;
        disguiser = null;
        statsDatabase = null;
    }

    @Override
    public void postReload() {
        statsDatabase = new DefaultStatsDatabase(this);
        disguiser = new DefaultDisguiser(this);
        listener.resetBorderDamager();
        for (World world : Bukkit.getWorlds()) {
            getGameManager().getGame(world);
        }
    }

    @Override
    public void preDisable() {
        preReload();
        getServer().getScheduler().cancelTasks(this);
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

    public LootConfig getLootConfig() {
        if (lootConfig == null) {
            lootConfig = new DefaultLootConfig(this);
        }
        return lootConfig;
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

    @Override
    public boolean isCleaner(Player player) {
        return cleaners.contains(player.getName());
    }

    @Override
    public void setCleaner(Player player, boolean cleaning) {
        if (cleaning) {
            cleaners.add(player.getName());
        } else {
            cleaners.remove(player.getName());
        }
    }

    @Override
    public Disguiser getDisguiser() {
        return disguiser;
    }

    @Override
    public StatsDatabase getStats() {
        return statsDatabase;
    }
}
