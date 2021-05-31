package com.alexlabbane.underwaterbedwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.Listener;

/**
 * Stops player tools from being damaged
 * @author Alex labbane
 *
 */
public class PlayerItemDamageListener implements Listener {
	
	/**
	 * Cancel item durability decreases
	 * @param e	the event being handled
	 */
	@EventHandler
	public void PlayerItemDamageEvent(PlayerItemDamageEvent e) {
		e.setCancelled(true);
	}
}
