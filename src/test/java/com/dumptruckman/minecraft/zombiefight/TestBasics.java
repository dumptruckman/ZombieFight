/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.zombiefight.api.ZFConfig;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.TestInstanceCreator;
import junit.framework.Assert;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ZombieFightPlugin.class, PluginDescriptionFile.class})
public class TestBasics {
    TestInstanceCreator creator;
    Server mockServer;
    CommandSender mockCommandSender;

    @Before
    public void setUp() throws Exception {
        creator = new TestInstanceCreator();
        assertTrue(creator.setUp());
        mockServer = creator.getServer();
        mockCommandSender = creator.getCommandSender();
    }

    @After
    public void tearDown() throws Exception {
        creator.tearDown();
    }

    @Test
    public void testBasics() {
        // Pull a core instance from the server.
        Plugin plugin = mockServer.getPluginManager().getPlugin("ZombieFight");
        ZombieFight inventories = (ZombieFight) plugin;
        // Make sure Core is not null
        assertNotNull(plugin);
        // Make sure Core is enabled
        assertTrue(plugin.isEnabled());
        // Make a fake server folder to fool MV into thinking a world folder exists.
        File serverDirectory = new File(creator.getPlugin().getServerFolder(), "world");
        serverDirectory.mkdirs();
        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("zf");
        // Assert debug mode is off
        Assert.assertEquals(Integer.valueOf(0), inventories.config().get(ZFConfig.DEBUG_MODE));
        // Send the debug command.
        String[] cmdArgs = new String[]{"debug", "3"};
        plugin.onCommand(mockCommandSender, mockCommand, "", cmdArgs);
        // Assert debug mode is on
        Assert.assertEquals(Integer.valueOf(3), inventories.config().get(ZFConfig.DEBUG_MODE));
        // Send the reload command.
        cmdArgs = new String[] { "reload" };
        plugin.onCommand(mockCommandSender, mockCommand, "", cmdArgs);
        // Assert debug mode is on
        Assert.assertEquals(Integer.valueOf(3), inventories.config().get(ZFConfig.DEBUG_MODE));

        // Setup a game world
        cmdArgs = new String[] { "enable", "-w", "world" };
        plugin.onCommand(mockCommandSender, mockCommand, "", cmdArgs);

        // Test player join.
        creator.playerJoin("dumptruckman");
        creator.playerJoin("dumptruckman2");
        creator.playerJoin("dumptruckman3");
        creator.playerJoin("dumptruckman4");
        creator.playerJoin("dumptruckman5");
        creator.playerJoin("dumptruckman6");
        creator.playerJoin("dumptruckman7");
        creator.playerJoin("dumptruckman8");
        creator.playerJoin("dumptruckman9");
        creator.playerJoin("dumptruckman10");
        creator.playerJoin("dumptruckman11");
        creator.playerJoin("dumptruckman12");
        creator.playerJoin("dumptruckman13");
        creator.playerJoin("dumptruckman14");
        creator.playerJoin("dumptruckman15");
        creator.playerJoin("dumptruckman16");
    }
}
