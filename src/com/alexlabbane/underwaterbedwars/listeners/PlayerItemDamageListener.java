package com.alexlabbane.underwaterbedwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.Listener;

/**
 * Stops player tools from being damaged
 * @author scien
 *
 */
public class PlayerItemDamageListener implements Listener {
	public PlayerItemDamageListener() { }
	
	@EventHandler
	public void PlayerItemDamageEvent(PlayerItemDamageEvent e) {
		e.setCancelled(true);
	}
}
