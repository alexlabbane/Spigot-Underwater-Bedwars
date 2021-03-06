package com.alexlabbane.underwaterbedwars.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.alexlabbane.underwaterbedwars.BedwarsGame;

/**
 * Shop inventory for all block related items
 * @author Alex Labbane
 *
 */
public class BlockShop extends ItemShop implements Listener {	
	/**
	 * Create new block shop
	 * @param color		the color of the team the shop is for
	 * @param game		reference to the game the shop is a part of
	 */
	public BlockShop(String color, BedwarsGame game) {
		super(color, game, "Blocks");
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
		
		// Add blocks for purchase to shop
		this.inv.setItem(19, this.createShopItem(this.color.toUpperCase() + "_WOOL", 16, "Buy Wool (4 iron)", 4, "IRON_INGOT"));
		this.inv.setItem(20, this.createShopItem("TERRACOTTA", 16, "Buy Terracotta (12 iron)", 12, "IRON_INGOT"));
		this.inv.setItem(21, this.createShopItem("GLASS", 4, "Buy Blast Proof Glass (12 iron)", 12, "IRON_INGOT"));
		this.inv.setItem(22, this.createShopItem("END_STONE", 12, "Buy End Stone (24 iron)", 24, "IRON_INGOT"));
		this.inv.setItem(23, this.createShopItem("LADDER", 16, "Buy Ladders (12 iron)", 12, "IRON_INGOT"));
		this.inv.setItem(24, this.createShopItem("OAK_PLANKS", 16, "Buy Wood (4 gold)", 4, "GOLD_INGOT"));
		this.inv.setItem(25, this.createShopItem("OBSIDIAN", 4, "Buy Obsidian (4 emerald)", 4, "EMERALD"));
	}
}
