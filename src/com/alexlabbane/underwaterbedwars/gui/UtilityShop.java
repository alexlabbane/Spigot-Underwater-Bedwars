package com.alexlabbane.underwaterbedwars.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.alexlabbane.underwaterbedwars.BedwarsGame;
import com.alexlabbane.underwaterbedwars.listeners.WaterListener;

public class UtilityShop extends ItemShop implements Listener {	
	public UtilityShop(String color, BedwarsGame game) {
		super(color, game, "Blocks");
	}

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
		this.inv.setItem(19, this.createShopItem("GOLDEN_APPLE", 1, "Buy Golden Apple (3 gold)", 3, "GOLD_INGOT"));
		this.inv.setItem(20, this.createShopItem("SILVERFISH_SPAWN_EGG", 1, "Buy Bed Bug (40 iron)", 40, "IRON_INGOT"));
		this.inv.setItem(21, this.createShopItem("GUARDIAN_SPAWN_EGG", 1, "Buy Dream Defender (120 iron)", 120, "IRON_INGOT"));
		this.inv.setItem(22, this.createShopItem("FIRE_CHARGE", 1, "Buy Fireball (40 iron)", 40, "IRON_INGOT"));
		this.inv.setItem(23, this.createShopItem("TNT", 1, "Buy TNT (4 gold)", 4, "GOLD_INGOT"));
		this.inv.setItem(24, this.createShopItem("ENDER_PEARL", 1, "Buy Ender Pearl (4 emerald)", 4, "EMERALD"));
		this.inv.setItem(25, this.createShopItem("WATER_BUCKET", 1, "Buy Water Bucket (3 gold)", 3, "GOLD_INGOT"));

		this.inv.setItem(28, this.createShopItem("GOLDEN_APPLE", 1, "Buy Golden Apple (3 gold)", 3, "GOLD_INGOT"));
		this.inv.setItem(29, this.createShopItem("MILK_BUCKET", 1, "Buy Magic Milk (5 gold)", 5, "GOLD_INGOT"));
		this.inv.setItem(30, this.createShopItem("SPONGE", 4, "Buy Sponge (4 gold)", 4, "GOLD_INGOT"));
		this.inv.setItem(31, this.createShopItem("CHEST", 1, "Buy Compact Pop-Up Tower (24 iron)", 24, "IRON_INGOT"));

	}
}
