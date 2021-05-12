package com.alexlabbane.underwaterbedwars.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.alexlabbane.underwaterbedwars.BedwarsGame;
import com.alexlabbane.underwaterbedwars.util.BedwarsTools;

public class ToolShop extends ItemShop implements Listener {	
	public ToolShop(String color, BedwarsGame game) {
		super(color, game, "Tools");
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
		// Tools upgrades
		if(player != null) {
			int invSlot = 19;
			
			this.inv.setItem(invSlot, this.createSpecialShopItem("SPECIAL_SHEARS", 1, "Buy Shears (20 iron)", 20, "IRON_INGOT"));
			
			invSlot++;
			
			// Pickaxe
			switch(BedwarsTools.Pickaxe.getUpgrade(this.bedwarsGame.getBedwarsPlayer(player).getPickaxe())) {
			case WOOD:
				this.inv.setItem(invSlot, this.createSpecialShopItem("SPECIAL_WOODEN_PICKAXE", 1, "Buy Wood Pickaxe (10 iron)", 10, "IRON_INGOT"));
				break;
			case IRON:
				this.inv.setItem(invSlot, this.createSpecialShopItem("SPECIAL_IRON_PICKAXE", 1, "Buy Iron Pickaxe (10 iron)", 10, "IRON_INGOT"));
				break;
			case GOLD:
				this.inv.setItem(invSlot, this.createSpecialShopItem("SPECIAL_GOLDEN_PICKAXE", 1, "Buy Gold Pickaxe (3 gold)", 3, "GOLD_INGOT"));
				break;
			case DIAMOND:
				this.inv.setItem(invSlot, this.createSpecialShopItem("SPECIAL_DIAMOND_PICKAXE", 1, "Buy Diamond Pickaxe (6 gold)", 6, "GOLD_INGOT"));
				break;
			}
			
			invSlot++;
			
			// Axe
			switch(BedwarsTools.Axe.getUpgrade(this.bedwarsGame.getBedwarsPlayer(player).getAxe())) {
			case WOOD:
				this.inv.setItem(invSlot, this.createSpecialShopItem("SPECIAL_WOODEN_AXE", 1, "Buy Wood Axe (10 iron)", 10, "IRON_INGOT"));
				break;
			case IRON:
				this.inv.setItem(invSlot, this.createSpecialShopItem("SPECIAL_IRON_AXE", 1, "Buy Iron Axe (10 iron)", 10, "IRON_INGOT"));
				break;
			case GOLD:
				this.inv.setItem(invSlot, this.createSpecialShopItem("SPECIAL_GOLDEN_AXE", 1, "Buy Gold Axe (3 gold)", 3, "GOLD_INGOT"));
				break;
			case DIAMOND:
				this.inv.setItem(invSlot, this.createSpecialShopItem("SPECIAL_DIAMOND_AXE", 1, "Buy Diamond Axe (6 gold)", 6, "GOLD_INGOT"));
				break;
			}
		}
	}
}