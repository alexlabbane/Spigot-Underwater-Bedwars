package com.alexlabbane.underwaterbedwars.util;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import net.minecraft.server.v1_16_R2.NBTTagCompound;

public class Util {
	static Plugin plugin;
	
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
}
