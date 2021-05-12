package com.alexlabbane.underwaterbedwars.shoputil;

import java.util.ArrayList;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import com.alexlabbane.underwaterbedwars.util.LeveledEnchantment;
import com.alexlabbane.underwaterbedwars.util.Util;

public class ShopPotion extends ShopWare {

	private String potionEffectName;
	private int level;
	private int durationTicks;
	
	public ShopPotion(String shopString) {
		super(shopString);
		
		String[] splitString = shopString.split(",");
		this.matName = "POTION";
		this.potionEffectName = splitString[0].replace("POTION_", "");
		this.level = Integer.parseInt(splitString[5]);
		this.durationTicks = Integer.parseInt(splitString[6]);
	}
	
	public String getPotionEffectName() { return this.potionEffectName; }
	public PotionEffectType getPotionEffect() { return PotionEffectType.getByName(this.potionEffectName); }
	public int getPotionEffectLevel() { return this.level; }
	public int getPotionEffectDuration() { return this.durationTicks; }
	
	@Override
	public String toString() {
		String str = "";
		str += Util.getCommonPotionName(this.getPotionEffect()) + " Potion";
		
		return str;
	}
}