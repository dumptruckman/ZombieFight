/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.dumptruckman.minecraft.zombiefight.util;

import com.dumptruckman.minecraft.pluginbase.plugin.AbstractBukkitPlugin;
import com.dumptruckman.minecraft.pluginbase.util.FileUtils;
import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.GameManagerHelper;
import com.dumptruckman.minecraft.zombiefight.ZombieFightListener;
import com.dumptruckman.minecraft.zombiefight.ZombieFightPlugin;
import com.dumptruckman.minecraft.zombiefight.api.GameManager;
import junit.framework.Assert;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.MockGateway;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@PrepareForTest({AbstractBukkitPlugin.class, ZombieFightListener.class})
public class TestInstanceCreator {
    private ZombieFightPlugin plugin;
    private ZombieFightListener listener;
    private Server mockServer;
    private CommandSender commandSender;
    public Map<String, Player> players = new HashMap<String, Player>();

    public static final File invDirectory = new File("bin/test/server/plugins/zombie-test");
    public static final File serverDirectory = new File("bin/test/server");
    public static final File worldsDirectory = new File("bin/test/server");

    public boolean setUp() {
        try {
            FileUtils.deleteFolder(invDirectory);
            FileUtils.deleteFolder(serverDirectory);
            invDirectory.mkdirs();
            Assert.assertTrue(invDirectory.exists());

            MockGateway.MOCK_STANDARD_METHODS = false;

            plugin = PowerMockito.spy(new ZombieFightPlugin());

            // Let's let all MV files go to bin/test
            doReturn(invDirectory).when(plugin).getDataFolder();

            // Return a fake PDF file.
            PluginDescriptionFile pdf = PowerMockito.spy(new PluginDescriptionFile("ZombieFight", "dev-test",
                    "com.dumptruckman.minecraft.zombiefight.ZombieFight"));
            when(pdf.getAuthors()).thenReturn(new ArrayList<String>());
            doReturn(pdf).when(plugin).getDescription();
            doReturn(true).when(plugin).isEnabled();
            plugin.setServerFolder(serverDirectory);

            // Add Core to the list of loaded plugins
            JavaPlugin[] plugins = new JavaPlugin[]{plugin};

            // Mock the Plugin Manager
            PluginManager mockPluginManager = PowerMockito.mock(PluginManager.class);
            when(mockPluginManager.getPlugins()).thenReturn(plugins);
            when(mockPluginManager.getPlugin("ZombieFight")).thenReturn(plugin);
            when(mockPluginManager.getPermission(anyString())).thenReturn(null);

            // Make some fake folders to fool the fake MV into thinking these worlds exist
            File worldNormalFile = new File(plugin.getServerFolder(), "world");
            Util.log("Creating world-folder: " + worldNormalFile.getAbsolutePath());
            worldNormalFile.mkdirs();
            MockWorldFactory.makeNewMockWorld("world", Environment.NORMAL, WorldType.NORMAL);
            File worldNetherFile = new File(plugin.getServerFolder(), "world_nether");
            Util.log("Creating world-folder: " + worldNetherFile.getAbsolutePath());
            worldNetherFile.mkdirs();
            MockWorldFactory.makeNewMockWorld("world_nether", Environment.NETHER, WorldType.NORMAL);
            File worldSkylandsFile = new File(plugin.getServerFolder(), "world_the_end");
            Util.log("Creating world-folder: " + worldSkylandsFile.getAbsolutePath());
            worldSkylandsFile.mkdirs();
            MockWorldFactory.makeNewMockWorld("world_the_end", Environment.THE_END, WorldType.NORMAL);
            File world2File = new File(plugin.getServerFolder(), "world2");
            Util.log("Creating world-folder: " + world2File.getAbsolutePath());
            world2File.mkdirs();
            MockWorldFactory.makeNewMockWorld("world2", Environment.NORMAL, WorldType.NORMAL);

            // Initialize the Mock server.
            mockServer = mock(Server.class);
            when(mockServer.getName()).thenReturn("TestBukkit");
            Logger.getLogger("Minecraft").setParent(Util.logger);
            when(mockServer.getLogger()).thenReturn(Util.logger);
            when(mockServer.getWorldContainer()).thenReturn(worldsDirectory);
            when(plugin.getServer()).thenReturn(mockServer);
            when(mockServer.getPluginManager()).thenReturn(mockPluginManager);

            // Give the server some worlds
            when(mockServer.getWorld(anyString())).thenAnswer(new Answer<World>() {
                public World answer(InvocationOnMock invocation) throws Throwable {
                    String arg;
                    try {
                        arg = (String) invocation.getArguments()[0];
                    } catch (Exception e) {
                        return null;
                    }
                    return MockWorldFactory.getWorld(arg);
                }
            });

            when(mockServer.getWorlds()).thenAnswer(new Answer<List<World>>() {
                public List<World> answer(InvocationOnMock invocation) throws Throwable {
                    return MockWorldFactory.getWorlds();
                }
            });

            when(mockServer.createWorld(Matchers.isA(WorldCreator.class))).thenAnswer(
                    new Answer<World>() {
                        public World answer(InvocationOnMock invocation) throws Throwable {
                            WorldCreator arg;
                            try {
                                arg = (WorldCreator) invocation.getArguments()[0];
                            } catch (Exception e) {
                                return null;
                            }
                            // Add special case for creating null worlds.
                            // Not sure I like doing it this way, but this is a special case
                            if (arg.name().equalsIgnoreCase("nullworld")) {
                                return MockWorldFactory.makeNewNullMockWorld(arg.name(), arg.environment(), arg.type());
                            }
                            return MockWorldFactory.makeNewMockWorld(arg.name(), arg.environment(), arg.type());
                        }
                    });

            when(mockServer.unloadWorld(anyString(), anyBoolean())).thenReturn(true);

            // Setup server.getPlayer();
            Answer<Player> playerAnswer = new Answer<Player>() {
                public Player answer(InvocationOnMock invocation) throws Throwable {
                    String arg;
                    try {
                        arg = (String) invocation.getArguments()[0];
                    } catch (Exception e) {
                        return null;
                    }
                    Player player = players.get(arg);
                    if (player != null) {

                    }
                    return players.get(arg);
                }
            };
            when(mockServer.getPlayer(anyString())).thenAnswer(playerAnswer);
            /*
            when(mockServer.getOfflinePlayer(anyString())).thenAnswer(playerAnswer);
            when(mockServer.getOfflinePlayers()).thenAnswer(new Answer<OfflinePlayer[]>() {
                public OfflinePlayer[] answer(InvocationOnMock invocation) throws Throwable {
                    return players.values().toArray(new Player[players.values().size()]);
                }
            });
            */

            // add mock scheduler
            BukkitScheduler mockScheduler = mock(BukkitScheduler.class);
            when(mockScheduler.scheduleSyncDelayedTask(any(Plugin.class), any(Runnable.class), anyLong())).
                    thenAnswer(new Answer<Integer>() {
                        public Integer answer(InvocationOnMock invocation) throws Throwable {
                            Runnable arg;
                            try {
                                arg = (Runnable) invocation.getArguments()[1];
                            } catch (Exception e) {
                                return null;
                            }
                            arg.run();
                            return null;
                        }
                    });
            when(mockScheduler.scheduleSyncDelayedTask(any(Plugin.class), any(Runnable.class))).
                    thenAnswer(new Answer<Integer>() {
                        public Integer answer(InvocationOnMock invocation) throws Throwable {
                            Runnable arg;
                            try {
                                arg = (Runnable) invocation.getArguments()[1];
                            } catch (Exception e) {
                                return null;
                            }
                            arg.run();
                            return null;
                        }
                    });
            when(mockServer.getScheduler()).thenReturn(mockScheduler);

            // Set InventoriesListener
            listener = PowerMockito.spy(new ZombieFightListener(plugin));
            Field inventoriesListenerField = ZombieFightPlugin.class.getDeclaredField("listener");
            inventoriesListenerField.setAccessible(true);
            inventoriesListenerField.set(plugin, listener);

            /*
            // Set GameManager
            GameManager gm = PowerMockito.spy(GameManagerHelper.newGameManager(plugin));
            Field gameManagerField = ZombieFightPlugin.class.getDeclaredField("gameManager");
            gameManagerField.setAccessible(true);
            gameManagerField.set(plugin, gm);
            when(plugin.getGameManager()).thenReturn(gm);
            */

            // Set server
            Field serverfield = JavaPlugin.class.getDeclaredField("server");
            serverfield.setAccessible(true);
            serverfield.set(plugin, mockServer);

            // Init our command sender
            final Logger commandSenderLogger = Logger.getLogger("CommandSender");
            commandSenderLogger.setParent(Util.logger);
            commandSender = mock(CommandSender.class);
            doAnswer(new Answer<Void>() {
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    commandSenderLogger.info(ChatColor.stripColor((String) invocation.getArguments()[0]));
                    return null;
                }
            }).when(commandSender).sendMessage(anyString());
            when(commandSender.getServer()).thenReturn(mockServer);
            when(commandSender.getName()).thenReturn("MockCommandSender");
            when(commandSender.isPermissionSet(anyString())).thenReturn(true);
            when(commandSender.isPermissionSet(Matchers.isA(Permission.class))).thenReturn(true);
            when(commandSender.hasPermission(anyString())).thenReturn(true);
            when(commandSender.hasPermission(Matchers.isA(Permission.class))).thenReturn(true);
            when(commandSender.addAttachment(plugin)).thenReturn(null);
            when(commandSender.isOp()).thenReturn(true);

            Bukkit.setServer(mockServer);

            // Load Multiverse Core
            plugin.onLoad();

            // Enable it.
            plugin.onEnable();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean tearDown() {
        /*
        List<MultiverseWorld> worlds = new ArrayList<MultiverseWorld>(core.getMVWorldManager()
                .getMVWorlds());
        for (MultiverseWorld world : worlds) {
            core.getMVWorldManager().deleteWorld(world.getName());
        }
        */

        try {
            Field serverField = Bukkit.class.getDeclaredField("server");
            serverField.setAccessible(true);
            serverField.set(Class.forName("org.bukkit.Bukkit"), null);
        } catch (Exception e) {
            Util.log(Level.SEVERE,
                    "Error while trying to unregister the server from Bukkit. Has Bukkit changed?");
            e.printStackTrace();
            Assert.fail(e.getMessage());
            return false;
        }

        MockWorldFactory.clearWorlds();

        Logging.close();

        return true;
    }

    public ZombieFightPlugin getPlugin() {
        return this.plugin;
    }

    public Server getServer() {
        return this.mockServer;
    }

    public CommandSender getCommandSender() {
        return commandSender;
    }

    public void playerJoin(String name) {
        Player player = players.get(name);
        if (player == null) {
            //player = new MockPlayer(name, mockServer, mockServer.getWorld("world"));
            player = PowerMockito.mock(Player.class);
            players.put(name, player);
        }
        World world = mockServer.getWorld("world");
        when(player.getWorld()).thenReturn(world);
        final Logger playerLogger = Logger.getLogger("Player: " + name);
        playerLogger.setParent(Util.logger);
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) throws Throwable {
                playerLogger.info(ChatColor.stripColor((String) invocation.getArguments()[0]));
                return null;
            }
        }).when(player).sendMessage(anyString());
        listener.playerJoin(new PlayerJoinEvent(player, "JOIN"));
    }

    public void playerQuit(String name) {
        Player player = players.get(name);
        if (player != null) {
            listener.playerQuit(new PlayerQuitEvent(player, "QUIT"));
            players.remove(name);
        }
    }
}
