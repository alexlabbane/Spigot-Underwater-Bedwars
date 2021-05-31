package com.alexlabbane.underwaterbedwars.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.alexlabbane.underwaterbedwars.BedwarsGame;
import com.alexlabbane.underwaterbedwars.listeners.WaterListener;

/**
 * Shop inventory for armor related items
 * @author Alex Labbane
 *
 */
public class ArmorShop extends ItemShop implements Listener {
	/**
	 * Create a new armor shop
	 * @param color		the color of the team the shop is for
	 * @param game		reference to the game the shop is a part of
	 */
	public ArmorShop(String color, BedwarsGame game) {
		super(color, game, "Armor");
	}

	/**
	 * Initialize all of the items/links that should be in the shop
	 * @param player	the player to create the shop for
	 */
	@Override
	public void initializeItems(Player player) {
		// Create links to other shop pages
		this.inv.setItem(0, this.createShopLink("NETHER_STAR", 1, "Quick Buy", "SHOP_QUICK_BUY", this.color));
		this.inv.setItem(1, this.createShopLink("TERRACOTTA", 1, "Blocks", "SHOP_BLOCKS", this.color));
		this.inv.setItem(2, this.createShopLink("GOLDEN_SWORD", 1, "Melee", "SHOP_MELEE", this.color));
		this.inv.setItem(3, this.createShopLink("CHAINMAIL_BOOTS", 1, "Armor", "SHOP_ARMOR", this.color));
		this.inv.setItem(4, this.createShopLink("STONE_PICKAXE", 1, "Tools", "SHOP_TOOLS", this.color));
		this.inv.setItem(5, this.createShopLink("BOW", 1, "Ranged", "SHOP_RANGED", this.color));
		this.inv.setItem(6, this.createShopLink("BREWING_STAND", 1, "Potions", "SHOP_POTIONS", this.color));
		this.inv.setItem(7, this.createShopLink("TNT", 1, "Utilities", "SHOP_UTILITIES", this.color));
		
		// Add armor for purchase to shop
		this.inv.setItem(19, this.createSpecialShopItem("SPECIAL_CHAINMAIL_BOOTS", 1, "Buy Chain Armor (40 iron)", 40, "IRON_INGOT"));
		this.inv.setItem(20, this.createSpecialShopItem("SPECIAL_IRON_BOOTS", 1, "Buy Iron Armor (12 gold)", 12, "GOLD_INGOT"));
		this.inv.setItem(21, this.createSpecialShopItem("SPECIAL_DIAMOND_BOOTS", 1, "Buy Diamond Armor (6 emerald)", 6, "EMERALD"));
	}
}
