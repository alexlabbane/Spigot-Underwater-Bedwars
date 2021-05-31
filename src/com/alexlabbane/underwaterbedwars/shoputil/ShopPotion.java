package com.alexlabbane.underwaterbedwars.shoputil;

import org.bukkit.potion.PotionEffectType;

import com.alexlabbane.underwaterbedwars.util.Util;

/**
 * Represents a potion purchased from a shop
 * @author Alex Labbane
 *
 */
public class ShopPotion extends ShopWare {

	private String potionEffectName;
	private int level;
	private int durationTicks;
	
	/**
	 * Create a new ShopPotion
	 * @param shopString	the metadata string defining the purchased potion
	 */
	public ShopPotion(String shopString) {
		super(shopString);
		
		String[] splitString = shopString.split(",");
		this.matName = "POTION";
		this.potionEffectName = splitString[0].replace("POTION_", "");
		this.level = Integer.parseInt(splitString[5]);
		this.durationTicks = Integer.parseInt(splitString[6]);
	}
	
	/************* Getters/Setters *************/
	
	public String getPotionEffectName() { return this.potionEffectName; }
	public PotionEffectType getPotionEffect() { return PotionEffectType.getByName(this.potionEffectName); }
	public int getPotionEffectLevel() { return this.level; }
	public int getPotionEffectDuration() { return this.durationTicks; }
	
	/**
	 * Retrieve the name of the potion
	 * @return	the name of the potion
	 */
	@Override
	public String toString() {
		String str = "";
		str += Util.getCommonPotionName(this.getPotionEffect()) + " Potion";
		
		return str;
	}
}