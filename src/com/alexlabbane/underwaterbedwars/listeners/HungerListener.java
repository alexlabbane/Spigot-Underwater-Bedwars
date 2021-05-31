package com.alexlabbane.underwaterbedwars.listeners;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import com.alexlabbane.underwaterbedwars.UnderwaterBedwars;

public class HungerListener implements Listener {
	@EventHandler
	public void onLoseHunger(FoodLevelChangeEvent event) {
		Player player = null;
		if(event.getEntity() instanceof Player) {
			player = (Player)event.getEntity();
		}
		
		if(UnderwaterBedwars.game.hasTeam(player)) {
			player.setFoodLevel(20); // Make sure player is already at max health
			event.setCancelled(true);
		}
		
		
		return;
	}
}
