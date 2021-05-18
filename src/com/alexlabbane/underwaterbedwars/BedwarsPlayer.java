package com.alexlabbane.underwaterbedwars;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.alexlabbane.underwaterbedwars.util.BedwarsArmor;
import com.alexlabbane.underwaterbedwars.util.BedwarsTools;
import com.alexlabbane.underwaterbedwars.util.LeveledEnchantment;
import com.alexlabbane.underwaterbedwars.util.Util;
import com.mojang.datafixers.util.Pair;

/**
 * Class to wrap Player objects with additional bedwars game state information
 * needed for individual players (such as tools upgrades, armor upgrades, etc).
 * Also provides various utility functions for player management
 * @author scien
 *
 */
public class BedwarsPlayer {
	private Player player;
	private BedwarsTeam team;
	
	private BedwarsArmor armor;
	private BedwarsTools.Axe axe;
	private BedwarsTools.Pickaxe pickaxe;
	private BedwarsTools.Shears shears;
	
	/**
	 * Construct a new Bedwars Player attached to player p
	 * Starts players with no tools and base armor
	 * @param p
	 */
	public BedwarsPlayer(Player p) {
		this.player = p;
		this.team = null;
		this.armor = BedwarsArmor.LEATHER;
		this.axe = BedwarsTools.Axe.NONE;
		this.pickaxe = BedwarsTools.Pickaxe.NONE;
		this.shears = BedwarsTools.Shears.NONE;
	}
	
	public Player getPlayer() { return this.player; }
	
	public BedwarsTeam getTeam() { return this.team; }
	public void setTeam(BedwarsTeam team) { this.team = team; }
	
	public BedwarsTools.Axe getAxe() { return this.axe; }
	public void setAxe(BedwarsTools.Axe axe) { this.axe = axe; }

	public BedwarsTools.Pickaxe getPickaxe() { return this.pickaxe; }
	public void setPickaxe(BedwarsTools.Pickaxe pickaxe) { this.pickaxe = pickaxe; }
	
	public BedwarsTools.Shears getShears() { return this.shears; }
	public void setShears(BedwarsTools.Shears shears) { this.shears = shears; }
	
	public BedwarsArmor getArmor() { return this.armor; }
	
	/**
	 * Sets the armor level of player to newArmor
	 * Allows armor downgrades (i.e. from iron to leather)
	 * @param newArmor
	 */
	public void setArmor(BedwarsArmor newArmor) {
		this.armor = newArmor;
	}
	
