package com.alexlabbane.underwaterbedwars.gui;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.alexlabbane.underwaterbedwars.BedwarsGame;
import com.alexlabbane.underwaterbedwars.util.LeveledEnchantment;
import com.alexlabbane.underwaterbedwars.util.Util;

public abstract class Shop implements Listener {
	protected final BedwarsGame bedwarsGame;
	protected final Inventory inv;
	
	public Shop(int shopSize, BedwarsGame game) {
		this.bedwarsGame = game;
		this.inv = Bukkit.createInventory(null, shopSize, "Item Shop");
	}
	
	public abstract void initializeItems();
	public abstract void handleTransaction(Player player, String shopCostString);
	
	/**
	 * Create an item to place in the shop
	 * This creates the item that appears in the actual shop inventory
	 * For special kinds of purchases (i.e. armor upgrades), a material name
	 * prefixed with "SPECIAL_" can be used
	 * @param matName
	 * @param amount
	 * @param name
	 * @param payAmount
	 * @param payMatName
	 * @return the item to show in the shop
	 */
	protected ItemStack createShopItem(final String matName, int amount, final String name, int payAmount, final String payMatName) {
		final ItemStack item = this.createEnchantedShopItem(matName, amount, name, payAmount, payMatName, new LeveledEnchantment[] {});
		
		return item;
	}
	
	/**
	 * Create a special item to place in the shop (i.e. armor upgrade)
	 * This creates the item that appears in the actual shop inventory
	 * For special kinds of purchases (i.e. armor upgrades), a material name
	 * prefixed with "SPECIAL_" can be used
	 * @param matName
	 * @param amount
	 * @param name
	 * @param payAmount
	 * @param payMatName
	 * @param enchantments
	 * @return the item to show in the shop
	 */
	protected ItemStack createSpecialShopItem(final String matName, int amount, final String name, int payAmount, final String payMatName) {
		String displayMatName = matName.replace("SPECIAL_", ""); // Remove "SPECIAL_" to generate display item
		
		final ItemStack item = new ItemStack(Material.getMaterial(displayMatName), amount);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		
		// String that stores purchase information for the item
		String shopCostString = matName + "," + amount + "," + payMatName + "," + payAmount + ",0"; // ",0 at end is for 0 enchantments
		
		Bukkit.getServer().getLogger().log(Level.WARNING, shopCostString);
		Util.addNBTTagString(item, "ShopCost", shopCostString); // Add string of meta info to the item
		
		return item;
	}
	
	/**
	 * Create an enchanted item to place in the shop
	 * This creates the item that appears in the actual shop inventory
	 * For special kinds of purchases (i.e. armor upgrades), a material name
	 * prefixed with "SPECIAL_" can be used
	 * @param matName
	 * @param amount
	 * @param name
	 * @param payAmount
	 * @param payMatName
	 * @param enchantments
	 * @return the item to show in the shop
	 */
	protected ItemStack createEnchantedShopItem(final String matName, int amount, final String name, int payAmount, final String payMatName, LeveledEnchantment[] enchantments) {
		final ItemStack item = new ItemStack(Material.getMaterial(matName), amount);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		
		// String that stores purchase information for the item
		String shopCostString = matName + "," + amount + "," + payMatName + "," + payAmount + "," + enchantments.length;
		for(LeveledEnchantment le : enchantments) {
			item.addEnchantment(le.getEnchantment(), le.getLevel());
			shopCostString += "," + le.getEnchantment().getName() + "," + le.getLevel();
		}
		
		Bukkit.getServer().getLogger().log(Level.WARNING, shopCostString);
		Util.addNBTTagString(item, "ShopCost", shopCostString); // Add string of meta info to the item
		
		return item;
	}
	
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		if(e.getInventory() == inv)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player player = null;
		if(e.getWhoClicked() instanceof Player)
			player = (Player)e.getWhoClicked();
		
		ItemStack item = e.getCurrentItem();
		if(item == null)
			return;
		
		if(e.getInventory() == inv) {
			e.setCancelled(true);
			
			String shopCostString = Util.getNBTTagString(item, "ShopCost");
			if(!shopCostString.equals("") && this.bedwarsGame.hasTeam(player)) {
				this.handleTransaction(player, shopCostString);
			}
			
			String shopLinkString = Util.getNBTTagString(item, "ShopLink");
			if(!shopLinkString.equals("") && this.bedwarsGame.hasTeam(player)) {
				// Handle a link to a different shop GUI
			}
		}
		
	}
	
	public void openInventory(final HumanEntity ent) {
		ent.openInventory(this.inv);
	}	
}
