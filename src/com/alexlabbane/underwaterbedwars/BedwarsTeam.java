package com.alexlabbane.underwaterbedwars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftTrident;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.alexlabbane.underwaterbedwars.gui.ItemShop;
import com.alexlabbane.underwaterbedwars.gui.TeamShop;
import com.alexlabbane.underwaterbedwars.gui.TrapShop;
import com.alexlabbane.underwaterbedwars.util.ChatMessages;
import com.alexlabbane.underwaterbedwars.util.LeveledEnchantment;
import com.alexlabbane.underwaterbedwars.util.TeamColor;
import com.alexlabbane.underwaterbedwars.util.TrapQueue;
import com.alexlabbane.underwaterbedwars.util.Util;
import com.alexlabbane.underwaterbedwars.world.ChunkManager;

import com.mojang.datafixers.util.Pair;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R2.EntityThrownTrident;

/**
 * Represents a team in a BedwarsGame. Most listeners to player actions are also implemented here
 * rather than in the BedwarsPlayer class.
 * @author Alex Labbane
 *
 */
public class BedwarsTeam implements Listener {
	private Plugin plugin;
	private BedwarsGame game;
	private TeamColor teamColor;
	private ArrayList<BedwarsPlayer> bwPlayers;
	private ArrayList<Pair<Material, LeveledEnchantment[]>> starterMaterials;
	private ArrayList<Pair<Material, LeveledEnchantment[]>> starterArmor;
	
	// Config path
	private String configPath;
	
	// Team base bounds
	private double baseXMin, baseZMin;
	private double baseXMax, baseZMax;
	
	// Team upgrades
	private int impalingLevel;
	private int protLevel;
	private int hasteLevel;
	private int forgeLevel;
	private int healPoolLevel;
	
	private TrapQueue traps;
	private BukkitTask healPool;
	
	// Shop stuff
	private ItemShop itemShop;
	private Location itemShopLocation;
	private Entity itemShopVillager;
	
	private TeamShop teamShop;
	private TrapShop trapShop;
	private Location teamShopLocation;
	private Entity teamShopVillager;
	
	// Gen stuff
	private BukkitTask genSpawner;
	private Location genLocation;
	private boolean genPaused;

	// Team bed
	private BedwarsBed bed;
	
	// Other important locations
	private Location chestLocation;
	private Location enderChestLocation;
	private Location spawnLocation;
	
	/**
	 * Create a new BedwarsTeam
	 * @param players		a prepopulated list of players to add to the team
	 * @param configPath	the path to team parameters in the config file
	 * @param p				reference to the plugin
	 * @param game			reference to the BedwarsGame the team is a part of
	 */
	public BedwarsTeam(ArrayList<BedwarsPlayer> players, String configPath, Plugin p, BedwarsGame game) {
		this.bwPlayers = players;
		this.initializeTeam(configPath, p, game);
	}
	
	/**
	 * Create a new BedwarsTeam
	 * @param configPath	the path to team parameters in the config file
	 * @param p				reference to the plugin
	 * @param game			reference to the BedwarsGame the team is a part of
	 */
	public BedwarsTeam(String configPath, Plugin p, BedwarsGame game) {
		this.bwPlayers = new ArrayList<BedwarsPlayer>();
		this.initializeTeam(configPath, p, game);
	}
	
	/************* Getters/setters *************/
	
	public TeamColor getColor() { return this.teamColor; }
	public Location getSpawnLocation() { return this.spawnLocation; }
	public double getBaseXMin() { return this.baseXMin; }
	public double getBaseXMax() { return this.baseXMax; }
	public double getBaseZMin() { return this.baseZMin; }
	public double getBaseZMax() { return this.baseZMax; }
	public TrapQueue getQueuedTraps() { return this.traps; }
	public TeamShop getTeamShop() { return this.teamShop; }
	public TrapShop getTrapShop() { return this.trapShop; }
	public BedwarsBed getBed() { return this.bed; }
	public String getConfigPath() { return this.configPath; }
	public ArrayList<Pair<Material, LeveledEnchantment[]>> getStarterMaterials() { return this.starterMaterials; }
	public ArrayList<Pair<Material, LeveledEnchantment[]>> getStarterArmor() { return this.starterArmor; }
	public void setItemShopLocation(Location loc) { this.setShopLocation("ITEM", loc, this.itemShopVillager); }
	public void setTeamShopLocation(Location loc) {	this.setShopLocation("TEAM", loc, this.teamShopVillager); }
	public void setGenLocation(Location loc) { this.genLocation = loc; }
	
