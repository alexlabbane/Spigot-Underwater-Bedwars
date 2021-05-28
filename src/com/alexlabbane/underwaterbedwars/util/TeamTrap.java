package com.alexlabbane.underwaterbedwars.util;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.alexlabbane.underwaterbedwars.BedwarsBed;
import com.alexlabbane.underwaterbedwars.BedwarsPlayer;
import com.alexlabbane.underwaterbedwars.BedwarsTeam;

import net.md_5.bungee.api.ChatColor;

public enum TeamTrap {
	BLINDNESS_SLOWNESS,
	COUNTER_OFFENSIVE,
	ALARM,
	MINER_FATIGUE;
	
	private static final int SLOWNESS_LEVEL = 1;
	private static final int SLOWNESS_DURATION = 20 * 8;
	
	private static final int BLINDNESS_LEVEL = 0;
	private static final int BLINDNESS_DURATION = 20 * 8;
	
	private static final int COUNTER_OFFENSIVE_RADIUS_SQUARED = 10 * 10;
	private static final int COUNTER_OFFENSIVE_DURATION = 20 * 10;
	private static final int COUNTER_OFFENSIVE_JUMP_LEVEL = 1;
	private static final int COUNTER_OFFENSIVE_SPEED_LEVEL = 0;
	
	private static final int MINER_FATIGUE_LEVEL = 0;
	private static final int MINER_FATIGUE_DURATION = 20 * 10;
	
	public static TeamTrap getByName(String name) {
		switch(name) {
		case "BLINDNESS_SLOWNESS":
			return BLINDNESS_SLOWNESS;
		case "COUNTER_OFFENSIVE":
			return COUNTER_OFFENSIVE;
		case "ALARM":
			return ALARM;
		case "MINER_FATIGUE":
			return MINER_FATIGUE;
		}
		
		return null;
	}
	
	public Material getDisplayMaterial() {
		switch(this) {
		case BLINDNESS_SLOWNESS:
			return Material.getMaterial("TRIPWIRE_HOOK");
		case COUNTER_OFFENSIVE:
			return Material.getMaterial("FEATHER");
		case ALARM:
			return Material.getMaterial("REDSTONE_TORCH");
		case MINER_FATIGUE:
			return Material.getMaterial("IRON_PICKAXE");
		}
		
		return null;
	}
	
	public String getName() {
		switch(this) {
		case BLINDNESS_SLOWNESS:
			return "It's a trap!";
		case COUNTER_OFFENSIVE:
			return "Counter-Offensive";
		case ALARM:
			return "Alarm Trap";
		case MINER_FATIGUE:
			return "Miner Fatigue";
		}
		
		return null;
	}
	
	/**
	 * Apply the trap effects to the relevant players
	 * Warn defenders that a trap has been triggered
	 * @param defenders
	 * @param offender
	 */
	public void apply(BedwarsTeam defenders, BedwarsPlayer offender) {
		for(BedwarsPlayer bwPlayer : defenders.getBedwarsPlayers()) {
			bwPlayer.getPlayer().sendTitle("", ChatColor.RED + this.getName() + " triggered!", 0, 20 * 3, 0);
			
			// Play the alarm noise
			new BukkitRunnable() {
				int count = 0;
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(count >= 16)
						this.cancel();
					
					float pitch = 1.8f;
					if(count % 2 == 0)
						pitch = 1.6f;
					
					bwPlayer.getPlayer().playSound(
							bwPlayer.getPlayer().getLocation(),
							Sound.BLOCK_NOTE_BLOCK_PLING,
							1,
							pitch);
					
					this.count++;
				}
				
			}.runTaskTimer(Util.plugin, 0, 2);
		}
		
		switch(this) {
		case BLINDNESS_SLOWNESS:
			offender.getPlayer().addPotionEffect(new PotionEffect(
					PotionEffectType.SLOW,
					TeamTrap.SLOWNESS_DURATION,
					TeamTrap.SLOWNESS_LEVEL));
			
			offender.getPlayer().addPotionEffect(new PotionEffect(
					PotionEffectType.BLINDNESS,
					TeamTrap.BLINDNESS_DURATION,
					TeamTrap.BLINDNESS_LEVEL));
			break;
		case COUNTER_OFFENSIVE:
			for(BedwarsPlayer bwPlayer : defenders.getBedwarsPlayers()) {
				Player p = bwPlayer.getPlayer();
				
				BedwarsBed teamBed = bwPlayer.getTeam().getBed();
				if(teamBed.getHeadLocation().distanceSquared(p.getLocation()) < TeamTrap.COUNTER_OFFENSIVE_RADIUS_SQUARED) {
					p.addPotionEffect(new PotionEffect(
							PotionEffectType.JUMP,
							TeamTrap.COUNTER_OFFENSIVE_DURATION,
							TeamTrap.COUNTER_OFFENSIVE_JUMP_LEVEL));
				
					p.addPotionEffect(new PotionEffect(
							PotionEffectType.SPEED,
							TeamTrap.COUNTER_OFFENSIVE_DURATION,
							TeamTrap.COUNTER_OFFENSIVE_SPEED_LEVEL));		
				}
			}
			break;
		case ALARM:
			offender.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
			break;
		case MINER_FATIGUE:
			offender.getPlayer().addPotionEffect(new PotionEffect(
					PotionEffectType.SLOW_DIGGING,
					TeamTrap.MINER_FATIGUE_DURATION,
					TeamTrap.MINER_FATIGUE_LEVEL));
			break;
		}
	}
}
