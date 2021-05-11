package com.alexlabbane.underwaterbedwars.util;

/**
 * Enum to define current armor level of a Bedwars Player
 * @author scien
 *
 */
public enum BedwarsArmor {
	LEATHER (0),
	CHAIN (1),
	IRON (2),
	DIAMOND (3);
	private final int level;
	
	private BedwarsArmor(int level) {
		this.level = level;
	}
	
	public int getLevel() { return this.level; }
}
