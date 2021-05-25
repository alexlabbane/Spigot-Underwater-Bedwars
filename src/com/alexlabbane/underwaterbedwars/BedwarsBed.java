package com.alexlabbane.underwaterbedwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.alexlabbane.underwaterbedwars.util.TeamColor;
import com.alexlabbane.underwaterbedwars.util.Util;

public class BedwarsBed implements Listener {
	private Location headLocation;
	private Location footLocation;
	private BlockFace facing;
	private TeamColor bedColor;
	private boolean broken;
	
	public BedwarsBed(String bedColor) {
		this(TeamColor.valueOf(bedColor));
	}
	
	public BedwarsBed(TeamColor bedColor) {
		FileConfiguration config = Util.plugin.getConfig();
		String lowerColor = bedColor.getColor().toLowerCase();
		
		this.headLocation = new Location(
				Bukkit.getServer().getWorlds().get(0), // Overworld
				config.getDouble(lowerColor + "-team.bed-location.x"),
				config.getDouble(lowerColor + "-team.bed-location.y"),
				config.getDouble(lowerColor + "-team.bed-location.z"));
		
		this.facing = BlockFace.valueOf(
				config.getString(lowerColor + "-team.bed-location.facing"));
		this.bedColor = bedColor;
		this.footLocation = null;
		this.set();
		
		Bukkit.getServer().getPluginManager().registerEvents(this, Util.plugin);
	}
	
	public void set() {
		this.broken = false;
		
		Block start = this.headLocation.getBlock();
		Material mat = Material.getMaterial(this.bedColor.getColor() + "_BED");
		
	    for (Bed.Part part : Bed.Part.values()) {
	        start.setBlockData(Bukkit.createBlockData(mat, (data) -> {
	           ((Bed) data).setPart(part);
	           ((Bed) data).setFacing(facing);
	        }));
	        
	        start = start.getRelative(facing.getOppositeFace());
	        
	        if(this.footLocation == null)
	        	this.footLocation = start.getLocation();
	    }
	}
	
	@EventHandler
	public void onBedBreak(BlockBreakEvent e) {
		if(e.getBlock().equals(this.headLocation.getBlock())
				|| e.getBlock().equals(this.footLocation.getBlock())) {
			this.broken = true;
			e.setDropItems(false);
		}	
	}
}
