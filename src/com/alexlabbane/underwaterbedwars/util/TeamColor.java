package com.alexlabbane.underwaterbedwars.util;

import org.bukkit.Color;

/**
 * Enum for team colors in a BedwarsGame
 * @author Alex Labbane
 *
 */
public enum TeamColor {
	RED ("RED"),
	BLUE ("BLUE"),
	GREEN ("GREEN"),
	YELLOW ("YELLOW"),
	AQUA ("CYAN"),
	CYAN ("CYAN"),
	WHITE ("WHITE"),
	PINK ("PINK"),
	GRAY ("GRAY");
	private final String colorString;
	
	/**
	 * Constructor
	 * @param colorString	the color of the team
	 */
	private TeamColor(String colorString) {
		this.colorString = colorString;
	}
	
	/************* Getters/Setters *************/
	
	public String getColor() { return this.colorString; }
	
	/**
	 * Get the actual color associated with the team color name
	 * @return	color of the team
	 */
	public Color RGB() {
		if(this.colorString.equals("RED"))
			return Color.fromRGB(11546150);
		else if(this.colorString.equals("BLUE"))
			return Color.fromRGB(3949738);
		else if(this.colorString.equals("GREEN"))
			return Color.fromRGB(6192150);
		else if(this.colorString.equals("YELLOW"))
			return Color.fromRGB(16701501);
		else if(this.colorString.equals("CYAN"))
			return Color.fromRGB(1481884);
		else if(this.colorString.equals("WHITE"))
			return Color.fromRGB(16383998);
		else if(this.colorString.equals("PINK"))
			return Color.fromRGB(15961002);
		else if(this.colorString.equals("GRAY"))
			return Color.fromRGB(4673362);
		else
			return Color.fromRGB(0, 0, 0);
		
	}
}
