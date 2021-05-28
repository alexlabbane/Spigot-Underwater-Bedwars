package com.alexlabbane.underwaterbedwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.alexlabbane.underwaterbedwars.util.Util;

/**
 * Class to easily create resource generators (i.e. diamonds, emeralds)
 * @author scien
 *
 */
public class GameGen {
	private BukkitTask genTask; // Bukkit task that generates resources
	private Location genLocation; // Location of the gen
	private Material material; // Material to be produced
	private int delay; // Delay between generations (in ticks)
	private int maxStackSize; // Maximum number of items allowed nearby
	private int genLevel;
	private String genName;
	
	public static final float GEN_RADIUS = 1.5f;
	
	public GameGen(String genName) {
		this.genName = genName;
		this.configureGen();
	}
	
	public GameGen(Location loc, Material mat, int delay, int maxStackSize) {
		this.genLocation = loc;
		this.material = mat;
		this.delay = delay;
		this.maxStackSize = maxStackSize;
		this.genLevel = -1; // Gens instantiated this way cannot have level upgrades
		this.genTask = null;
		
		this.initialize();
	}
	
	public void setGenLevel(int level) {
		if(this.genName == null)
			return;
		
		this.genLevel = level;
		this.configureGen();
	}
	
	public void configureGen() {
		if(this.genName == null)
			return;
		
		FileConfiguration config = Util.plugin.getConfig();
		
		Location genLocation = new Location(
				Bukkit.getServer().getWorlds().get(0),
				config.getDouble("game.gens." + genName + ".x"),
				config.getDouble("game.gens." + genName + ".y"),
				config.getDouble("game.gens." + genName + ".z"));
		
		Material genMat = Material.getMaterial(config.getString(
				"game.gens." + genName + ".material"));
		
		int delay = config.getInt("game.gens." + genName + ".level-" + this.genLevel + "-delay-ticks");
		int maxStackSize = config.getInt("game.gens." + genName + ".max-stack-size"); 
		
		this.genLocation = genLocation;
		this.material = genMat;
		this.delay = delay;
		this.maxStackSize = maxStackSize;
		this.genLevel = 0;
		this.genTask = null;
		
		this.initialize();	
}
	
	public void initialize() {
		this.stopGen();
		
		this.genTask = new BukkitRunnable() {
			@Override
			public void run() {
				if(Util.countDroppedItems(genLocation, material, GameGen.GEN_RADIUS) >= maxStackSize)
					return;
				
				ItemStack item = new ItemStack(material);
				Item droppedItem = genLocation.getWorld().dropItem(genLocation, item);
				droppedItem.setVelocity(new Vector(0, 0, 0));
			}
		}.runTaskTimer(Util.plugin, this.delay, this.delay);
	}
	
	public int getDelay() { return this.delay; }
	public int getDelaySeconds() { return this.delay / Util.TICKS_PER_SECOND; }
	public void setDelay(int ticks) {
		this.delay = ticks;
		this.initialize();
	}
	
	public int getMaxStackSize() { return this.maxStackSize; }
	public void setMaxStackSize(int stackSize) { this.maxStackSize = stackSize; }
	
	
	public void stopGen() {
		if(this.genTask != null) {
			this.genTask.cancel();
		}
	}
}
