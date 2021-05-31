package com.alexlabbane.underwaterbedwars.gui;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.alexlabbane.underwaterbedwars.BedwarsGame;
import com.alexlabbane.underwaterbedwars.BedwarsTeam;
import com.alexlabbane.underwaterbedwars.util.LeveledEnchantment;

/**
 * Shop inventory for melee related items
 * @author Alex Labbane
 *
 */
public class MeleeShop extends ItemShop implements Listener {	
	/**
	 * Create a new melee shop
	 * @param color		the color of the team the shop is for
	 * @param game		reference to the game the shop is a part of
	 */
	public MeleeShop(String color, BedwarsGame game) {
		super(color, game, "Melee");
	}

	/**
	 * Initialize all of the items/links that should be in the shop
	 * @param player	the player to create the shop for
	 */
	@Override
	public void initializeItems(Player player) {
		BedwarsTeam team = this.bedwarsGame.getTeam(player);
		LeveledEnchantment[] trident2Enchants = null;
		LeveledEnchantment[] trident3Enchants = null;
		LeveledEnchantment[] trident4Enchants = null;
		if(team != null && team.getImpalingLevel() > 0) {
			trident2Enchants = new LeveledEnchantment[]{
					new LeveledEnchantment(Enchantment.LOYALTY, 2),
					new LeveledEnchantment(Enchantment.DAMAGE_ALL, team.getImpalingLevel())};
			trident3Enchants = new LeveledEnchantment[]{
					new LeveledEnchantment(Enchantment.LOYALTY, 3),
					new LeveledEnchantment(Enchantment.DAMAGE_ALL, team.getImpalingLevel())};
			trident4Enchants = new LeveledEnchantment[]{
					new LeveledEnchantment(Enchantment.RIPTIDE, 2),
					new LeveledEnchantment(Enchantment.DAMAGE_ALL, team.getImpalingLevel())};
		} else {
			trident2Enchants = new LeveledEnchantment[]{new LeveledEnchantment(Enchantment.LOYALTY, 2)};
			trident3Enchants = new LeveledEnchantment[]{new LeveledEnchantment(Enchantment.LOYALTY, 3)};
			trident4Enchants = new LeveledEnchantment[]{new LeveledEnchantment(Enchantment.RIPTIDE, 2)};
		}
		
		// Create links to other shop pages
		this.inv.setItem(0, this.createShopLink("NETHER_STAR", 1, "Quick Buy", "SHOP_QUICK_BUY", this.color));
		this.inv.setItem(1, this.createShopLink("TERRACOTTA", 1, "Blocks", "SHOP_BLOCKS", this.color));
		this.inv.setItem(2, this.createShopLink("GOLDEN_SWORD", 1, "Melee", "SHOP_MELEE", this.color));
		this.inv.setItem(3, this.createShopLink("CHAINMAIL_BOOTS", 1, "Armor", "SHOP_ARMOR", this.color));
		this.inv.setItem(4, this.createShopLink("STONE_PICKAXE", 1, "Tools", "SHOP_TOOLS", this.color));
		this.inv.setItem(5, this.createShopLink("BOW", 1, "Ranged", "SHOP_RANGED", this.color));
		this.inv.setItem(6, this.createShopLink("BREWING_STAND", 1, "Potions", "SHOP_POTIONS", this.color));
		this.inv.setItem(7, this.createShopLink("TNT", 1, "Utilities", "SHOP_UTILITIES", this.color));
		
		// Add blocks for purchase to shop
		this.inv.setItem(19, this.createEnchantedShopItem("TRIDENT", 1, "Buy Trident II (10 iron)", 10, "IRON_INGOT", trident2Enchants));
		this.inv.setItem(20, this.createEnchantedShopItem("TRIDENT", 1, "Buy Trident III (7 gold)", 7, "GOLD_INGOT", trident3Enchants));
		this.inv.setItem(21, this.createEnchantedShopItem("TRIDENT", 1, "Buy Trident IV (4 emerald)", 4, "EMERALD", trident4Enchants));
		this.inv.setItem(22, this.createEnchantedShopItem("STICK", 1, "Buy Knockback Stick (5 gold)", 5, "GOLD_INGOT", new LeveledEnchantment[]{new LeveledEnchantment(Enchantment.KNOCKBACK, 2)}));
	}
}
