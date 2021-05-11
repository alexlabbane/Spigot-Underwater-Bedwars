package com.alexlabbane.underwaterbedwars.util;

public class BedwarsTools {
	public enum Pickaxe {
		NONE (0),
		WOOD (1),
		IRON (2),
		GOLD (3),
		DIAMOND (4);
		private final int level;
		
		private Pickaxe(int level) {
			this.level = level;
		}
		
		public int getLevel() { return this.level; }
	}
	
	public enum Axe {
		NONE (0),
		WOOD (1),
		IRON (2),
		GOLD (3),
		DIAMOND (4);
		private final int level;
		
		private Axe(int level) {
			this.level = level;
		}
		
		public int getLevel() { return this.level; }
	}
	
	public enum Shears {
		NONE (0),
		SHEARS (1);
		private final int level;
		
		private Shears(int level) {
			this.level = level;
		}
		
		public int getLevel() { return this.level; }
	}
}
