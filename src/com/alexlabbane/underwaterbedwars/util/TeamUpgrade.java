package com.alexlabbane.underwaterbedwars.util;

public enum TeamUpgrade {
	IMPALING,
	PROTECTION,
	HASTE,
	FORGE,
	HEAL_POOL;
	
	public static TeamUpgrade getByName(String name) {
		switch(name) {
		case "IMPALING":
			return TeamUpgrade.IMPALING;
		case "PROTECTION":
			return TeamUpgrade.PROTECTION;
		case "HASTE":
			return TeamUpgrade.HASTE;
		case "FORGE":
			return TeamUpgrade.FORGE;
		case "HEAL_POOL":
			return TeamUpgrade.HEAL_POOL;
		}
		
		return null;
	}	
}
