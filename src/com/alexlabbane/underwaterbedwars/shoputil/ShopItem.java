package com.alexlabbane.underwaterbedwars.shoputil;

import java.util.ArrayList;

import org.bukkit.enchantments.Enchantment;

import com.alexlabbane.underwaterbedwars.util.LeveledEnchantment;

/**
 * Represents a generic item purchased from a shop
 * @author Alex Labbane
 *
 */
public class ShopItem extends ShopWare {

	ArrayList<LeveledEnchantment> enchants;
	
	/**
	 * Create a new ShopItem
	 * @param shopString	the metadata string defining the purchased item
	 */
	public ShopItem(String shopString) {
		super(shopString);
		this.enchants = new ArrayList<LeveledEnchantment>();
		
		String[] splitString = shopString.split(",");
		for(int i = 5; i < 5 + 2 * Integer.parseInt(splitString[4]); i += 2) {
			Enchantment e = Enchantment.getByName(splitString[i]);
			int level = Integer.parseInt(splitString[i+1]);
			
			this.enchants.add(new LeveledEnchantment(e, level));
		}		
	}
	
	/**
	 * Create a new ShopItem
	 * @param shopString	the metadata string defining the purchased item
	 * @param enchant		enchantment to apply to the ShopItem
	 */
	public ShopItem(String shopString, LeveledEnchantment enchant) {
		super(shopString);
		this.enchants = new ArrayList<LeveledEnchantment>();
		this.enchants.add(enchant);
	}
	
	/**
	 * Create a new ShopItem
	 * @param shopString	the metadata string defining the purchased item
	 * @param enchants		list of enchants to apply
	 */
	public ShopItem(String shopString, LeveledEnchantment[] enchants) {
		super(shopString);
		this.enchants = new ArrayList<LeveledEnchantment>();
		for(LeveledEnchantment le : enchants)
			this.enchants.add(le);
	}
	
	/************* Getters/Setters *************/

	public ArrayList<LeveledEnchantment> getEnchants() {
		return this.enchants.size() > 0 ? this.enchants : null;
	}
}
