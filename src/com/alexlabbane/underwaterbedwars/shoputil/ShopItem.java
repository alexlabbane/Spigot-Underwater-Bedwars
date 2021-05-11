package com.alexlabbane.underwaterbedwars.shoputil;

import java.util.ArrayList;

import org.bukkit.enchantments.Enchantment;

import com.alexlabbane.underwaterbedwars.util.LeveledEnchantment;

public class ShopItem extends ShopWare {

	ArrayList<LeveledEnchantment> enchants;
	
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
	
	public ShopItem(String shopString, LeveledEnchantment enchant) {
		super(shopString);
		this.enchants = new ArrayList<LeveledEnchantment>();
		this.enchants.add(enchant);
	}
	
	public ShopItem(String shopString, LeveledEnchantment[] enchants) {
		super(shopString);
		this.enchants = new ArrayList<LeveledEnchantment>();
		for(LeveledEnchantment le : enchants)
			this.enchants.add(le);
	}
	
	public ArrayList<LeveledEnchantment> getEnchants() {
		return this.enchants.size() > 0 ? this.enchants : null;
	}
}
