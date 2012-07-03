package com.dumptruckman.minecraft.zombiefight;

import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.minecraft.zombiefight.api.Game;
import com.dumptruckman.minecraft.zombiefight.api.GamePlayer;
import com.dumptruckman.minecraft.zombiefight.api.PlayerType;
import com.dumptruckman.minecraft.zombiefight.api.ZombieFight;
import com.dumptruckman.minecraft.zombiefight.util.Language;
import me.desmin88.mobdisguise.api.MobDisguiseAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

class DefaultGamePlayer implements GamePlayer {

    private ZombieFight plugin;
    private Game game;

    private String name;

    private boolean online = false;

    private PlayerType playerType = PlayerType.HUMAN;

    private int id;

    private String trackedPlayer = "";

    DefaultGamePlayer(String name, ZombieFight plugin, Game game) {
        this.name = name;
        this.plugin = plugin;
        this.game = game;
        Player player = getPlayer();
        if (player != null && player.getWorld().equals(game.getWorld())) {
            online = true;
        }
        id = plugin.getStats().getPlayer(name);
    }

    private void fixUpPlayer(Player player) {
        player.setDisplayName(ChatColor.stripColor(player.getDisplayName()));
        player.setPlayerListName(ChatColor.stripColor(player.getPlayerListName()));
        if (isZombie()) {
            if (!MobDisguiseAPI.isDisguised(player)) {
                MobDisguiseAPI.disguisePlayer(player, "zombie");
            }
            /*if (!plugin.getDisguiser().isDisguised(player)) {
                plugin.getDisguiser().disguise(player, EntityType.ZOMBIE);
            }*/
            String name = plugin.getMessager().getMessage(Language.ZOMBIE_NAME, player.getDisplayName());
            player.setDisplayName(name + ChatColor.WHITE);
            int length = name.length();
            if (length > 16) {
                length = 15;
            }
            player.setPlayerListName(name.substring(0, length));
            game.addZombieItems(player.getInventory());
            //player.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(Integer.MAX_VALUE, plugin.config().get(ZFConfig.ZOMBIE_BREAK_SPEED_STRENGTH)));
        } else {
            if (MobDisguiseAPI.isDisguised(player)) {
                MobDisguiseAPI.undisguisePlayer(player);
            }
            /*if (plugin.getDisguiser().isDisguised(player)) {
                plugin.getDisguiser().undisguise(player);
            }*/
            String name = plugin.getMessager().getMessage(Language.HUMAN_NAME, player.getDisplayName());
            player.setDisplayName(name + ChatColor.WHITE);
            int length = name.length();
            if (length > 16) {
                length = 15;
            }
            player.setPlayerListName(name.substring(0, length));
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public final Player getPlayer() {
        return Bukkit.getPlayerExact(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isZombie() {
        return playerType != PlayerType.HUMAN;
    }

    @Override
    public PlayerType getType() {
        return playerType;
    }

    @Override
    public boolean isOnline() {
        if (online && getPlayer() == null) {
            leftGame();
        }
        return online;
    }

    @Override
    public void joinedGame() {
        Player player = getPlayer();
        if (player == null) {
            Logging.fine("Offline player '" + name + "' somehow joined game!");
            online = false;
            return;
        }
        online = true;
        fixUpPlayer(player);
    }

    @Override
    public void leftGame() {
        online = false;
        Player player = getPlayer();
        if (player != null) {
            player.setDisplayName(ChatColor.stripColor(player.getDisplayName()));
            if (MobDisguiseAPI.isDisguised(player)) {
                MobDisguiseAPI.undisguisePlayer(player);
            }
        }
    }

    @Override
    public void makeZombie(final boolean broadcast) {
        if (isZombie()) {
            return;
        }
        playerType = PlayerType.ZOMBIE;
        final Player player = getPlayer();
        if (player != null) {
            resetPlayer(player);
            fixUpPlayer(player);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if (broadcast) {
                        plugin.broadcastWorld(game.getWorld().getName(), plugin.getMessager().getMessage(Language.PLAYER_ZOMBIFIED, player.getName()));
                    }
                    plugin.getMessager().normal(Language.YOU_ARE_ZOMBIE, player);
                }
            }, 3L);
        }
        plugin.getStats().playerUpdate(this);
        if (game.hasStarted() && !game.hasEnded()) {
            plugin.getStats().playerTypeChange(game, this, PlayerType.ZOMBIE);
        }
    }

    @Override
    public void makeHuman() {
        playerType = PlayerType.HUMAN;
        Player player = getPlayer();
        if (player != null) {
            resetPlayer(player);
            fixUpPlayer(player);
        }
        plugin.getStats().playerUpdate(this);
        if (game.hasStarted() && !game.hasEnded()) {
            plugin.getStats().playerTypeChange(game, this, PlayerType.HUMAN);
        }
    }

    private void resetPlayer(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (int i = 0; i < armor.length; i++) {
            armor[i] = new ItemStack(Material.AIR);
        }
        player.getInventory().setArmorContents(armor);
        player.getInventory().clear();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(5F);
        player.setExhaustion(0F);
        player.closeInventory();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    @Override
    public Player getTrackedPlayer() {
        return Bukkit.getPlayerExact(trackedPlayer);
    }

    @Override
    public void setTrackedPlayer(Player player) {
        trackedPlayer = player.getName();
    }

    @Override
    public boolean isTracking(Player player) {
        return player.getName().equals(trackedPlayer);
    }
}
