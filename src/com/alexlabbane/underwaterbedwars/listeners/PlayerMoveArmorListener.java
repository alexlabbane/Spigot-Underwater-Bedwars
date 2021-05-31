package com.alexlabbane.underwaterbedwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

/**
 * Listener to disallow players from removing/replacing their armor
 * @author Alex Labbane
 *
 */
public class PlayerMoveArmorListener implements Listener {
	
	/**
	 * Do not let players remove armor
	 * @param e	the event being handled
	 */
	@EventHandler
	public void inventoryClickEvent(InventoryClickEvent e) {
		if(e.getSlotType() == InventoryType.SlotType.ARMOR)
			e.setCancelled(true);
	}
}
