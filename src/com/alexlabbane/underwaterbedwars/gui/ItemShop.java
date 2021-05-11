package com.alexlabbane.underwaterbedwars.gui;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.alexlabbane.underwaterbedwars.BedwarsGame;
import com.alexlabbane.underwaterbedwars.shoputil.ShopItem;
import com.alexlabbane.underwaterbedwars.util.LeveledEnchantment;

import net.md_5.bungee.api.ChatColor;

public class ItemShop extends Shop implements Listener {
	private String color;
	
	public ItemShop(String color, BedwarsGame game) {
		super(54, game);
		this.color = color;
		this.initializeItems();
	}
	
	@Override
	public void initializeItems() {
		// Add colored wool to shop
		this.inv.setItem(19, this.createShopItem(this.color.toUpperCase() + "_WOOL", 16, "Buy Wool (4 iron)", 4, "IRON_INGOT"));
		this.inv.setItem(20, this.createEnchantedShopItem("TRIDENT", 1, "Buy Trident II (10 iron)", 10, "IRON_INGOT", new LeveledEnchantment[]{new LeveledEnchantment(Enchantment.LOYALTY, 2)}));
		//this.inv.setItem(21, ); // TODO: Add rest of items (iron armor, tools, etc)
	}

	@Override
	public void handleTransaction(Player player, String shopCostString) {
		if(this.bedwarsGame.hasTeam(player)) {			
			ShopItem shopItem = new ShopItem(shopCostString);
			if(shopItem.canPlayerAfford(player)) {
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
				shopItem.playerPay(player);
				
				ItemStack item = new ItemStack(shopItem.getMat(), shopItem.getAmount());
				if(shopItem.getEnchants() != null)
					for(LeveledEnchantment le : shopItem.getEnchants())
						item.addEnchantment(le.getEnchantment(), le.getLevel());
				
				
				HashMap<Integer, ItemStack> notAdded = player.getInventory().addItem(item);
				
				if(!notAdded.isEmpty())
					notAdded.values().forEach(itemStack -> player.getWorld().dropItemNaturally(player.getLocation(), itemStack));
				
				player.sendMessage(ChatColor.GREEN + "You bought " + shopItem.getAmount() + "x " + shopItem.getMatName() + " for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName() + ".");
			} else {
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
				player.sendMessage(ChatColor.RED + "You can't afford this item!");
			}
		}
		
	}
}
