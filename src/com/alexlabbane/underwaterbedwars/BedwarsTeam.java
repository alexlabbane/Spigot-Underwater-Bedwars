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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.alexlabbane.underwaterbedwars.gui.ItemShop;
import com.alexlabbane.underwaterbedwars.util.LeveledEnchantment;
import com.alexlabbane.underwaterbedwars.util.TeamColor;
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
	private boolean sharpness;
	private int protLevel;
	
	// Shop stuff
	private ItemShop itemShop;
	private Location itemShopLocation;
	private Entity itemShopVillager;
	
	private Location teamShopLocation;
	
	// Gen stuff
	private BukkitTask genSpawner;
	private Location genLocation;

	// Other important locations
	private Location bedLocation;
	private Location chestLocation;
	private Location enderChestLocation;
	
	public BedwarsTeam(ArrayList<BedwarsPlayer> players, String color, Plugin p, BedwarsGame game) {
		this.plugin = p;
		this.teamColor = TeamColor.valueOf(color.toUpperCase());
		this.bwPlayers = players;
		this.game = game;
		
		this.sharpness = false;
		this.protLevel = 0;
		
		this.itemShop = new ItemShop(color, game);
		Bukkit.getServer().getPluginManager().registerEvents(this.itemShop, this.plugin);
		this.itemShopVillager = Bukkit.getServer().getPlayer("alab11").getWorld().spawnEntity(Bukkit.getServer().getPlayer("alab11").getLocation(), EntityType.VILLAGER);
		this.itemShopVillager.setSilent(true);
		Util.freezeEntity(this.itemShopVillager);
		Location itemShopLocation = new Location(
				this.itemShopVillager.getWorld(),
				this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.item-shop-location.x"),
				this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.item-shop-location.y"),
				this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.item-shop-location.z"));
		itemShopLocation.setPitch((float)this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.item-shop-location.pitch"));
		itemShopLocation.setYaw((float)this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.item-shop-location.yaw"));
		this.setItemShopLocation(itemShopLocation);
		
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
	
	public BedwarsTeam(String color, Plugin p, BedwarsGame game) {
		this.plugin = p;
		this.teamColor = TeamColor.valueOf(color.toUpperCase());
		this.bwPlayers = new ArrayList<BedwarsPlayer>();
		this.game = game;
		
		this.sharpness = false;
		this.protLevel = 0;
		
		this.itemShop = new ItemShop(color, game);
		Bukkit.getServer().getPluginManager().registerEvents(this.itemShop, this.plugin);
		
		// DEBUG
		this.itemShopVillager = Bukkit.getServer().getPlayer("alab11").getWorld().spawnEntity(Bukkit.getServer().getPlayer("alab11").getLocation(), EntityType.VILLAGER);
		
		Location itemShopLocation = new Location(
				this.itemShopVillager.getWorld(),
				this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.item-shop-location.x"),
				this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.item-shop-location.y"),
				this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.item-shop-location.z"));
		itemShopLocation.setPitch((float)this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.item-shop-location.pitch"));
		itemShopLocation.setYaw((float)this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.item-shop-location.yaw"));
		this.setItemShopLocation(itemShopLocation);
		
		this.setGenLocation(new Location(
				this.itemShopLocation.getWorld(),
				this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.gen-location.x"),
				this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.gen-location.y"),
				this.plugin.getConfig().getDouble(color.toLowerCase() + "-team.gen-location.z")));
		
		this.itemShopVillager.setSilent(true);
		Util.freezeEntity(this.itemShopVillager);
		
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
	
	public void setSharpness(boolean b) { this.sharpness = b; }
	public boolean hasSharpness() { return this.sharpness; }
	
	public void setProtectionLevel(int level) { this.protLevel = level; }
	public int getProtLevel() { return this.protLevel; }
	
	public ArrayList<Pair<Material, LeveledEnchantment[]>> getStarterMaterials() { return this.starterMaterials; }
	public ArrayList<Pair<Material, LeveledEnchantment[]>> getStarterArmor() { return this.starterArmor; }
	
	public void setItemShopLocation(Location loc) {
		this.itemShopLocation = loc;
		
		// Delay teleportation by 1 second
		// Fixed undesirable teleportation lag
		new BukkitRunnable() {
			@Override
			public void run() {
				itemShopVillager.teleport(loc);
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
				// TODO: Count how much stuff already in the gen
				// Write utility function to get all entities in certain radius of location??
				
				ItemStack iron = new ItemStack(Material.IRON_INGOT);
				
				if(Util.countDroppedItems(genLocation, iron.getType(), 1.5f) < 48) {
					Item droppedIron = genLocation.getWorld().dropItem(genLocation, iron);
					droppedIron.setVelocity(new Vector(0, 0, 0));
				}
				
				if(counter % 4 == 0) {
					// Spawn gold 1/4 as often
					ItemStack gold = new ItemStack(Material.GOLD_INGOT);
					
					if(Util.countDroppedItems(genLocation, gold.getType(), 1.5f) < 16) {
						Item droppedGold = genLocation.getWorld().dropItem(genLocation, gold);
						droppedGold.setVelocity(new Vector(0, 0, 0));	
					}
				}
				counter++;
			}
			
		}.runTaskTimer(this.plugin, 20, 15);
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
		}
	}
}
