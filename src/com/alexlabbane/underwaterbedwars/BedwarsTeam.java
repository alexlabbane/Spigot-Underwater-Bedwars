package com.alexlabbane.underwaterbedwars;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
import com.alexlabbane.underwaterbedwars.util.LeveledEnchantment;
import com.alexlabbane.underwaterbedwars.util.TeamColor;
import com.alexlabbane.underwaterbedwars.util.TrapQueue;
import com.alexlabbane.underwaterbedwars.util.Util;
import com.mojang.datafixers.util.Pair;

import net.md_5.bungee.api.ChatColor;

public class BedwarsTeam implements Listener {
	private Plugin plugin;
	private BedwarsGame game;
	private TeamColor teamColor;
	private ArrayList<BedwarsPlayer> bwPlayers;
	private ArrayList<Pair<Material, LeveledEnchantment[]>> starterMaterials;
	private ArrayList<Pair<Material, LeveledEnchantment[]>> starterArmor;
	
	// Team upgrades (TODO: add the rest)
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

	// Team bed
	private BedwarsBed bed;
	
	// Other important locations
	private Location chestLocation;
	private Location enderChestLocation;
	
	public BedwarsTeam(ArrayList<BedwarsPlayer> players, String color, Plugin p, BedwarsGame game) {
		this.bwPlayers = players;
		this.initializeTeam(color, p, game);
	}
	
	public BedwarsTeam(String color, Plugin p, BedwarsGame game) {
		this.bwPlayers = new ArrayList<BedwarsPlayer>();
		this.initializeTeam(color, p, game);
	}
	
