package com.alexlabbane.underwaterbedwars.util;

/**
 * Enum to define current armor level of a Bedwars Player
 * @author Alex Labbane
 *
 */
public enum BedwarsArmor {
	LEATHER (0),
	CHAIN (1),
	IRON (2),
	DIAMOND (3);
	private final int level;
	
	/**
	 * Constructor
	 * @param level	armor level
	 */
	private BedwarsArmor(int level) {
		this.level = level;
	}
	
	/************* Getters/Setters *************/
	
	public int getLevel() { return this.level; }
}
