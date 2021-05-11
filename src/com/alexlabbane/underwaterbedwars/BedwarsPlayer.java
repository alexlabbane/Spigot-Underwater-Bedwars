package com.alexlabbane.underwaterbedwars;

import org.bukkit.entity.Player;

import com.alexlabbane.underwaterbedwars.util.BedwarsArmor;
import com.alexlabbane.underwaterbedwars.util.BedwarsTools;

/**
 * Class to wrap Player objects with additional bedwars game state information
 * needed for individual players (such as tools upgrades, armor upgrades, etc).
 * Also provides various utility functions for player management
 * @author scien
 *
 */
public class BedwarsPlayer {
	private Player player;
	private BedwarsTeam team;
	
	private BedwarsArmor armor;
	private BedwarsTools.Axe axe;
	private BedwarsTools.Pickaxe pickaxe;
	private BedwarsTools.Shears shears;
	
	/**
	 * Construct a new Bedwars Player attached to player p
	 * Starts players with no tools and base armor
	 * @param p
	 */
	public BedwarsPlayer(Player p) {
		this.player = p;
		this.team = null;
		this.armor = BedwarsArmor.LEATHER;
		this.axe = BedwarsTools.Axe.NONE;
		this.pickaxe = BedwarsTools.Pickaxe.NONE;
		this.shears = BedwarsTools.Shears.NONE;
	}
	
	public Player getPlayer() { return this.player; }
	
	public BedwarsTeam getTeam() { return this.team; }
	public void setTeam(BedwarsTeam team) { this.team = team; }
	
	public BedwarsTools.Axe getAxe() { return this.axe; }
	public void setAxe(BedwarsTools.Axe axe) { this.axe = axe; }

	public BedwarsTools.Pickaxe getPickaxe() { return this.pickaxe; }
	public void setPickaxe(BedwarsTools.Pickaxe pickaxe) { this.pickaxe = pickaxe; }
	
	public BedwarsTools.Shears getShears() { return this.shears; }
	public void setShears(BedwarsTools.Shears shears) { this.shears = shears; }
	
	public BedwarsArmor getArmor() { return this.armor; }
	
	/**
	 * Sets the armor level of player to newArmor
	 * Allows armor downgrades (i.e. from iron to leather)
	 * @param newArmor
	 */
	public void setArmor(BedwarsArmor newArmor) {
		this.armor = newArmor;
	}
	
	/**
	 * Upgrade the pickaxe of a Bedwars Player
	 * Will fail to upgrade if player already has highest tier pickaxe
	 * @return true if upgrade occurred, false if upgrade failed
	 */
	public boolean upgradePickaxe() {
		if(this.pickaxe != BedwarsTools.Pickaxe.getUpgrade(this.pickaxe)) {
			this.pickaxe = BedwarsTools.Pickaxe.getUpgrade(this.pickaxe);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Downgrade the pickaxe of a Bedwars Player
	 * Will fail to downgrade if player has no pickaxe or lowest tier pickaxe
	 * @return true if downgrade occurred, false if downgrade failed
	 */
	public boolean downgradePickaxe() {
		if(this.pickaxe != BedwarsTools.Pickaxe.getDowngrade(this.pickaxe)) {
			this.pickaxe = BedwarsTools.Pickaxe.getDowngrade(this.pickaxe);
			return true;
		}
		
		return false;	
	}

	/**
	 * Upgrade the axe of a Bedwars Player
	 * Will fail to upgrade if player already has highest tier axe
	 * @return true if upgrade occurred, false if upgrade failed
	 */
	public boolean upgradeAxe() {
		if(this.axe != BedwarsTools.Axe.getUpgrade(this.axe)) {
			this.axe = BedwarsTools.Axe.getUpgrade(this.axe);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Downgrade the axe of a Bedwars Player
	 * Will fail to downgrade if player has no axe or lowest tier axe
	 * @return true if downgrade occurred, false if downgrade failed
	 */
	public boolean downgradeAxe() {
		if(this.axe != BedwarsTools.Axe.getDowngrade(this.axe)) {
			this.axe = BedwarsTools.Axe.getDowngrade(this.axe);
			return true;
		}
		
		return false;	
	}
	
	/**
	 * Upgrade the axe of a Bedwars Player
	 * Will fail to upgrade if player already has highest tier axe
	 * @return true if upgrade occurred, false if upgrade failed
	 */
	public boolean upgradeShears() {
		if(this.shears != BedwarsTools.Shears.getUpgrade(this.shears)) {
			this.shears = BedwarsTools.Shears.getUpgrade(this.shears);
			return true;
		}
		
		return false;
	}
}
