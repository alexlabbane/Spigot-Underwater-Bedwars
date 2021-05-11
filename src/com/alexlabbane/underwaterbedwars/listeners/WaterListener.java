package com.alexlabbane.underwaterbedwars.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.plugin.Plugin;

public class WaterListener implements Listener {
	@SuppressWarnings("unused")
	private Plugin plugin;
	private static boolean enabled = true;
	
	public WaterListener(Plugin p) {
		this.plugin = p;
	}
	
	public static void toggle() {
		WaterListener.enabled = !WaterListener.enabled;
	}
	
	@EventHandler
	public void onBlockFromTo(BlockFromToEvent e) {
		Block b = e.getBlock();
		
		if(b.getType() == Material.WATER && WaterListener.enabled)
			e.setCancelled(true);
	}
}