	private void initializeTeam(String color, Plugin p, BedwarsGame game) {
		this.plugin = p;
		this.teamColor = TeamColor.valueOf(color.toUpperCase());
		this.game = game;
		
		this.impalingLevel = 0;
		this.protLevel = 0;
		this.hasteLevel = 0;
		this.forgeLevel = 0;
		this.healPoolLevel = 0;
		this.traps = new TrapQueue();
		this.healPool = null;
		
		// Initialize bed
		this.bed = new BedwarsBed(this.teamColor, this);
		
		// Initialize item shop
		this.itemShop = new ItemShop(color, game);
		
		Location itemShopLocation = new Location(
				Bukkit.getServer().getWorlds().get(0),
				this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.item-shop-location.x"),
				this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.item-shop-location.y"),
				this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.item-shop-location.z"));
		itemShopLocation.setPitch((float)this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.item-shop-location.pitch"));
		itemShopLocation.setYaw((float)this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.item-shop-location.yaw"));
		this.itemShopVillager = itemShopLocation.getWorld().spawnEntity(itemShopLocation, EntityType.VILLAGER);
		this.setItemShopLocation(itemShopLocation);
		
		this.itemShopVillager.setSilent(true);
		Util.freezeEntity(this.itemShopVillager);
		
		// TODO: Initialize team shop
		this.teamShop = new TeamShop(color, game);
		this.trapShop = new TrapShop(color, game);
		
		Location teamShopLocation = new Location(
				Bukkit.getServer().getWorlds().get(0),
				this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.team-shop-location.x"),
				this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.team-shop-location.y"),
				this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.team-shop-location.z"));
		teamShopLocation.setPitch((float)this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.team-shop-location.pitch"));
		teamShopLocation.setYaw((float)this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.team-shop-location.yaw"));
		this.teamShopVillager = teamShopLocation.getWorld().spawnEntity(teamShopLocation, EntityType.VILLAGER);
		this.setTeamShopLocation(teamShopLocation);
		
		this.teamShopVillager.setSilent(true);
		Util.freezeEntity(this.teamShopVillager);		
		// Initialize gen
		this.setGenLocation(new Location(
				this.itemShopLocation.getWorld(),
				this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.gen-location.x"),
				this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.gen-location.y"),
				this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.gen-location.z")));
				
		this.starterMaterials = new ArrayList<Pair<Material, LeveledEnchantment[]>>();
		this.starterArmor = new ArrayList<Pair<Material, LeveledEnchantment[]>>();
		this.initializeStarterMaterials();
		this.initializeStarterArmor();
		this.initializeGen();
	}
	
	public ArrayList<BedwarsPlayer> getBedwarsPlayers() { return this.bwPlayers; }
	public ArrayList<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		for(BedwarsPlayer bwPlayer : this.bwPlayers)
			players.add(bwPlayer.getPlayer());
		
		return players;
	}
	
	public TeamColor getColor() { return this.teamColor; }
	public TrapQueue getQueuedTraps() { return this.traps; }
	public TeamShop getTeamShop() { return this.teamShop; }
	public TrapShop getTrapShop() { return this.trapShop; }
	public BedwarsBed getBed() { return this.bed; }
	
	public void setImpalingLevel(int level) { 
		this.impalingLevel = level; 
		
		for(BedwarsPlayer bwPlayer : this.bwPlayers)
			bwPlayer.applyImpalingEffect();
	}
	public int getImpalingLevel() { return this.impalingLevel; }
	
	public void setHasteLevel(int level) { 
		this.hasteLevel = level; 
		
		// Update player haste
		for(BedwarsPlayer bwPlayer : this.bwPlayers)
			bwPlayer.setPlayerHaste();
	}
	public int getHasteLevel() { return this.hasteLevel; }
	
	public void setForgeLevel(int level) { this.forgeLevel = level; }
	public int getForgeLevel() { return this.forgeLevel; }
	
	public void setHealPoolLevel(int level) { this.healPoolLevel = level; }
	public int getHealPoolLevel() { return this.healPoolLevel; }
	
	public void setProtectionLevel(int level) { 
		this.protLevel = level;
		
		// Update player armor
		for(BedwarsPlayer bwPlayer : this.bwPlayers)
			bwPlayer.setPlayerArmor();
	}
	public int getProtLevel() { return this.protLevel; }
	
	public ArrayList<Pair<Material, LeveledEnchantment[]>> getStarterMaterials() { return this.starterMaterials; }
	public ArrayList<Pair<Material, LeveledEnchantment[]>> getStarterArmor() { return this.starterArmor; }
	
	public void setItemShopLocation(Location loc) { this.setShopLocation("ITEM", loc, this.itemShopVillager); }
	public void setTeamShopLocation(Location loc) {	this.setShopLocation("TEAM", loc, this.teamShopVillager); }
	
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
		}.runTaskLater(this.plugin, 20);
	}
	
	public void setGenLocation(Location loc) {
		this.genLocation = loc;
	}
	
	public void initializeStarterMaterials() {
		this.starterMaterials.add(new Pair<Material, LeveledEnchantment[]>(Material.TRIDENT, new LeveledEnchantment[]{ new LeveledEnchantment(Enchantment.LOYALTY, 1) })); // trident always at index 0
	}
	
	public void initializeStarterArmor() {
		this.starterArmor.add(new Pair<Material, LeveledEnchantment[]>(Material.LEATHER_BOOTS, null)); // boots always index 0
		this.starterArmor.add(new Pair<Material, LeveledEnchantment[]>(Material.LEATHER_LEGGINGS, null)); // leggings always index 1
		this.starterArmor.add(new Pair<Material, LeveledEnchantment[]>(Material.LEATHER_CHESTPLATE, null)); // chestplate always index 2
		this.starterArmor.add(new Pair<Material, LeveledEnchantment[]>(Material.LEATHER_HELMET, new LeveledEnchantment[] { new LeveledEnchantment(Enchantment.WATER_WORKER, 1)} )); // helmet always index 3
	}
	
	public void initializeGen() {
		if(this.genSpawner != null)
			this.genSpawner.cancel();		
		
		this.genSpawner = new BukkitRunnable() {
			int counter = 0;
			
			@Override
			public void run() {
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
				
				if(counter % 32 == 0 && forgeLevel >= 3) {
					// Spawn emerald 1/32 as often as iron
					ItemStack emerald = new ItemStack(Material.EMERALD);
					if(Util.countDroppedItems(genLocation, emerald.getType(), 1.5f) < 4) {
						Item droppedEmerald = genLocation.getWorld().dropItem(genLocation, emerald);
						droppedEmerald.setVelocity(new Vector(0, 0, 0));
					}
				}
				
				counter++;
			}
			
		}.runTaskTimer(this.plugin, 20, getGenDelay());
	}
	
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
		}.runTaskTimer(this.plugin, 20, 20);
	}
	
	public void stopHealPool() {
		if(this.healPool == null) {
			return;
		}
		
		this.healPool.cancel();
	}
	
	// TODO: Perform this in BedwarsPlayer class
	public void setPlayerStarterMaterials(Player player) {
		PlayerInventory inv = player.getInventory();
		
		for(Pair<Material, LeveledEnchantment[]> itemPair : this.starterMaterials) {
			ItemStack item = new ItemStack(itemPair.getFirst());
			if(itemPair.getSecond() != null)
				for(LeveledEnchantment le : itemPair.getSecond())
					item.addEnchantment(le.getEnchantment(), le.getLevel());

			inv.addItem(item);
		}
	}
	
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
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		e.getDrops().clear();
		
		// Downgrade tools
		BedwarsPlayer bwPlayer = this.game.getBedwarsPlayer(p);
		
		if(bwPlayer != null && this.hasPlayer(p)) {
			bwPlayer.downgradeAxe();
			bwPlayer.downgradePickaxe();
			
			// Respawn player immediately in spectator
			new BukkitRunnable() {
				@Override
				public void run() {
					p.spigot().respawn();
					p.setGameMode(GameMode.SPECTATOR);
				}
			}.runTaskLater(this.plugin, 1);			
			
			// Update player title
			for(int i = 0; i < 5; i++) {
				final int secondsLeft = 5 - i;
				new BukkitRunnable() {
					@Override
					public void run() {
						p.sendTitle(ChatColor.RED + "Respawn in " + secondsLeft + " seconds...", ChatColor.RED + "Please wait.", 0, 20, 0);
					}
				}.runTaskLater(this.plugin, 20 * i);	
			}

			// Put the player back at base and in survival
			new BukkitRunnable() {
				@Override
				public void run() {
					// TODO: Send to teams respawn location
					p.setGameMode(GameMode.SURVIVAL);
					bwPlayer.setPlayerArmor();
					bwPlayer.setPlayerStarterMaterials();
					bwPlayer.setPlayerHaste();
				}
			}.runTaskLater(this.plugin, 20 * 5);			
		}
	}
	
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
			
			Bukkit.broadcastMessage(pDamagee.getName() + " " + this.hasPlayer(pDamagee) + " " + pDamager.getName() + " " + this.hasPlayer(pDamager));
			if(this.hasPlayer(pDamagee) && this.hasPlayer(pDamager))
				e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		Entity entity = e.getRightClicked();
		
		if(entity == this.itemShopVillager) {
			// Each player opens their own instance of the shop
			ItemShop playerShop = new ItemShop(this.teamColor.getColor(), this.itemShop);
			playerShop.initializeItems(p); // Populate with player specific tool upgrades
			playerShop.openInventory((HumanEntity)p);
			// this.itemShop.openInventory((HumanEntity)p); // DEPRECATED: each player opens same instance of the shop
		} else if (entity == this.teamShopVillager) {
			// Players open the same instance of the team shop
			this.game.getTeam(p).teamShop.openInventory((HumanEntity)p);
		}
	}	
}
