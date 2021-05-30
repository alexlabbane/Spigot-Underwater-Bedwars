package com.alexlabbane.underwaterbedwars.util;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.minecraft.server.v1_16_R2.NBTTagCompound;

public class Util {
	public static Plugin plugin;
	public static final int TICKS_PER_SECOND = 20;
	
	public static void setPlugin(Plugin p) {
		plugin = p;
	}
	
	// String
	public static ItemStack addNBTTagString(ItemStack item, String tag, String data) {
		NamespacedKey key = new NamespacedKey(plugin, tag);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, data);
		item.setItemMeta(itemMeta);
		
		return item;
	}
	
	public static String getNBTTagString(ItemStack item, String tag) {
		NamespacedKey key = new NamespacedKey(plugin, tag);
		
		try {
			item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
		} catch (NullPointerException e) { return ""; }
		
		return item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
	}
	
	public static Entity addNBTTagString(Entity entity, String tag, String data) {
		NamespacedKey key = new NamespacedKey(plugin, tag);
		entity.getPersistentDataContainer().set(key, PersistentDataType.STRING, data);
		
		return entity;
	}
	
	public static String getNBTTagString(Entity entity, String tag) {
		NamespacedKey key = new NamespacedKey(plugin, tag);
		
		try {
			entity.getPersistentDataContainer().get(key, PersistentDataType.STRING);
		} catch (NullPointerException e) { return ""; }
		
		return entity.getPersistentDataContainer().get(key, PersistentDataType.STRING);
	}
	
	// Boolean (0 or 1 int)
	public static ItemStack addNBTTagBoolean(ItemStack item, String tag, int data) {
		NamespacedKey key = new NamespacedKey(plugin, tag);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, data);
		item.setItemMeta(itemMeta);
		
		return item;
	}
	
	public static int getNBTTagBoolean(ItemStack item, String tag) {
		NamespacedKey key = new NamespacedKey(plugin, tag);
		try {
			item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
		} catch (NullPointerException e) { return 0; }
		
		return item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
	}
	
	// Long
	public static ItemStack addNBTTagLong(ItemStack item, String tag, long data) {
		NamespacedKey key = new NamespacedKey(plugin, tag);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.getPersistentDataContainer().set(key, PersistentDataType.LONG, data);
		item.setItemMeta(itemMeta);
		
		return item;
	}
	
	public static long getNBTTagLong(ItemStack item, String tag) {
		NamespacedKey key = new NamespacedKey(plugin, tag);
		try {
			return item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.LONG);
		} catch (NullPointerException e) { return 0; }
		
	}
	
	public static void freezeEntity(Entity en){
	    net.minecraft.server.v1_16_R2.Entity nmsEn = ((CraftEntity) en).getHandle();
	    NBTTagCompound compound = new NBTTagCompound();
	    nmsEn.d(compound);
	    compound.setByte("NoAI", (byte) 1);
	    nmsEn.load(compound);
	}
	  
	public static String getCommonPotionName(PotionEffectType pet) {
		switch(pet.getName()) {
		case "SPEED":
			return "Speed";
		case "INVISIBILITY":
			return "Invisibility";
		case "JUMP":
			return "Jump";
		}
		
		return "Undefined";
	}
	
	/**
	 * Counts the number of dropped items matching mat
	 * in a radius r around the location
	 * @param loc
	 * @param mat
	 * @param r
	 * @return
	 */
	public static int countDroppedItems(Location loc, Material mat, float r) {
		Entity[] chunkEntities = loc.getChunk().getEntities();
		
		int count = 0;
		
		for(Entity en : chunkEntities) {
			if(en instanceof Item) {
				Item itm = (Item)en;
				
				if(itm.getItemStack().getType() == mat) count += itm.getItemStack().getAmount();
			}
		}
		
		return count;
	}

	public static void spawnFirework(Player player) {
		Location loc = player.getLocation();
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
       
        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.RED).flicker(true).build());
       
        fw.setFireworkMeta(fwm);
        
        Random rand = new Random();
        int duration = 20 + (rand.nextInt() % 40);
        
        new BukkitRunnable() {
        	@Override
        	public void run() {
                fw.detonate();		
        	}
        }.runTaskLater(Util.plugin, duration);
	}
}
