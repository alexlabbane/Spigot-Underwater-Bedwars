package com.alexlabbane.underwaterbedwars.gui;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.alexlabbane.underwaterbedwars.BedwarsGame;
import com.alexlabbane.underwaterbedwars.BedwarsPlayer;
import com.alexlabbane.underwaterbedwars.shoputil.ShopItem;
import com.alexlabbane.underwaterbedwars.util.BedwarsArmor;
import com.alexlabbane.underwaterbedwars.util.LeveledEnchantment;

import net.md_5.bungee.api.ChatColor;

/**
 * Class for Quick Buy GUI (i.e. the main item shop)
 * Different pages will be represented by different classes
 * Items will be placed in the shop to "link" to different pages
 * 
 * Two different kinds of items will exist in the shop GUI
 * Shop Items are items that can be purchased (i.e. wool, armor upgrades, etc)
 * Link Items are items in the shop that link to another shop GUI (which is opened on click)
 * @author scien
 *
 */
public class ItemShop extends Shop implements Listener {
	private String color;
	
	public ItemShop(String color, BedwarsGame game) {
		super(54, game);
		this.color = color;
		this.initializeItems();
	}
	
	@Override
	public void initializeItems() {
		// Add colored wool to shop
		this.inv.setItem(19, this.createShopItem(this.color.toUpperCase() + "_WOOL", 16, "Buy Wool (4 iron)", 4, "IRON_INGOT"));
		this.inv.setItem(20, this.createEnchantedShopItem("TRIDENT", 1, "Buy Trident II (10 iron)", 10, "IRON_INGOT", new LeveledEnchantment[]{new LeveledEnchantment(Enchantment.LOYALTY, 2)}));
		this.inv.setItem(21, this.createSpecialShopItem("SPECIAL_CHAINMAIL_BOOTS", 1, "Buy Chain Armor (40 iron)", 40, "IRON_INGOT")); // TODO: Add rest of items (iron armor, tools, etc)

		this.inv.setItem(30, this.createSpecialShopItem("SPECIAL_IRON_BOOTS", 1, "Buy Iron Armor (12 gold)", 12, "GOLD_INGOT")); // TODO: Add rest of items (iron armor, tools, etc)
	}

	@Override
	public void handleTransaction(Player player, String shopCostString) {
		if(this.bedwarsGame.hasTeam(player)) {			
			ShopItem shopItem = new ShopItem(shopCostString);
			if(shopItem.canPlayerAfford(player)) {				
				if(shopItem.getMatName().startsWith("SPECIAL_")) {
					// Handle special drops
					handleSpecialTransaction(player, shopCostString);
				} else {
					// Handle normal drops
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
					shopItem.playerPay(player);
					
					ItemStack item = new ItemStack(shopItem.getMat(), shopItem.getAmount());
					if(shopItem.getEnchants() != null)
						for(LeveledEnchantment le : shopItem.getEnchants())
							item.addEnchantment(le.getEnchantment(), le.getLevel());
					
					
					HashMap<Integer, ItemStack> notAdded = player.getInventory().addItem(item);
					
					if(!notAdded.isEmpty())
						notAdded.values().forEach(itemStack -> player.getWorld().dropItemNaturally(player.getLocation(), itemStack));
					player.sendMessage(ChatColor.GREEN + "You bought " + shopItem.getAmount() + "x " + shopItem.getMatName() + " for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName() + ".");
				}				
			} else {
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
				player.sendMessage(ChatColor.RED + "You can't afford this item!");
			}
		}
		
	}
	
	/**
	 * Handles special transactions (i.e. armor purchases)
	 * Assumes player can already afford item
	 * @param player
	 * @param shopCostString
	 */
	private void handleSpecialTransaction(Player player, String shopCostString) {
		ShopItem shopItem = new ShopItem(shopCostString);
		BedwarsPlayer bwPlayer = this.bedwarsGame.getBedwarsPlayer(player);
		
		switch(shopItem.getMatName()) {
		case "SPECIAL_CHAINMAIL_BOOTS": // Chainmail armor
			if(bwPlayer.getArmor().getLevel() < BedwarsArmor.CHAIN.getLevel()) {
				// Upgrade is legal
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
				shopItem.playerPay(player);
				
				bwPlayer.setArmor(BedwarsArmor.CHAIN);
				bwPlayer.setPlayerArmor();
				player.sendMessage(ChatColor.GREEN + "You bought Chainmail Armor for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName() + ".");
			} else {
				player.sendMessage(ChatColor.RED + "You already have better armor!");
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
			break;
		case "SPECIAL_IRON_BOOTS": // Iron armor
			if(bwPlayer.getArmor().getLevel() < BedwarsArmor.IRON.getLevel()) {
				// Upgrade is legal
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
				shopItem.playerPay(player);
				
				bwPlayer.setArmor(BedwarsArmor.IRON);
				bwPlayer.setPlayerArmor();
				player.sendMessage(ChatColor.GREEN + "You bought Iron Armor for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName() + ".");
			} else {
				player.sendMessage(ChatColor.RED + "You already have better armor!");
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
			break;
		case "SPECIAL_DIAMOND_BOOTS": // Diamond armor
			if(bwPlayer.getArmor().getLevel() < BedwarsArmor.DIAMOND.getLevel()) {
				// Upgrade is legal
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
				shopItem.playerPay(player);
				
				bwPlayer.setArmor(BedwarsArmor.DIAMOND);
				bwPlayer.setPlayerArmor();
				player.sendMessage(ChatColor.GREEN + "You bought Diamond Armor for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName() + ".");
			} else {
				player.sendMessage(ChatColor.RED + "You already have better armor!");
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
			break;
		}
	}
}
