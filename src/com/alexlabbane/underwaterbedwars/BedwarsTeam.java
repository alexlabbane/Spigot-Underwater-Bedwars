package com.alexlabbane.underwaterbedwars;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
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

import com.alexlabbane.underwaterbedwars.gui.ItemShop;
import com.alexlabbane.underwaterbedwars.util.LeveledEnchantment;
import com.alexlabbane.underwaterbedwars.util.TeamColor;
import com.mojang.datafixers.util.Pair;

import net.md_5.bungee.api.ChatColor;

public class BedwarsTeam implements Listener {
	private Plugin plugin;
	private TeamColor teamColor;
	private ArrayList<Player> players;
	private ArrayList<Pair<Material, LeveledEnchantment[]>> starterMaterials;
	private ArrayList<Pair<Material, LeveledEnchantment[]>> starterArmor;
	
	// Shop stuff
	private ItemShop itemShop;
	private Location itemShopLocation;
	private Entity itemShopVillager;
	
	public BedwarsTeam(ArrayList<Player> players, String color, Plugin p, BedwarsGame game) {
		this.plugin = p;
		this.teamColor = TeamColor.valueOf(color.toUpperCase());
		this.players = players;
		
		this.itemShop = new ItemShop(color, game);
		Bukkit.getServer().getPluginManager().registerEvents(this.itemShop, this.plugin);
		this.itemShopVillager = Bukkit.getServer().getPlayer("alab11").getWorld().spawnEntity(Bukkit.getServer().getPlayer("alab11").getLocation(), EntityType.VILLAGER);
		
		this.starterMaterials = new ArrayList<Pair<Material, LeveledEnchantment[]>>();
		this.starterArmor = new ArrayList<Pair<Material, LeveledEnchantment[]>>();
		this.initializeStarterMaterials();
		this.initializeStarterArmor();
	}
	
	public BedwarsTeam(String color, Plugin p, BedwarsGame game) {
		this.plugin = p;
		this.teamColor = TeamColor.valueOf(color.toUpperCase());
		this.players = new ArrayList<Player>();
		
		this.itemShop = new ItemShop(color, game);
		Bukkit.getServer().getPluginManager().registerEvents(this.itemShop, this.plugin);
		this.itemShopVillager = Bukkit.getServer().getPlayer("alab11").getWorld().spawnEntity(Bukkit.getServer().getPlayer("alab11").getLocation(), EntityType.VILLAGER);
		
		this.starterMaterials = new ArrayList<Pair<Material, LeveledEnchantment[]>>();
		this.starterArmor = new ArrayList<Pair<Material, LeveledEnchantment[]>>();
		this.initializeStarterMaterials();
		this.initializeStarterArmor();
	}
	
	public ArrayList<Player> getPlayers() { return this.players; }
	public TeamColor getColor() { return this.teamColor; }
	
	public void initializeStarterMaterials() {
		this.starterMaterials.add(new Pair<Material, LeveledEnchantment[]>(Material.TRIDENT, new LeveledEnchantment[]{ new LeveledEnchantment(Enchantment.LOYALTY, 1) })); // trident always at index 0
	}
	
	public void initializeStarterArmor() {
		this.starterArmor.add(new Pair<Material, LeveledEnchantment[]>(Material.LEATHER_BOOTS, null)); // boots always index 0
		this.starterArmor.add(new Pair<Material, LeveledEnchantment[]>(Material.LEATHER_LEGGINGS, null)); // leggings always index 1
		this.starterArmor.add(new Pair<Material, LeveledEnchantment[]>(Material.LEATHER_CHESTPLATE, null)); // chestplate always index 2
		this.starterArmor.add(new Pair<Material, LeveledEnchantment[]>(Material.LEATHER_HELMET, new LeveledEnchantment[] { new LeveledEnchantment(Enchantment.WATER_WORKER, 1)} )); // helmet always index 3
	}
	
	public void setPlayerArmor(Player player) {
		PlayerInventory inv = player.getInventory();

		ArrayList<ItemStack> finishedArmor = new ArrayList<ItemStack>();
		for(Pair<Material, LeveledEnchantment[]> armor : this.starterArmor) {
			ItemStack armorPiece = new ItemStack(armor.getFirst());
			if(armor.getSecond() != null)
				for(LeveledEnchantment le : armor.getSecond())
					armorPiece.addEnchantment(le.getEnchantment(), le.getLevel());
			
			finishedArmor.add(armorPiece);
		}
		
		inv.setBoots(finishedArmor.get(0));
		inv.setLeggings(finishedArmor.get(1));
		inv.setChestplate(finishedArmor.get(2));
		inv.setHelmet(finishedArmor.get(3));
		
		// Dye to match color
		for(ItemStack armor : inv.getArmorContents()) {
			ItemMeta meta = armor.getItemMeta();
			if(meta instanceof LeatherArmorMeta) {
				((LeatherArmorMeta) meta).setColor(this.getColor().RGB());
			}
			
			armor.setItemMeta(meta);
		}
	}
	
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
		if(!players.contains(player))
			players.add(player);
		
		player.getInventory().clear();
		this.setPlayerArmor(player);
		this.setPlayerStarterMaterials(player);
	}
	
	public boolean hasPlayer(Player player) {
		return this.players.contains(player);
	}
	
	public void removePlayer(Player player) {
		this.players.remove(player);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		e.getDrops().clear();
		if(this.hasPlayer(p)) {
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
					setPlayerArmor(p);
					setPlayerStarterMaterials(p);
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
			this.itemShop.openInventory((HumanEntity)p);
		}
	}
}
