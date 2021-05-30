package com.alexlabbane.underwaterbedwars;

import java.util.ArrayDeque;
import java.util.Queue;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.alexlabbane.underwaterbedwars.util.ChatMessages;
import com.alexlabbane.underwaterbedwars.util.TeamColor;
import com.alexlabbane.underwaterbedwars.util.TeamTrap;
import com.alexlabbane.underwaterbedwars.util.Util;

import net.md_5.bungee.api.ChatColor;

public class BedwarsBed implements Listener {
	private Location headLocation;
	private Location footLocation;
	private BlockFace facing;
	private TeamColor bedColor;
	private boolean broken;
	
	private BedwarsTeam team; // Team the bed belongs to
	private Queue<BedwarsPlayer> trappedPlayers;
	
	private static int TRAP_COOLDOWN = 20 * 30; // 30 second cooldown for traps (per player)
	private static int TRAP_RADIUS_SQUARED = 6 * 6; // trigger traps in 6 block radius 
	
	public BedwarsBed(String bedColor, BedwarsTeam team) {
		this(TeamColor.valueOf(bedColor), team);
	}
	
	public BedwarsBed(TeamColor bedColor, BedwarsTeam team) {
		FileConfiguration config = Util.plugin.getConfig();
		String lowerColor = bedColor.getColor().toLowerCase();
		
		this.team = team;
		this.trappedPlayers = new ArrayDeque<BedwarsPlayer>();
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
	
	public Location getHeadLocation() { return this.headLocation; }
	public Location getFootLocation() { return this.footLocation; }
	public boolean isBroken() { return this.broken; }
	
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
		Player p = e.getPlayer();
//		if(this.team.hasPlayer(p)) {
//			e.setCancelled(true);
//			return;
//		}
		
		if(e.getBlock().equals(this.headLocation.getBlock())
				|| e.getBlock().equals(this.footLocation.getBlock())) {
			
			// Delay setting bed as broken by 5 ticks
			new BukkitRunnable() {
				@Override
				public void run() {
					broken = true;
				}
			}.runTaskLater(Util.plugin, 5);
			
			// Update scoreboard for all players with new bed break
			UnderwaterBedwars.game.updateScoreboards();
			
			for(BedwarsTeam team : UnderwaterBedwars.game.getTeams()) {
				for(BedwarsPlayer bwPlayer : team.getBedwarsPlayers()) {
					Player player = bwPlayer.getPlayer();
					
					if(team == this.team) {
						player.sendTitle(
								"",
								ChatColor.RED + "Your bed has been destroyed by " + ChatMessages.getColoredChatMessage(e.getPlayer().getName()) + ChatColor.RED + "!",
								0,
								60,
								0);
						
						player.sendMessage(
								ChatColor.RED + "Your bed has been destroyed by " + 
								ChatMessages.getColoredChatMessage(e.getPlayer().getName()) + 
								ChatColor.RED + "!");
					} else {
						player.sendMessage(
								ChatColor.valueOf(bedColor.getColor()) + this.bedColor.getColor() + 
								ChatColor.RED + " bed was destroyed by " + 
								ChatMessages.getColoredChatMessage(e.getPlayer().getName()));
					}
					
					player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
				}
			}
			
			e.setDropItems(false);
		}	
	}
	
	@EventHandler
	public void onSleep(PlayerBedEnterEvent e) {
		if(e.getBed() == this.headLocation.getBlock()
			|| e.getBed() == this.footLocation.getBlock()) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerSetSpawn(PlayerBedLeaveEvent e) {
		if(e.getBed() == this.headLocation.getBlock()
				|| e.getBed() == this.footLocation.getBlock()) {
				e.setSpawnLocation(false);
			}
	}
	
	@EventHandler
	public void onPlayerEnterTrapRange(PlayerMoveEvent e) {
		// Check if bed is broken; make sure player within specified block radius
		if(this.broken || e.getPlayer().getLocation().distanceSquared(this.headLocation) > BedwarsBed.TRAP_RADIUS_SQUARED)
			return;
		
		BedwarsPlayer bwPlayer = UnderwaterBedwars.game.getBedwarsPlayer(e.getPlayer());
		
		// Check if player already has trap applied
		if(this.trappedPlayers.contains(bwPlayer))
			return;
		
		// TODO: Check if player has magic milk active
		
		if(bwPlayer != null && (true || bwPlayer.getTeam().getColor() != this.bedColor)) {
			// If player not on same team as bed, check if we need to trigger a trap!
			if(this.team != null && this.team.getQueuedTraps().size() > 0) {
				// There is a queued trap, so we apply it
				TeamTrap trap = this.team.getQueuedTraps().pop();
				trap.apply(this.team, bwPlayer);				
				
				// Add bwPlayer to queue of trapped players
				this.trappedPlayers.add(bwPlayer);
				
				// Remove the player from the queue after 30 seconds
				new BukkitRunnable() {
					@Override
					public void run() {
						trappedPlayers.remove();
					}
				}.runTaskLater(Util.plugin, BedwarsBed.TRAP_COOLDOWN);
			}
		}
	}
}
