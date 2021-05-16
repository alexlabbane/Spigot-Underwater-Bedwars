package com.alexlabbane.underwaterbedwars.util;

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
}
