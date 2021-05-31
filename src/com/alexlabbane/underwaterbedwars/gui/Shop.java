package com.alexlabbane.underwaterbedwars.gui;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.alexlabbane.underwaterbedwars.BedwarsGame;
import com.alexlabbane.underwaterbedwars.util.LeveledEnchantment;
import com.alexlabbane.underwaterbedwars.util.Util;

/**
 * Abstract class used to define a shop GUI. Defines all minimum requirements required
 * to be a shop.
 * @author Alex Labbane
 *
 */
public abstract class Shop implements Listener {
	protected final BedwarsGame bedwarsGame;
	protected final Inventory inv;
	protected final String title;
	
	/**
	 * Create a new shop
	 * @param shopSize	the number of spaces in the shop GUI
	 * @param game		reference to the game the shop is a part of
	 */
	public Shop(int shopSize, BedwarsGame game) {
		this.bedwarsGame = game;
		this.title = "Item Shop";
		this.inv = Bukkit.createInventory(null, shopSize, this.title);
    	Bukkit.getServer().getPluginManager().registerEvents(this, this.bedwarsGame.getPlugin());
	}
	
	/**
	 * Create a new shop with a custom title
	 * @param shopSize		the number of spaces in the shop GUI
	 * @param game			reference to the game the shop is a part of
	 * @param shopTitle		custom title for the shop
	 */
	public Shop(int shopSize, BedwarsGame game, String shopTitle) {
		this.bedwarsGame = game;
		this.inv = Bukkit.createInventory(null, shopSize, shopTitle);
		this.title = shopTitle;
    	Bukkit.getServer().getPluginManager().registerEvents(this, this.bedwarsGame.getPlugin());
	}
	
	/************* Getters/Setters *************/

	public BedwarsGame getGame() { return this.bedwarsGame; }
	public String getTitle() { return this.title; }
	
	/************* Abstract methods *************/
	
	public abstract void initializeItems(Player player);
	public abstract void handleTransaction(Player player, String shopCostString);
	public abstract void handleLink(Player player, String shopLinkString);
	
	/**
	 * Create an item to place in the shop
	 * This creates the item that appears in the actual shop inventory
	 * For special kinds of purchases (i.e. armor upgrades), a material name
	 * prefixed with "SPECIAL_" can be used with {@link #createSpecialShopItem(String, int, String, int, String)}
	 * @param matName		the name of the material to be displayed in the shop GUI
	 * @param amount		the amount of the material to be displayed in the shop GUI
	 * @param name			the name of the item to be displayed in the shop GUI
	 * @param payAmount		the amount of the pay material to be required
	 * @param payMatName	the material used to pay for the item
	 * @return 				the item to show in the shop
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
	 * @param matName		the name of the material to be displayed in the shop GUI
	 * @param amount		the amount of the material to be displayed in the shop GUI
	 * @param name			the name of the item to be displayed in the shop GUI
	 * @param payAmount		the amount of the pay material to be required
	 * @param payMatName	the material used to pay for the item
	 * @return 				the item to show in the shop
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
	 * Create a purchasable potion to be placed in the shop
	 * @param potionEffectName	the name of the potion effect
	 * @param level				the level of potion effect to be applied
	 * @param durationTicks		the number of ticks the potion should be active for
	 * @param name				the name of the item to be displayed in the shop GUI
	 * @param payAmount			the amount of the pay material to be required
	 * @param payMatName		the material used to pay for the item
	 * @return					the item to be shown in the shop
	 */
	protected ItemStack createPotionShopItem(final String potionEffectName, int level, int durationTicks, final String name, int payAmount, final String payMatName) { 
		final ItemStack item = new ItemStack(Material.POTION);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		((PotionMeta)meta).addCustomEffect(new PotionEffect(PotionEffectType.getByName(potionEffectName), durationTicks, level), true);
		item.setItemMeta(meta);
		
		// ",0," used to make sure ShopItem class knows there are no enchants
		String shopCostString = "POTION_" + potionEffectName + "," + 1 + "," + payMatName + "," + payAmount + ",0," + level + "," + durationTicks;
		
		Bukkit.getServer().getLogger().log(Level.WARNING, shopCostString);
		Util.addNBTTagString(item, "ShopCost", shopCostString); // Add string of meta info to the item
		
		return item;
	}
	
