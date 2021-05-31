package com.alexlabbane.underwaterbedwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.alexlabbane.underwaterbedwars.util.Util;

import net.md_5.bungee.api.ChatColor;

/**
 * Represents game resource generators (i.e. diamonds, emeralds)
 * @author Alex Labbane
 *
 */
public class GameGen {
	private String genName; // As specified in the configuration file

	private BukkitTask genTask; // Bukkit task that generates resources
	private Location genLocation; // Location of the gen
	private Material material; // Material to be produced
	private int delay; // Delay between generations (in ticks)
	private int maxStackSize; // Maximum number of items allowed nearby
	private int genLevel;
	
	private ArmorStand holoText; // Used to put holographic text above the gen
	
	/************* Static members *************/
	
	public static final float GEN_RADIUS = 1.5f;
	
	public GameGen(String genName) {
		this.genName = genName;		
		this.holoText = null;
		this.genTask = null;

		this.configureGen();
	}
	
	public GameGen(Location loc, Material mat, int delay, int maxStackSize) {
		this.genLocation = loc;
		this.material = mat;
		this.delay = delay;
		this.maxStackSize = maxStackSize;
		this.genLevel = -1; // Gens instantiated this way cannot have level upgrades
		this.genTask = null;
		
		this.holoText = (ArmorStand) this.genLocation.getWorld().spawnEntity(this.genLocation, EntityType.ARMOR_STAND);
		this.holoText.setCustomName(" ");
		this.holoText.setVisible(false);
		this.holoText.setCustomNameVisible(true);
		
		this.initialize();
	}
	
	/************* Getters/setters *************/
	
	public int getMaxStackSize() { return this.maxStackSize; }
	public void setMaxStackSize(int stackSize) { this.maxStackSize = stackSize; }
	
	public int getGenLevel() { return this.genLevel; }
	public void setGenLevel(int level) {
		if(this.genName == null)
			return;
		
		this.genLevel = level;
		this.configureGen();
	}
	
	public int getDelay() { return this.delay; }
	public int getDelaySeconds() { return this.delay / Util.TICKS_PER_SECOND; }
	public void setDelay(int ticks) {
		this.delay = ticks;
		this.initialize();
	}

	
	/**
	 * Get initial values of member variables from the config
	 */
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
		
		if(this.holoText == null)
			this.holoText = (ArmorStand) this.genLocation.getWorld().spawnEntity(this.genLocation, EntityType.ARMOR_STAND);
		
		this.holoText.setCustomName(" ");
		this.holoText.setVisible(false);
		this.holoText.setCustomNameVisible(true);
		
		this.initialize();	
	}
	
	/**
	 * Start the gen spawning resources
	 */
	public void initialize() {
		this.stopGen();		

		this.genTask = new BukkitRunnable() {
			int tickCounter = 0;

			@Override
			public void run() {
				tickCounter += Util.TICKS_PER_SECOND;
				int secondsLeft = (delay - tickCounter) / Util.TICKS_PER_SECOND;
				holoText.setCustomName(ChatColor.YELLOW + "Spawn in " + ChatColor.RED + secondsLeft + ChatColor.YELLOW + " seconds");
				
				if(tickCounter < delay) {
					// Stop here if the spawn timer hasn't reached zero yet
					return;
				}
				
				tickCounter = 0;
				
				if(Util.countDroppedItems(genLocation, material, GameGen.GEN_RADIUS) >= maxStackSize)
					return;
				
				ItemStack item = new ItemStack(material);
				Item droppedItem = genLocation.getWorld().dropItem(genLocation, item);
				droppedItem.setVelocity(new Vector(0, 0, 0));
			}
		}.runTaskTimer(Util.plugin, 0, Util.TICKS_PER_SECOND);
	}
	
	/**
	 * Stop the gen from spawning resources. Must be re-initialized
	 * to start spawning resources again using {@link #initialize()}
	 */
	public void stopGen() {
		if(this.genTask != null) {
			this.genTask.cancel();
		}
	}
}
