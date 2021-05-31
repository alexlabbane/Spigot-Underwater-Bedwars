package com.alexlabbane.underwaterbedwars.listeners;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.alexlabbane.underwaterbedwars.BedwarsTeam;
import com.alexlabbane.underwaterbedwars.UnderwaterBedwars;

import net.md_5.bungee.api.ChatColor;

/**
 * Listener for block place/break events to make sure they are legal
 * @author Alex Labbane
 *
 */
public class BlockListener implements Listener {
	// Keep track of all placed blocks in HashSet
	private static HashSet<Block> placedBlocks = new HashSet<Block>();
	
	/**
	 * For blocks placed by an entity during the game session, allow them to be removed
	 * @param e	event being handled
	 */
	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent e) {
		placedBlocks.add(e.getBlock());
	}
	
	/**
	 * Cancel break events unless they are for a block placed by an entity
	 * @param e	event being handled
	 */
	@EventHandler
	public void onBreakBlock(BlockBreakEvent e) {
		if(!isBlockPlacedByPlayer(e.getBlock())) {
			
			// If the block is a bed, don't cancel the event
			for(BedwarsTeam team : UnderwaterBedwars.game.getTeams()) {
				if(team.getBed().isBroken())
					continue;
				
				Location blockLocation = e.getBlock().getLocation();
				Location headLocation = team.getBed().getHeadLocation();
				Location footLocation = team.getBed().getFootLocation();	

				if((
								headLocation.getBlockX() == blockLocation.getBlockX()
								&& headLocation.getBlockY() == blockLocation.getBlockY()
								&& headLocation.getBlockZ() == blockLocation.getBlockZ())
						|| (
								footLocation.getBlockX() == blockLocation.getBlockX()
								&& footLocation.getBlockY() == blockLocation.getBlockY()
								&& footLocation.getBlockZ() == blockLocation.getBlockZ())) {

					return; // Block is a bed, so don't cancel
				}
			}
			
			if(e.getPlayer() != null) {
				e.getPlayer().sendMessage(ChatColor.RED + "You can only break blocks placed by players!");
			}
			
			if(!UnderwaterBedwars.game.mapIsEditable()) {
				e.setCancelled(true);
			}
			
			return;
		}		
	}
	
	/**
	 * Set a block as placed by a player/not placed by a player
	 * @param block				the block to set
	 * @param placedByPlayer	whether or not it should be set as placed by a player
	 */
	public static void setBlockPlacedByPlayer(Block block, boolean placedByPlayer) {
		if(placedByPlayer) {
			placedBlocks.add(block);
		} else {
			placedBlocks.remove(block);
		}
	}
	
	/**
	 * Determine if a given block was placed by an entity or not
	 * @param b		the block to check
	 * @return		true if the block was placed by an entity
	 */
	public static boolean isBlockPlacedByPlayer(Block b) {
		return placedBlocks.contains(b);
	}
}
