package com.alexlabbane.underwaterbedwars.util;

import org.bukkit.enchantments.Enchantment;

public class LeveledEnchantment {
	private Enchantment enchant;
	private int level;
	
	public LeveledEnchantment(Enchantment e, int level) {
		this.enchant = e;
		this.level = level;
	}
	
	public int getLevel() { return this.level; }
	public Enchantment getEnchantment() { return this.enchant; }
	public void setLevel(int level) { this.level = level; }
	public void setEnchantment(Enchantment e) { this.enchant = e; }
}