	/**
	 * Create an enchanted item to place in the shop
	 * This creates the item that appears in the actual shop inventory
	 * For special kinds of purchases (i.e. armor upgrades), a material name
	 * prefixed with "SPECIAL_" can be used
	 * @param matName		the name of the material to be displayed in the shop GUI
	 * @param amount		the amount of the material to be displayed in the shop GUI
	 * @param name			the name of the item to be displayed in the shop GUI
	 * @param payAmount		the amount of the pay material to be required
	 * @param payMatName	the material used to pay for the item
	 * @param enchantments	the list of enchantments to be applied to the item
	 * @return 				the item to show in the shop
	 */
	protected ItemStack createEnchantedShopItem(final String matName, int amount, final String name, int payAmount, final String payMatName, LeveledEnchantment[] enchantments) {
		final ItemStack item = new ItemStack(Material.getMaterial(matName), amount);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		
		// String that stores purchase information for the item
		String shopCostString = matName + "," + amount + "," + payMatName + "," + payAmount + "," + enchantments.length;
		for(LeveledEnchantment le : enchantments) {
			item.addUnsafeEnchantment(le.getEnchantment(), le.getLevel());
			shopCostString += "," + le.getEnchantment().getName() + "," + le.getLevel();
		}
		
		Bukkit.getServer().getLogger().log(Level.WARNING, shopCostString);
		Util.addNBTTagString(item, "ShopCost", shopCostString); // Add string of meta info to the item
		
		return item;
	}
	
	/**
	 * Creates a link to another shop
	 * When this item is clicked on, it should open a new shop GUI for the linked shop
	 * Shop to link to denoted by shopName
	 * @param matName		the name of the material to be displayed in the shop GUI
	 * @param amount		the amount of the material to be displayed in the shop GUI
	 * @param name 			the display name of the link
	 * @param shopName		the name of the shop to link to as used by {@link #handleLink(Player, String)}
	 * @param color			the color of shop that the linked shop should create
	 * @return 				the item to show in the shop
	 */
	protected ItemStack createShopLink(final String matName, int amount, final String name,  final String shopName, final String color) {
		final ItemStack item = new ItemStack(Material.getMaterial(matName));
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		
		// String that stores link information for the item
		String shopLinkString = matName + "," + amount + "," + name + "," + shopName + "," + color;
		Util.addNBTTagString(item, "ShopLink", shopLinkString);
		
		return item;
	}
	
	/**
	 * Cancel any attempt to move items in the shop GUI
	 * @param e	the event being handled
	 */
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		if(e.getInventory() == inv)
			e.setCancelled(true);
	}
	
	/**
	 * When a shop item is clicked on, make sure it is not picked up.
	 * Handle as a transaction or link as applicable.
	 * @param e	the event being handled
	 */
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
			if(shopCostString != null && !shopCostString.equals("") && this.bedwarsGame.hasTeam(player)) {
				this.handleTransaction(player, shopCostString);
			}
			
			String shopLinkString = Util.getNBTTagString(item, "ShopLink");
			if(shopLinkString != null && !shopLinkString.equals("") && this.bedwarsGame.hasTeam(player)) {
				this.handleLink(player, shopLinkString);
			}
		}
		
	}
	
	/**
	 * Open the shop GUI for the player
	 * @param ent	the human entity to open the inventory for (i.e. player who clicked on the shop)
	 */
	public void openInventory(final HumanEntity ent) {
		ent.openInventory(this.inv);
	}	
}
