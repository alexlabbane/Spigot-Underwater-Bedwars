package com.alexlabbane.underwaterbedwars.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.plugin.Plugin;

/**
 * Listener to prevent water from flowing
 * @author Alex Labbane
 *
 */
public class WaterListener implements Listener {
	@SuppressWarnings("unused")
	private Plugin plugin;
	private static boolean enabled = true;
	
	/**
	 * Create a new water listener
	 * @param p	referece to the plugin
	 */
	public WaterListener(Plugin p) {
		this.plugin = p;
	}
	
	/**
	 * Turn water flow on/off
	 */
	public static void toggle() {
		WaterListener.enabled = !WaterListener.enabled;
	}
	
	/**
	 * Cancel water flow if it is disabled
	 * @param e	the event being handled
	 */
	@EventHandler
	public void onBlockFromTo(BlockFromToEvent e) {
		Block b = e.getBlock();
		
		if(b.getType() == Material.WATER && WaterListener.enabled)
			e.setCancelled(true);
	}
}
