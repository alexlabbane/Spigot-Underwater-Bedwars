package com.alexlabbane.underwaterbedwars.util;

import org.bukkit.enchantments.Enchantment;

/**
 * Represents an enchantment to be applied to an item
 * @author Alex Labbane
 *
 */
public class LeveledEnchantment {
	private Enchantment enchant;
	private int level;
	
	/**
	 * Create a new LeveledEnchantment
	 * @param e			the enchantment
	 * @param level		the level of enchantment
	 */
	public LeveledEnchantment(Enchantment e, int level) {
		this.enchant = e;
		this.level = level;
	}
	
	/************* Getters/Setters *************/
	
	public int getLevel() { return this.level; }
	public Enchantment getEnchantment() { return this.enchant; }
	public void setLevel(int level) { this.level = level; }
	public void setEnchantment(Enchantment e) { this.enchant = e; }
}