	/**
	 * Set the players armor to whatever armor they have bought
	 * Adds enchantments based on team upgrades
	 */
	public void setPlayerArmor() {
		PlayerInventory inv = player.getInventory();

		ArrayList<ItemStack> finishedArmor = new ArrayList<ItemStack>();
		for(Pair<Material, LeveledEnchantment[]> armor : team.getStarterArmor()) {
			ItemStack armorPiece = new ItemStack(armor.getFirst());
			if(armor.getSecond() != null)
				for(LeveledEnchantment le : armor.getSecond())
					armorPiece.addEnchantment(le.getEnchantment(), le.getLevel());
			
			finishedArmor.add(armorPiece);
		}
		
		// Add purchased armor upgrades and enchantments
		switch(this.armor) {
		case CHAIN:
			finishedArmor.set(0, new ItemStack(Material.CHAINMAIL_BOOTS));
			finishedArmor.set(1, new ItemStack(Material.CHAINMAIL_LEGGINGS));
			break;
		case IRON:
			finishedArmor.set(0, new ItemStack(Material.IRON_BOOTS));
			finishedArmor.set(1, new ItemStack(Material.IRON_LEGGINGS));
			break;
		case DIAMOND:
			finishedArmor.set(0, new ItemStack(Material.DIAMOND_BOOTS));
			finishedArmor.set(1, new ItemStack(Material.DIAMOND_LEGGINGS));
			break;
		}
		
		// Add protection
		int protLevel = this.team.getProtLevel();
		if(protLevel > 0 && protLevel <= 4) {
			finishedArmor.get(0).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protLevel);
			finishedArmor.get(1).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protLevel);
			finishedArmor.get(2).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protLevel);
		}
		
		// Dye to match color
		for(ItemStack armor : finishedArmor) {
			ItemMeta meta = armor.getItemMeta();
			if(meta instanceof LeatherArmorMeta) {
				((LeatherArmorMeta) meta).setColor(this.getTeam().getColor().RGB());
			}
			
			armor.setItemMeta(meta);
		}
		
		inv.setBoots(finishedArmor.get(0));
		inv.setLeggings(finishedArmor.get(1));
		inv.setChestplate(finishedArmor.get(2));
		inv.setHelmet(finishedArmor.get(3));
	}
	
	public void setPlayerStarterMaterials() {
		PlayerInventory inv = this.player.getInventory();
		
		// Add team-wide starter materials
		for(Pair<Material, LeveledEnchantment[]> itemPair : this.team.getStarterMaterials()) {
			ItemStack item = new ItemStack(itemPair.getFirst());
			if(itemPair.getSecond() != null)
				for(LeveledEnchantment le : itemPair.getSecond())
					item.addEnchantment(le.getEnchantment(), le.getLevel());

			inv.addItem(item);
		}
		
		// Add player specific materials (i.e. purchased tools)
		ItemStack pickaxe;
		switch(this.pickaxe) {
		case WOOD:
			pickaxe = new ItemStack(Material.WOODEN_PICKAXE);
			pickaxe.addEnchantment(Enchantment.DIG_SPEED, 1);
			inv.addItem(pickaxe);
			break;
		case IRON:
			pickaxe = new ItemStack(Material.IRON_PICKAXE);
			pickaxe.addEnchantment(Enchantment.DIG_SPEED, 2);
			inv.addItem(pickaxe);
			break;
		case GOLD:
			pickaxe = new ItemStack(Material.GOLDEN_PICKAXE);
			pickaxe.addEnchantment(Enchantment.DIG_SPEED, 3);
			pickaxe.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
			inv.addItem(pickaxe);
			break;
		case DIAMOND:
			pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
			pickaxe.addEnchantment(Enchantment.DIG_SPEED, 3);
			inv.addItem(pickaxe);
			break;
		}
		
		ItemStack axe;
		switch(this.axe) {
		case WOOD:
			axe = new ItemStack(Material.WOODEN_AXE);
			axe.addEnchantment(Enchantment.DIG_SPEED, 1);
			inv.addItem(axe);
			break;
		case IRON:
			axe = new ItemStack(Material.IRON_AXE);
			axe.addEnchantment(Enchantment.DIG_SPEED, 2);
			inv.addItem(axe);
			break;
		case GOLD:
			axe = new ItemStack(Material.GOLDEN_AXE);
			axe.addEnchantment(Enchantment.DIG_SPEED, 3);
			axe.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
			inv.addItem(axe);
			break;
		case DIAMOND:
			axe = new ItemStack(Material.DIAMOND_AXE);
			axe.addEnchantment(Enchantment.DIG_SPEED, 3);
			inv.addItem(axe);
			break;
		}
		
		ItemStack shears = new ItemStack(Material.SHEARS);
		if(this.shears == BedwarsTools.Shears.SHEARS)
			inv.addItem(shears);
		
		this.applyImpalingEffect();
	}
	
	/**
	 * Apply haste effect to player based on team upgrade
	 */
	public void setPlayerHaste() {
		if(this.team == null) return;
		
		if(this.team.getHasteLevel() > 0) {
			this.player.addPotionEffect(
					new PotionEffect(
							PotionEffectType.FAST_DIGGING, 
							Integer.MAX_VALUE, 
							this.team.getHasteLevel() - 1));
		}
	}
	
	/**
	 * Apply impaling to any tridents the player is holding
	 */
	public void applyImpalingEffect() {
		Inventory playerInv = this.player.getInventory();
		for(int i = 0; i < playerInv.getSize(); i++) {
			ItemStack itemStack = playerInv.getItem(i);
			if(itemStack == null) continue;
			
			if(itemStack.getType() == Material.TRIDENT && this.team.getImpalingLevel() > 0)
				itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, this.team.getImpalingLevel());
		}
	}
	
	/**
	 * Upgrade the pickaxe of a Bedwars Player
	 * Will fail to upgrade if player already has highest tier pickaxe
	 * @return true if upgrade occurred, false if upgrade failed
	 */
	public boolean upgradePickaxe() {
		if(this.pickaxe != BedwarsTools.Pickaxe.getUpgrade(this.pickaxe)) {
			this.pickaxe = BedwarsTools.Pickaxe.getUpgrade(this.pickaxe);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Downgrade the pickaxe of a Bedwars Player
	 * Will fail to downgrade if player has no pickaxe or lowest tier pickaxe
	 * @return true if downgrade occurred, false if downgrade failed
	 */
	public boolean downgradePickaxe() {
		if(this.pickaxe != BedwarsTools.Pickaxe.getDowngrade(this.pickaxe)) {
			this.pickaxe = BedwarsTools.Pickaxe.getDowngrade(this.pickaxe);
			return true;
		}
		
		return false;	
	}

	/**
	 * Upgrade the axe of a Bedwars Player
	 * Will fail to upgrade if player already has highest tier axe
	 * @return true if upgrade occurred, false if upgrade failed
	 */
	public boolean upgradeAxe() {
		if(this.axe != BedwarsTools.Axe.getUpgrade(this.axe)) {
			this.axe = BedwarsTools.Axe.getUpgrade(this.axe);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Downgrade the axe of a Bedwars Player
	 * Will fail to downgrade if player has no axe or lowest tier axe
	 * @return true if downgrade occurred, false if downgrade failed
	 */
	public boolean downgradeAxe() {
		if(this.axe != BedwarsTools.Axe.getDowngrade(this.axe)) {
			this.axe = BedwarsTools.Axe.getDowngrade(this.axe);
			return true;
		}
		
		return false;	
	}
	
	/**
	 * Upgrade the axe of a Bedwars Player
	 * Will fail to upgrade if player already has highest tier axe
	 * @return true if upgrade occurred, false if upgrade failed
	 */
	public boolean upgradeShears() {
		if(this.shears != BedwarsTools.Shears.getUpgrade(this.shears)) {
			this.shears = BedwarsTools.Shears.getUpgrade(this.shears);
			return true;
		}
		
		return false;
	}
	
	public void setPlayerPickaxe() {
		PlayerInventory inv = this.player.getInventory();
		
		if(inv.contains(Material.WOODEN_PICKAXE)
				|| inv.contains(Material.IRON_PICKAXE)
				|| inv.contains(Material.GOLDEN_PICKAXE)
				|| inv.contains(Material.DIAMOND_PICKAXE)) 
		{
			// If player already has a pickaxe in their inventory, replace it with upgrade
			for(int i = 0; i < inv.getContents().length; i++) {
				if(inv.getContents()[i] != null
						&& (inv.getContents()[i].getType() == Material.WOODEN_PICKAXE
						|| inv.getContents()[i].getType() == Material.IRON_PICKAXE
						|| inv.getContents()[i].getType() == Material.GOLDEN_PICKAXE
						|| inv.getContents()[i].getType() == Material.DIAMOND_PICKAXE)) {
					inv.setItem(i, BedwarsTools.Pickaxe.getBedwarsTool(this.pickaxe));
				}
			}
		} else {
			// If no pickaxe previously in inventory, add one to it
			inv.addItem(BedwarsTools.Pickaxe.getBedwarsTool(this.pickaxe));
		}
	}
	
	public void setPlayerAxe() {
		PlayerInventory inv = this.player.getInventory();
		
		if(inv.contains(Material.WOODEN_AXE)
				|| inv.contains(Material.IRON_AXE)
				|| inv.contains(Material.GOLDEN_AXE)
				|| inv.contains(Material.DIAMOND_AXE)) 
		{
			// If player already has a pickaxe in their inventory, replace it with upgrade
			for(int i = 0; i < inv.getContents().length; i++) {
				if(inv.getContents()[i] != null
						&& (inv.getContents()[i].getType() == Material.WOODEN_AXE
						|| inv.getContents()[i].getType() == Material.IRON_AXE
						|| inv.getContents()[i].getType() == Material.GOLDEN_AXE
						|| inv.getContents()[i].getType() == Material.DIAMOND_AXE)) {
					inv.setItem(i, BedwarsTools.Axe.getBedwarsTool(this.axe));
				}
			}
		} else {
			// If no pickaxe previously in inventory, add one to it
			inv.addItem(BedwarsTools.Axe.getBedwarsTool(this.axe));
		}
	}
	
	public void setPlayerShears() {
		PlayerInventory inv = this.player.getInventory();
		
		if(inv.contains(Material.SHEARS))
		{
			// If player already has a pickaxe in their inventory, replace it with upgrade
			for(int i = 0; i < inv.getContents().length; i++) {
				if(inv.getContents()[i] != null
						&& (inv.getContents()[i].getType() == Material.SHEARS)) {
					inv.setItem(i, BedwarsTools.Shears.getBedwarsTool(this.shears));
				}
			}
		} else {
			// If no pickaxe previously in inventory, add one to it
			inv.addItem(BedwarsTools.Shears.getBedwarsTool(this.shears));
		}
	}

	public boolean insideBase() {
		double playerX = this.getPlayer().getLocation().getX();
		double playerZ = this.getPlayer().getLocation().getZ();
		double xMin = Util.plugin.getConfig().getDouble(this.team.getColor().getColor().toLowerCase() + "-team.base-bounds.x-min");
		double xMax = Util.plugin.getConfig().getDouble(this.team.getColor().getColor().toLowerCase() + "-team.base-bounds.x-max");
		double zMin = Util.plugin.getConfig().getDouble(this.team.getColor().getColor().toLowerCase() + "-team.base-bounds.z-min");
		double zMax = Util.plugin.getConfig().getDouble(this.team.getColor().getColor().toLowerCase() + "-team.base-bounds.z-max");
		
		return (playerX >= xMin && playerX <= xMax && playerZ >= zMin && playerZ <= zMax);
	}
}
