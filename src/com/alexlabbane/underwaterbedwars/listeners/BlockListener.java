package com.alexlabbane.underwaterbedwars.listeners;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.alexlabbane.underwaterbedwars.BedwarsTeam;
import com.alexlabbane.underwaterbedwars.UnderwaterBedwars;

import net.md_5.bungee.api.ChatColor;

public class BlockListener implements Listener {
	// Keep track of all placed blocks in HashSet
	private static HashSet<Block> placedBlocks = new HashSet<Block>();
	
	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent e) {
		placedBlocks.add(e.getBlock());
	}
	
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
			
			e.setCancelled(true);
			return;
		}
	}
	
	public static boolean isBlockPlacedByPlayer(Block b) {
		return placedBlocks.contains(b);
	}
}
