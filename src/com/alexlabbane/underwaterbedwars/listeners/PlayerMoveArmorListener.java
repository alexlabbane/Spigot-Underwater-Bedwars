package com.alexlabbane.underwaterbedwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

/**
 * Listener to disallow players from removing/replacing their armor
 * @author scien
 *
 */
public class PlayerMoveArmorListener implements Listener {
	@EventHandler
	public void inventoryClickEvent(InventoryClickEvent e) {
		if(e.getSlotType() == InventoryType.SlotType.ARMOR)
			e.setCancelled(true);
	}
}
