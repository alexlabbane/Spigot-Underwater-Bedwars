package com.alexlabbane.underwaterbedwars.util;

/**
 * Enum used to define BedwarsTeam upgrades
 * @author Alex Labbane
 *
 */
public enum TeamUpgrade {
	IMPALING,
	PROTECTION,
	HASTE,
	FORGE,
	HEAL_POOL;
	
	/**
	 * Get a team upgrade by its name
	 * @param name	the name of the upgrade
	 * @return		the TeamUpgrade referenced by name
	 */
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
