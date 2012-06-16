package com.dumptruckman.minecraft.zombiefight.util;

import org.bukkit.Achievement;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MockPlayer implements Player {

    String name;
    Server server;
    World world;
    
    public MockPlayer(String name, Server server, World world) {
        this.name = name;
        this.server = server;
        this.world = world;
    }
    

    @Override
    public void setCompassTarget(Location location) {

    }

    @Override
    public Location getCompassTarget() {
        return null;
    }

    @Override
    public void giveExp(int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float getExp() {
        return 0;
    }

    @Override
    public void setExp(float v) {

    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public void setLevel(int i) {

    }

    @Override
    public int getTotalExperience() {
        return 0;
    }

    @Override
    public void setTotalExperience(int i) {

    }

    @Override
    public float getExhaustion() {
        return 0;
    }

    @Override
    public void setExhaustion(float v) {

    }

    @Override
    public float getSaturation() {
        return 0;
    }

    @Override
    public void setSaturation(float v) {

    }

    @Override
    public int getFoodLevel() {
        return 0;
    }

    @Override
    public void setFoodLevel(int i) {

    }

    @Override
    public Location getBedSpawnLocation() {
        return null;
    }

    @Override
    public void setBedSpawnLocation(Location location) {

    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public PlayerInventory getInventory() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return this.name;
    }

    @Override
    public String getPlayerListName() {
        return this.name;
    }


    @Override
    public int getHealth() {
        return 0;
    }

    @Override
    public void setHealth(int i) {
    }

    @Override
    public int getMaximumNoDamageTicks() {
        return 0;
    }

    @Override
    public void setMaximumNoDamageTicks(int i) {

    }

    @Override
    public int getLastDamage() {
        return 0;
    }

    @Override
    public void setLastDamage(int i) {

    }

    @Override
    public int getMaxHealth() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getRemainingAir() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setRemainingAir(int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getMaximumAir() {
        return 0;
    }

    @Override
    public void setMaximumAir(int i) {
    }

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect, boolean b) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> potionEffects) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType potionEffectType) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removePotionEffect(PotionEffectType potionEffectType) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        return null;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public int getFireTicks() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getMaxFireTicks() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setFireTicks(int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float getFallDistance() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setFallDistance(float v) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getTicksLived() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setTicksLived(int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Player getPlayer() {
        return this;
    }

    @Override
    public boolean teleport(Location location) {
        return teleport(location, TeleportCause.UNKNOWN);
    }

    @Override
    public boolean teleport(Location location, TeleportCause teleportCause) {
        return true;
    }

    @Override
    public Server getServer() {
        return this.server;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    @Override
    public void setDisplayName(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setPlayerListName(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InetSocketAddress getAddress() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendRawMessage(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void kickPlayer(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void chat(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean performCommand(String s) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSneaking() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setSneaking(boolean b) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSprinting() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setSprinting(boolean b) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void saveData() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void loadData() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setSleepingIgnored(boolean b) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSleepingIgnored() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void playNote(Location location, byte b, byte b1) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void playNote(Location location, Instrument instrument, Note note) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void playEffect(Location location, Effect effect, int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> void playEffect(Location location, Effect effect, T t) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendBlockChange(Location location, Material material, byte b) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean sendChunkChange(Location location, int i, int i1, int i2, byte[] bytes) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendBlockChange(Location location, int i, byte b) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendMap(MapView mapView) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateInventory() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void awardAchievement(Achievement achievement) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void incrementStatistic(Statistic statistic) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void incrementStatistic(Statistic statistic, int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material, int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setPlayerTime(long l, boolean b) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getPlayerTime() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getPlayerTimeOffset() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void resetPlayerTime() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean getAllowFlight() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAllowFlight(boolean b) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void hidePlayer(Player player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void showPlayer(Player player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canSee(Player player) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendMessage(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendMessage(String[] strings) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Object> serialize() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isConversing() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void acceptConversationInput(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void abandonConversation(Conversation conversation) {
        //To change body of implemented methods use File | Settings | File Templates.
    }



    @Override
    public boolean setWindowProperty(Property property, int i) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InventoryView getOpenInventory() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InventoryView openInventory(Inventory itemStacks) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InventoryView openWorkbench(Location location, boolean b) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InventoryView openEnchanting(Location location, boolean b) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void openInventory(InventoryView inventoryView) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void closeInventory() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ItemStack getItemInHand() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setItemInHand(ItemStack itemStack) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ItemStack getItemOnCursor() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setItemOnCursor(ItemStack itemStack) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSleeping() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getSleepTicks() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public GameMode getGameMode() {
        return GameMode.SURVIVAL;
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    
    
    
    

    @Override
    public double getEyeHeight() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double getEyeHeight(boolean b) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Location getEyeLocation() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Block> getLineOfSight(HashSet<Byte> bytes, int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Block getTargetBlock(HashSet<Byte> bytes, int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> bytes, int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Egg throwEgg() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Snowball throwSnowball() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Arrow shootArrow() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public boolean isFlying() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setFlying(boolean b) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent conversationAbandonedEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isBlocking() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void damage(int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void damage(int i, Entity entity) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getNoDamageTicks() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setNoDamageTicks(int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Player getKiller() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    @Override
    public void setVelocity(Vector vector) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Vector getVelocity() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    

    @Override
    public boolean teleport(Entity entity) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean teleport(Entity entity, TeleportCause teleportCause) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Entity> getNearbyEntities(double v, double v1, double v2) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getEntityId() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }


    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    @Override
    public void remove() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isDead() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Entity getPassenger() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean setPassenger(Entity entity) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isEmpty() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean eject() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }


    
    
    
    
    
    
    
    
    

    @Override
    public void setLastDamageCause(EntityDamageEvent entityDamageEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public UUID getUniqueId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    
    
    
    
    
    

    @Override
    public void playEffect(EntityEffect entityEffect) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public EntityType getType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isInsideVehicle() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean leaveVehicle() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Entity getVehicle() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setMetadata(String s, MetadataValue metadataValue) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<MetadataValue> getMetadata(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasMetadata(String s) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeMetadata(String s, Plugin plugin) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isOnline() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isBanned() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setBanned(boolean b) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isWhitelisted() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setWhitelisted(boolean b) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    

    
    
    
    
    @Override
    public long getFirstPlayed() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getLastPlayed() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasPlayedBefore() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isPermissionSet(String s) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasPermission(String s) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void recalculatePermissions() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendPluginMessage(Plugin plugin, String s, byte[] bytes) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isOp() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setOp(boolean b) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
