package com.alexlabbane.underwaterbedwars.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Class to encompass enums for all of the upgradeable tools in Bedwars
 * This includes the pickaxe, axe, and shears
 * @author scien
 */
public class BedwarsTools {
	
	/**
	 * Enum to define what level of pickaxe a player should have
	 * getUpgrade() can be called when a player buys an upgraded one from the shop
	 * getDowngrade() can be called when a player dies (and loses one level of upgrade)
	 * @author scien
	 */
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
		
		/**
		 * Get the next level of pickaxe that can be purchased
		 * No level above diamond can be bought
		 * @param pickaxe
		 * @return upgraded pickaxe, same as pickaxe param if no upgrade occurs
		 */
		public static Pickaxe getUpgrade(BedwarsTools.Pickaxe pickaxe) {
			switch(pickaxe) {
			case NONE:
				return WOOD;
			case WOOD:
				return IRON;
			case IRON:
				return GOLD;
			case GOLD:
				return DIAMOND;
			case DIAMOND:
				return DIAMOND;
			}
			
			// Default; should be impossible
			return NONE;
		}
		
		/**
		 * Get the level of pickaxe one tier below pickaxe parameter
		 * Once wood is bought, will always keep at least wood level
		 * @param pickaxe
		 * @return dowgraded pickaxe, same as pickaxe param if no downgrade occurs
		 */
		public static Pickaxe getDowngrade(BedwarsTools.Pickaxe pickaxe) {
			switch(pickaxe) {
			case WOOD:
				return WOOD;
			case IRON:
				return WOOD;
			case GOLD:
				return IRON;
			case DIAMOND:
				return GOLD;
			case NONE:
				return NONE;
			}
			
			// Default; should be impossible
			return NONE;
		}
		
		/**
		 * Get the name of the material for each level of pickaxe
		 * @param pickaxe
		 * @return String the name of the material
		 */
		public static String getMaterialName(BedwarsTools.Pickaxe pickaxe) {
			switch(pickaxe) {
			case WOOD:
				return "WOODEN_PICKAXE";
			case IRON:
				return "IRON_PICKAXE";
			case GOLD:
				return "GOLDEN_PICKAXE";
			case DIAMOND:
				return "DIAMOND_PICKAXE";
			}
			
			return null;
		}
		
