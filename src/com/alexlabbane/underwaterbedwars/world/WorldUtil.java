package com.alexlabbane.underwaterbedwars.world;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Utility functions relating to the world (i.e. locations, dimensions, etc)
 * @author Alex Labbane
 *
 */
public class WorldUtil {
	
	/**
	 * Determine if two blocks are at the same location in the world
	 * @param b1	the first block
	 * @param b2	the second block
	 * @return		true if the blocks are at the same location
	 */
	public static boolean sameLocation(Block b1, Block b2) {
		if(approxEqual(b1.getLocation(), b2.getLocation())) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Determine if two locations have the same block x, y, z
	 * @param l1	the first location
	 * @param l2	the second location
	 * @return		true if the locations have the same block x, y, z coordinates
	 */
	public static boolean approxEqual(Location l1, Location l2) {
		if(l1.getBlockX() == l2.getBlockX()
				&& l1.getBlockY() == l2.getBlockY()
				&& l1.getBlockZ() == l2.getBlockZ())
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Get the squared distance between two players
	 * @param p1		the first player
	 * @param p2		the second player
	 * @return double	the squared distance between p1 and p2
	 */
	public static double getPlayerDistanceSquared(Player p1, Player p2) {
		return p1.getLocation().distanceSquared(p2.getLocation());
	}
	
	/**
	 * Get the distance between two players
	 * @param p1		the first player
	 * @param p2		the second player
	 * @return double	the distance between p1 and p2
	 */
	public static double getPlayerDistance(Player p1, Player p2) {
		return p1.getLocation().distance(p2.getLocation());
	}
}
