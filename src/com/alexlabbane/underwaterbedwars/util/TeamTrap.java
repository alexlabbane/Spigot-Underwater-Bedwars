package com.alexlabbane.underwaterbedwars.util;

import org.bukkit.Material;

public enum TeamTrap {
	BLINDNESS_SLOWNESS,
	COUNTER_OFFENSIVE,
	ALARM,
	MINER_FATIGUE;
	
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
}