		/**
		 * Get ItemStack for the specified pickaxe level
		 * Adds enchantments as well
		 * @param pickaxe
		 * @return ItemStack the bedwars pickaxe associated with pickaxe param
		 */
		public static ItemStack getBedwarsTool(BedwarsTools.Pickaxe pickaxe) {
			ItemStack newPickaxe = null;
			if(pickaxe != BedwarsTools.Pickaxe.NONE)
				newPickaxe = new ItemStack(Material.getMaterial(BedwarsTools.Pickaxe.getMaterialName(pickaxe)));

			switch(pickaxe) {
			case WOOD:
				newPickaxe.addEnchantment(Enchantment.DIG_SPEED, 1);
				break;
			case IRON:
				newPickaxe.addEnchantment(Enchantment.DIG_SPEED, 2);
				break;
			case GOLD:
				newPickaxe.addEnchantment(Enchantment.DIG_SPEED, 3);
				newPickaxe.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
				break;
			case DIAMOND:
				newPickaxe.addEnchantment(Enchantment.DIG_SPEED, 3);
				break;
			}
			
			return newPickaxe;
		}
	}
	
	/**
	 * Enum to define what level of axe a player should have
	 * getUpgrade() can be called when a player buys an upgraded one from the shop
	 * getDowngrade() can be called when a player dies (and loses one level of upgrade)
	 * @author scien
	 */
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
		
		/**
		 * Get the next level of axe that can be purchased
		 * No level above diamond can be bought
		 * @param axe
		 * @return upgraded axe, same as axe parameter if no upgrade occurs
		 */
		public static Axe getUpgrade(BedwarsTools.Axe axe) {
			switch(axe) {
			case NONE:
				return WOOD;
			case WOOD:
				return IRON;
			case IRON:
				return GOLD;
			case GOLD:
				return DIAMOND;
			case DIAMOND:
				return DIAMOND;
			}
			
			// Default; should be impossible
			return NONE;
		}
		
		/**
		 * Get the level of axe one tier below axe parameter
		 * Once wood is bought, will always keep at least wood level
		 * @param axe
		 * @return dowgraded axe, same as axe parameter is no downgrade occurs
		 */
		public static Axe getDowngrade(BedwarsTools.Axe axe) {
			switch(axe) {
			case WOOD:
				return WOOD;
			case IRON:
				return WOOD;
			case GOLD:
				return IRON;
			case DIAMOND:
				return GOLD;
			case NONE:
				return NONE;
			}
			
			// Default; should be impossible
			return NONE;
		}
		
		/**
		 * Get the name of the material for each level of axe
		 * @param axe
		 * @return String the name of the material
		 */
		public static String getMaterialName(BedwarsTools.Axe axe) {
			switch(axe) {
			case WOOD:
				return "WOODEN_AXE";
			case IRON:
				return "IRON_AXE";
			case GOLD:
				return "GOLDEN_AXE";
			case DIAMOND:
				return "DIAMOND_AXE";
			}
			
			return null;
		}
		
		/**
		 * Get ItemStack for the specified axe level
		 * Adds enchantments as well
		 * @param axe
		 * @return ItemStack the bedwars axe associated with axe param
		 */
		public static ItemStack getBedwarsTool(BedwarsTools.Axe axe) {
			ItemStack newAxe = null;
			if(axe != BedwarsTools.Axe.NONE)
				newAxe = new ItemStack(Material.getMaterial(BedwarsTools.Axe.getMaterialName(axe)));

			switch(axe) {
			case WOOD:
				newAxe.addEnchantment(Enchantment.DIG_SPEED, 1);
				break;
			case IRON:
				newAxe.addEnchantment(Enchantment.DIG_SPEED, 2);
				break;
			case GOLD:
				newAxe.addEnchantment(Enchantment.DIG_SPEED, 3);
				newAxe.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
				break;
			case DIAMOND:
				newAxe.addEnchantment(Enchantment.DIG_SPEED, 3);
				break;
			}
			
			return newAxe;
		}
	}
	
	/**
	 * Enum to define if a player has shears
	 * @author scien
	 */
	public enum Shears {
		NONE (0),
		SHEARS (1);
		private final int level;
		
		private Shears(int level) {
			this.level = level;
		}
		
		public int getLevel() { return this.level; }
		
		/**
		 * Get the next level of shears that can be purchased
		 * Only one level of shears to buy
		 * @param shears
		 * @return upgraded shears, same as shears parameter if no upgrade occurs
		 */
		public static Shears getUpgrade(BedwarsTools.Shears shears) {
			switch(shears) {
			case NONE:
				return SHEARS;
			case SHEARS:
				return SHEARS;
			}
			
			// Default; should be impossible
			return NONE;
		}
		
		/**
		 * Get the name of the material for each level of shears
		 * @param shears
		 * @return String the name of the material
		 */
		public static String getMaterialName(BedwarsTools.Shears shears) {
			return "SHEARS";
		}
		
		/**
		 * Get ItemStack for the specified shears level
		 * Adds enchantments as well
		 * @param shears
		 * @return ItemStack the bedwars shears associated with shears param
		 */
		public static ItemStack getBedwarsTool(BedwarsTools.Shears shears) {
			ItemStack newShears = null;
			if(shears != BedwarsTools.Shears.NONE)
				newShears = new ItemStack(Material.getMaterial(BedwarsTools.Shears.getMaterialName(shears)));
			
			return newShears;
		}
	}
}