	public ArrayList<BedwarsPlayer> getBedwarsPlayers() { return this.bwPlayers; }
	public ArrayList<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		for(BedwarsPlayer bwPlayer : this.bwPlayers)
			players.add(bwPlayer.getPlayer());
		
		return players;
	}
	
	public int getImpalingLevel() { return this.impalingLevel; }
	public void setImpalingLevel(int level) { 
		this.impalingLevel = level; 
		
		// Apply the correct effect + level to all players on the team
		for(BedwarsPlayer bwPlayer : this.bwPlayers)
			bwPlayer.applyImpalingEffect();
	}
	
	public int getHasteLevel() { return this.hasteLevel; }
	public void setHasteLevel(int level) { 
		this.hasteLevel = level; 
		
		// Update player haste
		for(BedwarsPlayer bwPlayer : this.bwPlayers)
			bwPlayer.setPlayerHaste();
	}
	
	public int getForgeLevel() { return this.forgeLevel; }
	public void setForgeLevel(int level) { this.forgeLevel = level; }

	public int getHealPoolLevel() { return this.healPoolLevel; }
	public void setHealPoolLevel(int level) { this.healPoolLevel = level; }
	
	public int getProtLevel() { return this.protLevel; }
	public void setProtectionLevel(int level) { 
		this.protLevel = level;
		
		// Update player armor
		for(BedwarsPlayer bwPlayer : this.bwPlayers)
			bwPlayer.setPlayerArmor();
	}
	
	
	/**
	 * Initialize all of the data members of the team
	 * @param configPath	the path in the configuration file to team parameters
	 * @param p				reference to the plugin
	 * @param game			reference to the BedwarsGame the team is a part of
	 */
	private void initializeTeam(String configPath, Plugin p, BedwarsGame game) {		
		this.plugin = p;
		this.configPath = configPath;
		
		FileConfiguration config = this.plugin.getConfig();
		String color = config.getString(this.configPath + ".color");

		this.teamColor = TeamColor.valueOf(color);
		this.game = game;
		
		// Default all team upgrades
		this.impalingLevel = 0;
		this.protLevel = 0;
		this.hasteLevel = 0;
		this.forgeLevel = 0;
		this.healPoolLevel = 0;
		this.traps = new TrapQueue();
		this.healPool = null;
		
		// Get base bounds
		this.baseXMin = Util.plugin.getConfig().getDouble(this.configPath + ".base-bounds.x-min");
		this.baseXMax = Util.plugin.getConfig().getDouble(this.configPath + ".base-bounds.x-max");
		this.baseZMin = Util.plugin.getConfig().getDouble(this.configPath + ".base-bounds.z-min");
		this.baseZMax = Util.plugin.getConfig().getDouble(this.configPath + ".base-bounds.z-max");
		
		// Set spawn location
		this.spawnLocation = new Location(
				Bukkit.getServer().getWorlds().get(0),
				config.getDouble(this.configPath + ".spawn-location.x"),
				config.getDouble(this.configPath + ".spawn-location.y"),
				config.getDouble(this.configPath + ".spawn-location.z"));
		this.spawnLocation.setPitch((float) config.getDouble(this.configPath + ".spawn-location.pitch"));
		this.spawnLocation.setYaw((float) config.getDouble(this.configPath + ".spawn-location.yaw"));

		// Initialize bed
		this.bed = new BedwarsBed(this.teamColor, this);
		
		// Initialize item shop
		this.itemShop = new ItemShop(color, game);
		
		Location itemShopLocation = new Location(
				Bukkit.getServer().getWorlds().get(0),
				this.plugin.getConfig().getDouble(this.configPath + ".item-shop-location.x"),
				this.plugin.getConfig().getDouble(this.configPath + ".item-shop-location.y"),
				this.plugin.getConfig().getDouble(this.configPath + ".item-shop-location.z"));
		itemShopLocation.setPitch((float)this.plugin.getConfig().getDouble(this.configPath + ".item-shop-location.pitch"));
		itemShopLocation.setYaw((float)this.plugin.getConfig().getDouble(this.configPath + ".item-shop-location.yaw"));
		this.itemShopVillager = itemShopLocation.getWorld().spawnEntity(itemShopLocation, EntityType.VILLAGER);
		this.itemShopVillager.setCustomName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "ITEM SHOP");
		this.itemShopVillager.setCustomNameVisible(true);
		this.setItemShopLocation(itemShopLocation);
		
		this.itemShopVillager.setSilent(true);
		
		// Stop the villager from moving around
		Util.freezeEntity(this.itemShopVillager);
		
		// Initialize team shop
		this.teamShop = new TeamShop(color, game);
		this.trapShop = new TrapShop(color, game);
		
		Location teamShopLocation = new Location(
				Bukkit.getServer().getWorlds().get(0),
				this.plugin.getConfig().getDouble(this.configPath + ".team-shop-location.x"),
				this.plugin.getConfig().getDouble(this.configPath + ".team-shop-location.y"),
				this.plugin.getConfig().getDouble(this.configPath + ".team-shop-location.z"));
		teamShopLocation.setPitch((float)this.plugin.getConfig().getDouble(this.configPath + ".team-shop-location.pitch"));
		teamShopLocation.setYaw((float)this.plugin.getConfig().getDouble(this.configPath + ".team-shop-location.yaw"));
		this.teamShopVillager = teamShopLocation.getWorld().spawnEntity(teamShopLocation, EntityType.VILLAGER);
		this.teamShopVillager.setCustomName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "TEAM UPGRADES");
		this.teamShopVillager.setCustomNameVisible(true);
		this.setTeamShopLocation(teamShopLocation);
		
		this.teamShopVillager.setSilent(true);
		
		// Stop the villager from moving around
		Util.freezeEntity(this.teamShopVillager);
		
		// Initialize base gen
		this.genPaused = false;
		this.setGenLocation(new Location(
				this.itemShopLocation.getWorld(),
				this.plugin.getConfig().getDouble(this.configPath + ".gen-location.x"),
				this.plugin.getConfig().getDouble(this.configPath + ".gen-location.y"),
				this.plugin.getConfig().getDouble(this.configPath + ".gen-location.z")));
		
		// Give players all starter armor/tools
		this.starterMaterials = new ArrayList<Pair<Material, LeveledEnchantment[]>>();
		this.starterArmor = new ArrayList<Pair<Material, LeveledEnchantment[]>>();
		this.initializeStarterMaterials();
		this.initializeStarterArmor();
		this.initializeGen();
	}
	
	/**
	 * Move the shop of given shopType to a new location on the map
	 * @param shopType		the type of shop that should be moved
	 * @param newLoc		the new location of the shop
	 * @param shopEntity	the entity the shop is attached to
	 */
	private void setShopLocation(String shopType, Location newLoc, Entity shopEntity) {
		if(shopType.equals("ITEM"))
			this.itemShopLocation = newLoc;
		else if(shopType.equals("TEAM"))
			this.teamShopLocation = newLoc;
		
		// Delay teleportation by 1 second
		// Fixed undesirable teleportation lag
		new BukkitRunnable() {
			@Override
			public void run() {
				shopEntity.teleport(newLoc);
			}
		}.runTaskLater(this.plugin, Util.TICKS_PER_SECOND);
	}
	
	/**
	 * Add default starter materials for all team members
	 */
	public void initializeStarterMaterials() {
		this.starterMaterials.add(new Pair<Material, LeveledEnchantment[]>(Material.TRIDENT, new LeveledEnchantment[]{ new LeveledEnchantment(Enchantment.LOYALTY, 1) })); // trident always at index 0
	}
	
	/**
	 * Add default armor for all team members
	 */
	public void initializeStarterArmor() {
		this.starterArmor.add(new Pair<Material, LeveledEnchantment[]>(Material.LEATHER_BOOTS, null)); // boots always index 0
		this.starterArmor.add(new Pair<Material, LeveledEnchantment[]>(Material.LEATHER_LEGGINGS, null)); // leggings always index 1
		this.starterArmor.add(new Pair<Material, LeveledEnchantment[]>(Material.LEATHER_CHESTPLATE, null)); // chestplate always index 2
		this.starterArmor.add(new Pair<Material, LeveledEnchantment[]>(Material.LEATHER_HELMET, new LeveledEnchantment[] { new LeveledEnchantment(Enchantment.WATER_WORKER, 1)} )); // helmet always index 3
	}
	
	/**
	 * Initialize base gen for the team. Cancels any existing gen first.
	 * Separated from GameGen class (only used for game wide gens such as diamonds/emeralds)
	 */
	public void initializeGen() {
		this.stopGen();
		
		ChunkManager.registerChunk(this.genLocation.getChunk());
	
		this.genSpawner = new BukkitRunnable() {
			int counter = 0;
			
			@Override
			public void run() {
				if(genPaused) {
					return;
				}
				
				ItemStack iron = new ItemStack(Material.IRON_INGOT);
				
				if(Util.countDroppedItems(genLocation, iron.getType(), 1.5f) < 48) {
					Item droppedIron = genLocation.getWorld().dropItem(genLocation, iron);
					droppedIron.setVelocity(new Vector(0, 0, 0));
				}
				
				if(counter % 4 == 0) {
					// Spawn gold 1/4 as often as iron
					ItemStack gold = new ItemStack(Material.GOLD_INGOT);
					
					if(Util.countDroppedItems(genLocation, gold.getType(), 1.5f) < 16) {
						Item droppedGold = genLocation.getWorld().dropItem(genLocation, gold);
						droppedGold.setVelocity(new Vector(0, 0, 0));	
					}
				}
				
				if(counter % 64 == 0 && forgeLevel >= 3) {
					// Spawn emerald 1/64 as often as iron
					ItemStack emerald = new ItemStack(Material.EMERALD);
					if(Util.countDroppedItems(genLocation, emerald.getType(), 1.5f) < 4) {
						Item droppedEmerald = genLocation.getWorld().dropItem(genLocation, emerald);
						droppedEmerald.setVelocity(new Vector(0, 0, 0));
					}
				}
				
				counter++;
			}
			
		}.runTaskTimer(this.plugin, Util.TICKS_PER_SECOND, getGenDelay());
	}
	
	/**
	 * Stop the team generator from making resources. Destroys the task in charge of the generator.
	 */
	public void stopGen() {
		ChunkManager.unregisterChunk(this.genLocation.getChunk());
		
		if(this.genSpawner != null)
			this.genSpawner.cancel();	
	}
	
	/**
	 * Pauses the team generator from making new resources. Does not destroy the task in charge of
	 * the generator.
	 */
	public void pauseGen() { this.genPaused = true;	}
	/**
	 * Resume a paused team generator.
	 */
	public void resumeGen() { this.genPaused = false; }
	
	/**
	 * Determines how long the generator should take to spawn new resources
	 * @return	the number of ticks between each counter increment in the generator
	 */
	private int getGenDelay() {
		switch(this.forgeLevel) {
		case 0:
			return 20;
		case 1:
			return 15;
		case 2:
			return 10;
		case 3:
			return 10;
		case 4:
			return 5;
		}
		
		return 20;
	}
	
	/**
	 * Start a heal pool around the team base. Heals all players inside.
	 */
	public void startHealPool() {
		if(this.healPoolLevel == 0)
			return;
		
		// Poll once/sec for players on team that get heal pool
		this.healPool = new BukkitRunnable() {
			@Override
			public void run() {
				for(BedwarsPlayer bwPlayer : bwPlayers) {
					if(bwPlayer.insideBase()) {
						bwPlayer.getPlayer().addPotionEffect(
								new PotionEffect(
										PotionEffectType.REGENERATION, Integer.MAX_VALUE, healPoolLevel - 1));
					} else {
						bwPlayer.getPlayer().removePotionEffect(PotionEffectType.REGENERATION);
					}
				}
			}
		}.runTaskTimer(this.plugin, Util.TICKS_PER_SECOND, Util.TICKS_PER_SECOND);
	}
	
	/**
	 * Cancel a heal pool around the team base, if it exists
	 */
	public void stopHealPool() {
		if(this.healPool == null) {
			return;
		}
		
		this.healPool.cancel();
	}
	
	/**
	 * Add a new player to the team
	 * @param player	the player to be added
	 */
	public void addPlayer(Player player) {
		BedwarsPlayer newBedwarsPlayer = new BedwarsPlayer(player);
		
		if(!this.getPlayers().contains(player))
			bwPlayers.add(newBedwarsPlayer);
		
		this.game.getBedwarsPlayer(player).setTeam(this);
		player.getInventory().clear();
		this.game.getBedwarsPlayer(player).setPlayerArmor();
		this.game.getBedwarsPlayer(player).setPlayerStarterMaterials();
		this.setHasteLevel(this.hasteLevel); // Make sure new player gets haste upgrade applied
	}
	
	/**
	 * Check if the team has a given player on it
	 * @param player	the player to check
	 * @return			true if the player is on the team
	 */
	public boolean hasPlayer(Player player) {
		return this.getPlayers().contains(player);
	}
	
	public void removePlayer(Player player) {
		for(int i = 0; i < this.bwPlayers.size(); i++) {
			BedwarsPlayer bwPlayer = this.bwPlayers.get(i);
			if(bwPlayer.getPlayer() == player) {
				this.bwPlayers.remove(i);
				bwPlayer.setTeam(null);
				i--;
			}
		}
	}	
	
	/**
	 * Handle death of a player on this team
	 * @param e	the player death event being handled
	 */
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		e.getDrops().clear();
		
		// Color player names with their team color in the death message
		e.setDeathMessage(ChatMessages.getColoredChatMessage(e.getDeathMessage()));
		
		BedwarsPlayer bwPlayer = this.game.getBedwarsPlayer(p);
		
		if(bwPlayer != null && this.hasPlayer(p)) {
			// Downgrade tools
			bwPlayer.downgradeAxe();
			bwPlayer.downgradePickaxe();
			
			// Transfer resources to killer
			LivingEntity killer = p.getKiller();
			Player killingPlayer = null;
			
			if(killer instanceof Player) {
				killingPlayer = (Player)killer;
			}
			
			if(killingPlayer != null) {
				BedwarsPlayer killingBedwarsPlayer = this.game.getBedwarsPlayer(killingPlayer);
				boolean resourcesTransferred = false;
				
				if(killingBedwarsPlayer != null) {
					HashMap<Material, Integer> transferred = bwPlayer.transferCurrency(killingBedwarsPlayer);
					
					for(Material transferredMaterial : transferred.keySet()) {
						if(transferred.get(transferredMaterial) > 0) {
							resourcesTransferred = true;
							killingPlayer.sendMessage(
									ChatColor.YELLOW + "+" + transferred.get(transferredMaterial) + " " + transferredMaterial.name());	
						}
					}
				}
				
				// Play sound
				if(resourcesTransferred) {
					p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
				}
			}
			
			
			// Respawn player immediately in spectator (1 tick delay)
			new BukkitRunnable() {
				@Override
				public void run() {
					p.spigot().respawn();
					p.setGameMode(GameMode.SPECTATOR);
				}
			}.runTaskLater(this.plugin, 1);			
			
			// Return here if player is final kill
			if(this.bed.isBroken()) {
				e.setDeathMessage(e.getDeathMessage() + ChatColor.AQUA + ChatColor.BOLD.toString() + " FINAL KILL!");
				bwPlayer.setStillAlive(false);
				this.game.updateScoreboards();
				return;
			}
			
			// Update player title for respawn countdown
			for(int i = 0; i < 5; i++) {
				final int secondsLeft = 5 - i;
				new BukkitRunnable() {
					@Override
					public void run() {
						p.sendTitle(ChatColor.RED + "Respawn in " + secondsLeft + " seconds...", ChatColor.RED + "Please wait.", 0, Util.TICKS_PER_SECOND, 0);
					}
				}.runTaskLater(this.plugin, Util.TICKS_PER_SECOND * i);	
			}

			// Put the player back at base and in survival
			new BukkitRunnable() {
				@Override
				public void run() {
					p.teleport(spawnLocation);
					p.setGameMode(GameMode.SURVIVAL);
					bwPlayer.setPlayerArmor();
					bwPlayer.setPlayerStarterMaterials();
					bwPlayer.setPlayerHaste();
				}
			}.runTaskLater(this.plugin, Util.TICKS_PER_SECOND * 5);			
		}
		
		// Check if all other teams are dead
		boolean otherTeamsDead = true;
		boolean teamStillAlive = false;
		for(BedwarsTeam team : this.game.getTeams()) {			
			for(BedwarsPlayer player : team.getBedwarsPlayers()) {
				if(player.isStillAlive()) {
					if(team == this) {
						teamStillAlive = true;
					} else {
						otherTeamsDead = false;
					}
				}
			}
		}
		
		// Show victory message if all other teams dead
		if(otherTeamsDead && teamStillAlive) {
			Bukkit.broadcastMessage(
					ChatColor.valueOf(this.teamColor.getColor()) + this.teamColor.getColor()
					+ ChatColor.GREEN + " team won the game!");
			
			for(Player player : this.getPlayers()) {
				player.sendTitle(ChatColor.YELLOW + ChatColor.BOLD.toString() + "VICTORY!", "", 0, 80, 0);

				new BukkitRunnable() {
					int count = 0;
					
					@Override
					public void run() {
						Util.spawnFirework(player);
						count++;
						
						if(count >= 5) {
							this.cancel();
						}
					}
				}.runTaskTimer(this.plugin, 10, 10);
			}
		}
		
	}
	
	/**
	 * Make sure a player always has at least a base level trident in their inventory.
	 * Do not allow players to drop a base level trident. Some bugs can probably
	 * be caused by throwing the trident and then buying a second one.
	 * @param e	the event being handled
	 */
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		ItemStack droppedItem = e.getItemDrop().getItemStack();
		Inventory playerInventory = player.getInventory();
		
		// Only handle events for players on this team
		if(!this.hasPlayer(player)) {
			return;
		}
		
		BedwarsPlayer bwPlayer = this.game.getBedwarsPlayer(player);
		
		// Handle dropping base level trident
		if(droppedItem.getType() == Material.TRIDENT 
				&& droppedItem.containsEnchantment(Enchantment.LOYALTY) 
				&& droppedItem.getEnchantmentLevel(Enchantment.LOYALTY) == 1) 
		{
				e.setCancelled(true);
		}
		
		// Handle dropping non-base level trident
		if(droppedItem.getType() == Material.TRIDENT
				&& droppedItem.getEnchantmentLevel(Enchantment.LOYALTY) != 1)
		{	
			// Give player base trident if they threw their last one
			if(!bwPlayer.hasBaseTrident()
					&& !bwPlayer.hasUpgradedTrident()) 
			{
				ItemStack baseTrident = new ItemStack(Material.TRIDENT);
				baseTrident.addEnchantment(Enchantment.LOYALTY, 1);
				playerInventory.addItem(baseTrident);
			}
		}
			
	}
	
	/**
	 * Remove base level trident from a player if they get
	 * another trident
	 * @param e	the event being handled
	 */
	@EventHandler
	public void onPlayerGetItem(EntityPickupItemEvent e) {
		Entity entity = e.getEntity();
		Player player = null;
		
		if(entity instanceof Player) {
			player = (Player)entity;
		} else {
			return;
		}
		
		Inventory playerInventory = player.getInventory();
		
		// Only handle events for players on this team
		if(!this.hasPlayer(player)) {
			return;
		}
		
		ItemStack item = e.getItem().getItemStack();
		
		// If player picked up a trident (not base level)
		if(item.getType() == Material.TRIDENT
				&& item.getEnchantmentLevel(Enchantment.LOYALTY) != 1) 
		{
			// Remove all base tridents
			BedwarsPlayer bwPlayer = this.game.getBedwarsPlayer(player);
			while(bwPlayer.hasBaseTrident()) {			
				ItemStack baseTrident = bwPlayer.getBaseTrident();
				playerInventory.remove(baseTrident);
			}
		} 
	}
	
	/**
	 * Prevent a player from having a base level trident return
	 * to them if they already have another trident in inventory.
	 * Also remove base level trident from inventory if a player
	 * has a non-base trident return to them
	 * @param e	event being handled
	 */
	@EventHandler
	public void tridentReturnToPlayer(PlayerPickupArrowEvent e) {
		Player player = e.getPlayer();
		Inventory playerInventory = player.getInventory();

		// Only handle events for players on this team
		if(!this.hasPlayer(player)) {
			return;
		}
		
		AbstractArrow projectile = e.getArrow();
		Trident trident = null;
		
		if(projectile instanceof Trident) {
			trident = (Trident)projectile;
		} else {
			return;
		}
		
		// Get the itemstack associated with trident entity with NMS
		EntityThrownTrident thrownTrident = ((CraftTrident) trident).getHandle();
		ItemStack tridentItemStack = CraftItemStack.asBukkitCopy(thrownTrident.trident);
		BedwarsPlayer bwPlayer = this.game.getBedwarsPlayer(player);
		
		// If a base trident returned
		if(tridentItemStack.getType() == Material.TRIDENT
				&& tridentItemStack.getEnchantmentLevel(Enchantment.LOYALTY) == 1)
		{
			// If player has another trident, cancel the event and delete the trident being picked up
			if(bwPlayer.hasBaseTrident() 
					|| bwPlayer.hasUpgradedTrident()) 
			{
				trident.remove();
				e.setCancelled(true);	
			}
		} 
		// If upgraded trident returned, remove all base tridents
		else if(tridentItemStack.getType() == Material.TRIDENT
				&& tridentItemStack.getEnchantmentLevel(Enchantment.LOYALTY) != 1)
		{
			// Remove all base tridents
			while(bwPlayer.hasBaseTrident()) {			
				ItemStack baseTrident = bwPlayer.getBaseTrident();
				playerInventory.remove(baseTrident);
			}
		}
	}
	
	/**
	 * Disallow friendly fire
	 * @param e	the damage event being handled
	 */
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		Entity damagee = e.getEntity();
		Entity damager = e.getDamager();
		
		// In case damager is a projectile and not a player
		if(damager instanceof Projectile && ((Projectile)damager).getShooter() instanceof Player)
			damager = (Entity)((Projectile)damager).getShooter();
		
		if(damagee instanceof Player && damager instanceof Player) {
			Player pDamagee = (Player)damagee;
			Player pDamager = (Player)damager;

			// If both players are on this team, cancel the event
			if(this.hasPlayer(pDamagee) && this.hasPlayer(pDamager))
				e.setCancelled(true);
		}
	}
	
	/**
	 * Open correct shop associated with team villager
	 * @param e	the event being handled
	 */
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		Entity entity = e.getRightClicked();
		
		if(entity == this.itemShopVillager) {
			// Each player opens their own instance of the shop
			ItemShop playerShop = new ItemShop(this.teamColor.getColor(), this.itemShop);
			playerShop.initializeItems(p); // Populate with player specific tool upgrades
			playerShop.openInventory((HumanEntity)p);
			
		} else if (entity == this.teamShopVillager) {
			// Players open the same instance of the team shop
			this.game.getTeam(p).teamShop.openInventory((HumanEntity)p);
		}
	}
}
