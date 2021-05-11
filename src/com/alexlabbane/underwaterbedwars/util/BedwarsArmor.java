package com.alexlabbane.underwaterbedwars.util;

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
