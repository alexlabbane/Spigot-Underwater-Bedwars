package com.alexlabbane.underwaterbedwars.gui;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import com.alexlabbane.underwaterbedwars.BedwarsGame;
import com.alexlabbane.underwaterbedwars.BedwarsPlayer;
import com.alexlabbane.underwaterbedwars.shoputil.ShopItem;
import com.alexlabbane.underwaterbedwars.shoputil.ShopPotion;
import com.alexlabbane.underwaterbedwars.util.BedwarsArmor;
import com.alexlabbane.underwaterbedwars.util.BedwarsTools;
import com.alexlabbane.underwaterbedwars.util.LeveledEnchantment;

import net.md_5.bungee.api.ChatColor;

/**
 * Class for Quick Buy GUI (i.e. the main item shop)
 * Different pages will be represented by different classes (and will inherit from this class)
 * Items will be placed in the shop to "link" to different pages
 * 
 * Two different kinds of items will exist in the shop GUI
 * Shop Items are items that can be purchased (i.e. wool, armor upgrades, etc)
 * Link Items are items in the shop that link to another shop GUI (which is opened on click)
 * @author scien
 *
 */
public class ItemShop extends Shop implements Listener {
	protected String color;
	
	public ItemShop(String color, BedwarsGame game) {
		super(54, game, "Quick Buy");
		this.color = color;
		this.initializeItems(null);
	}
	
	public ItemShop(String color, BedwarsGame game, String title) {
		super(54, game, title);
		this.color = color;
		this.initializeItems(null);
	}
	
	// Copy constructor
	public ItemShop(String color, ItemShop itemShop) {
		super(54, itemShop.getGame(), itemShop.getTitle());
		this.color = color;
		this.initializeItems(null);
	}
	
	@Override
	public void initializeItems(Player player) {
		// Create links to other shop pages
		this.inv.setItem(0, this.createShopLink("NETHER_STAR", 1, "Quick Buy", "SHOP_QUICK_BUY", this.color));
		this.inv.setItem(1, this.createShopLink("TERRACOTTA", 1, "Blocks", "SHOP_BLOCKS", this.color));
		this.inv.setItem(2, this.createShopLink("GOLDEN_SWORD", 1, "Melee", "SHOP_MELEE", this.color));
		this.inv.setItem(3, this.createShopLink("CHAINMAIL_BOOTS", 1, "Armor", "SHOP_ARMOR", this.color));
		this.inv.setItem(4, this.createShopLink("STONE_PICKAXE", 1, "Tools", "SHOP_TOOLS", this.color));
		this.inv.setItem(5, this.createShopLink("BOW", 1, "Ranged", "SHOP_RANGED", this.color));
		this.inv.setItem(6, this.createShopLink("BREWING_STAND", 1, "Potions", "SHOP_POTIONS", this.color));
		this.inv.setItem(7, this.createShopLink("TNT", 1, "Utilities", "SHOP_UTILITIES", this.color));
		
		// Add items for purchase to shop
		this.inv.setItem(19, this.createShopItem(this.color.toUpperCase() + "_WOOL", 16, "Buy Wool (4 iron)", 4, "IRON_INGOT"));
		this.inv.setItem(20, this.createEnchantedShopItem("TRIDENT", 1, "Buy Trident II (10 iron)", 10, "IRON_INGOT", new LeveledEnchantment[]{new LeveledEnchantment(Enchantment.LOYALTY, 2)}));
		this.inv.setItem(21, this.createSpecialShopItem("SPECIAL_CHAINMAIL_BOOTS", 1, "Buy Chain Armor (40 iron)", 40, "IRON_INGOT")); // TODO: Add rest of items (iron armor, tools, etc)
		
		// Tools upgrades
		if(player != null) {
			// Pickaxe
			switch(BedwarsTools.Pickaxe.getUpgrade(this.bedwarsGame.getBedwarsPlayer(player).getPickaxe())) {
			case WOOD:
				this.inv.setItem(22, this.createSpecialShopItem("SPECIAL_WOODEN_PICKAXE", 1, "Buy Wood Pickaxe (10 iron)", 10, "IRON_INGOT"));
				break;
			case IRON:
				this.inv.setItem(22, this.createSpecialShopItem("SPECIAL_IRON_PICKAXE", 1, "Buy Iron Pickaxe (10 iron)", 10, "IRON_INGOT"));
				break;
			case GOLD:
				this.inv.setItem(22, this.createSpecialShopItem("SPECIAL_GOLDEN_PICKAXE", 1, "Buy Gold Pickaxe (3 gold)", 3, "GOLD_INGOT"));
				break;
			case DIAMOND:
				this.inv.setItem(22, this.createSpecialShopItem("SPECIAL_DIAMOND_PICKAXE", 1, "Buy Diamond Pickaxe (6 gold)", 6, "GOLD_INGOT"));
				break;
			}
		}

		this.inv.setItem(28, this.createShopItem("OAK_PLANKS", 16, "Buy Wood (4 gold)", 4, "GOLD_INGOT"));
		this.inv.setItem(29, this.createEnchantedShopItem("TRIDENT", 1, "Buy Trident III (7 gold)", 7, "GOLD_INGOT", new LeveledEnchantment[]{new LeveledEnchantment(Enchantment.LOYALTY, 3)}));
		this.inv.setItem(30, this.createSpecialShopItem("SPECIAL_IRON_BOOTS", 1, "Buy Iron Armor (12 gold)", 12, "GOLD_INGOT")); // TODO: Add rest of items (iron armor, tools, etc)
		this.inv.setItem(31, this.createSpecialShopItem("SPECIAL_SHEARS", 1, "Buy Shears (20 iron)", 20, "IRON_INGOT"));
		this.inv.setItem(32, this.createShopItem("ARROW", 8, "Buy Arrows (2 gold)", 2, "GOLD_INGOT"));
		this.inv.setItem(33, this.createPotionShopItem("SPEED", 1, 20 * 45, "Buy Speed Potion (1 emerald)", 1, "EMERALD"));
	}
	
	@Override
	public void handleLink(Player player, String shopLinkString) {
		if(this.bedwarsGame.hasTeam(player)) {
			// Open GUI of the other shop
			String[] shopLinkItems = shopLinkString.split(",");
			final String linkedShop = shopLinkItems[3];
			
			ItemShop newShop = null;
			switch(linkedShop) {
			case "SHOP_BLOCKS":
				newShop = new BlockShop(this.color, this.bedwarsGame);
				break;
			case "SHOP_QUICK_BUY":
				newShop = new ItemShop(this.color, this.bedwarsGame);
				break;
			case "SHOP_MELEE":
				newShop = new MeleeShop(this.color, this.bedwarsGame);
				break;
			case "SHOP_ARMOR":
				newShop = new ArmorShop(this.color, this.bedwarsGame);
				break;
			case "SHOP_TOOLS":
				newShop = new ToolShop(this.color, this.bedwarsGame);
				break;
			}
			
			newShop.initializeItems(player);
			if(newShop != null)
				newShop.openInventory((HumanEntity)player);
		}
	}
	
	@Override
	public void handleTransaction(Player player, String shopCostString) {
		if(this.bedwarsGame.hasTeam(player)) {			
			ShopItem shopItem = new ShopItem(shopCostString);
			if(shopItem.canPlayerAfford(player)) {				
				if(shopItem.getMatName().startsWith("SPECIAL_")) {
					// Handle special drops
					handleSpecialTransaction(player, shopCostString);
				} else if(shopItem.getMatName().startsWith("POTION_")) {
					// Handle potion drops
					handlePotionTransaction(player, shopCostString);
				} else {
					// Handle normal drops
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
					shopItem.playerPay(player);
					
					ItemStack item = new ItemStack(shopItem.getMat(), shopItem.getAmount());
					if(shopItem.getEnchants() != null)
						for(LeveledEnchantment le : shopItem.getEnchants())
							item.addUnsafeEnchantment(le.getEnchantment(), le.getLevel());
					
					
					HashMap<Integer, ItemStack> notAdded = player.getInventory().addItem(item);
					
					if(!notAdded.isEmpty())
						notAdded.values().forEach(itemStack -> player.getWorld().dropItemNaturally(player.getLocation(), itemStack));
					player.sendMessage(ChatColor.GREEN + "You bought " + shopItem.getAmount() + "x " + shopItem.getMatName() + " for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName() + ".");
				}				
			} else {
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
				player.sendMessage(ChatColor.RED + "You can't afford this item!");
			}
		}
		
		// Refresh the shop
		this.initializeItems(player);
		player.updateInventory();
	}
	
	/**
	 * Handles potion transactions
	 * Assumes player can already afford item
	 * Applies potion effect and uses ShopPotion class instead of ShopItem
	 * to parse shopCostString
	 * @param player
	 * @param shopCostString
	 */
	private void handlePotionTransaction(Player player, String shopCostString) {
		ShopPotion shopPotion = new ShopPotion(shopCostString);
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
		shopPotion.playerPay(player);
		
		ItemStack item = new ItemStack(shopPotion.getMat(), shopPotion.getAmount());
		PotionMeta meta = (PotionMeta) item.getItemMeta();
		meta.addCustomEffect(
				new PotionEffect(
						shopPotion.getPotionEffect(), 
						shopPotion.getPotionEffectDuration(), 
						shopPotion.getPotionEffectLevel()),
				true);
		meta.setDisplayName(shopPotion.toString());
		item.setItemMeta(meta);
		
		HashMap<Integer, ItemStack> notAdded = player.getInventory().addItem(item);
		
		if(!notAdded.isEmpty())
			notAdded.values().forEach(itemStack -> player.getWorld().dropItemNaturally(player.getLocation(), itemStack));
		player.sendMessage(ChatColor.GREEN + "You bought " + shopPotion.getAmount() + "x " + shopPotion.getMatName() + " for " + ChatColor.YELLOW + shopPotion.getPayAmount() + "x " + shopPotion.getPayMatName() + ".");
	}
	
	/**
	 * Handles special transactions (i.e. armor purchases)
	 * Assumes player can already afford item
	 * @param player
	 * @param shopCostString
	 */
	private void handleSpecialTransaction(Player player, String shopCostString) {
		ShopItem shopItem = new ShopItem(shopCostString);
		BedwarsPlayer bwPlayer = this.bedwarsGame.getBedwarsPlayer(player);
		
		switch(shopItem.getMatName()) {
		/******************** ARMOR ********************/
		case "SPECIAL_CHAINMAIL_BOOTS": // Chainmail armor
			if(bwPlayer.getArmor().getLevel() < BedwarsArmor.CHAIN.getLevel()) {
				// Upgrade is legal
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
				shopItem.playerPay(player);
				
				bwPlayer.setArmor(BedwarsArmor.CHAIN);
				bwPlayer.setPlayerArmor();
				player.sendMessage(ChatColor.GREEN + "You bought Chainmail Armor for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName() + ".");
			} else {
				player.sendMessage(ChatColor.RED + "You already have better armor!");
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
			break;
		case "SPECIAL_IRON_BOOTS": // Iron armor
			if(bwPlayer.getArmor().getLevel() < BedwarsArmor.IRON.getLevel()) {
				// Upgrade is legal
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
				shopItem.playerPay(player);
				
				bwPlayer.setArmor(BedwarsArmor.IRON);
				bwPlayer.setPlayerArmor();
				player.sendMessage(ChatColor.GREEN + "You bought Iron Armor for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName() + ".");
			} else {
				player.sendMessage(ChatColor.RED + "You already have better armor!");
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
			break;
		case "SPECIAL_DIAMOND_BOOTS": // Diamond armor
			if(bwPlayer.getArmor().getLevel() < BedwarsArmor.DIAMOND.getLevel()) {
				// Upgrade is legal
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
				shopItem.playerPay(player);
				
				bwPlayer.setArmor(BedwarsArmor.DIAMOND);
				bwPlayer.setPlayerArmor();
				player.sendMessage(ChatColor.GREEN + "You bought Diamond Armor for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName() + ".");
			} else {
				player.sendMessage(ChatColor.RED + "You already have better armor!");
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
			break;
			
		/******************** PICKAXES ********************/
		case "SPECIAL_WOODEN_PICKAXE":
			if(bwPlayer.getPickaxe().getLevel() < BedwarsTools.Pickaxe.WOOD.getLevel()) {
				// Upgrade is legal
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
				shopItem.playerPay(player);
				
				bwPlayer.setPickaxe(BedwarsTools.Pickaxe.WOOD);
				bwPlayer.setPlayerPickaxe();
				player.sendMessage(ChatColor.GREEN + "You bought a Wood Pickaxe for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName() + ".");
			} else {
				player.sendMessage(ChatColor.RED + "You already have a better pickaxe!");
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
			break;
		case "SPECIAL_IRON_PICKAXE":
			if(bwPlayer.getPickaxe().getLevel() < BedwarsTools.Pickaxe.IRON.getLevel()) {
				// Upgrade is legal
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
				shopItem.playerPay(player);
				
				bwPlayer.setPickaxe(BedwarsTools.Pickaxe.IRON);
				bwPlayer.setPlayerPickaxe();
				player.sendMessage(ChatColor.GREEN + "You bought a Iron Pickaxe for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName() + ".");
			} else {
				player.sendMessage(ChatColor.RED + "You already have a better pickaxe!");
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
			break;
		case "SPECIAL_GOLDEN_PICKAXE":
			if(bwPlayer.getPickaxe().getLevel() < BedwarsTools.Pickaxe.GOLD.getLevel()) {
				// Upgrade is legal
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
				shopItem.playerPay(player);
				
				bwPlayer.setPickaxe(BedwarsTools.Pickaxe.GOLD);
				bwPlayer.setPlayerPickaxe();
				player.sendMessage(ChatColor.GREEN + "You bought a Gold Pickaxe for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName() + ".");
			} else {
				player.sendMessage(ChatColor.RED + "You already have a better pickaxe!");
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
			break;
		case "SPECIAL_DIAMOND_PICKAXE":
			if(bwPlayer.getPickaxe().getLevel() < BedwarsTools.Pickaxe.DIAMOND.getLevel()) {
				// Upgrade is legal
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
				shopItem.playerPay(player);
				
				bwPlayer.setPickaxe(BedwarsTools.Pickaxe.DIAMOND);
				bwPlayer.setPlayerPickaxe();
				player.sendMessage(ChatColor.GREEN + "You bought a Diamond Pickaxe for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName() + ".");
			} else {
				player.sendMessage(ChatColor.RED + "You already have a better pickaxe!");
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
			break;	
			
		/******************** AXES ********************/
		case "SPECIAL_WOODEN_AXE":
			if(bwPlayer.getAxe().getLevel() < BedwarsTools.Axe.WOOD.getLevel()) {
				// Upgrade is legal
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
				shopItem.playerPay(player);
				
				bwPlayer.setAxe(BedwarsTools.Axe.WOOD);
				bwPlayer.setPlayerAxe();
				player.sendMessage(ChatColor.GREEN + "You bought a Wood Axe for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName() + ".");
			} else {
				player.sendMessage(ChatColor.RED + "You already have a better axe!");
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
			break;
		case "SPECIAL_IRON_AXE":
			if(bwPlayer.getAxe().getLevel() < BedwarsTools.Axe.IRON.getLevel()) {
				// Upgrade is legal
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
				shopItem.playerPay(player);
				
				bwPlayer.setAxe(BedwarsTools.Axe.IRON);
				bwPlayer.setPlayerAxe();
				player.sendMessage(ChatColor.GREEN + "You bought a Iron Axe for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName() + ".");
			} else {
				player.sendMessage(ChatColor.RED + "You already have a better axe!");
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
			break;
		case "SPECIAL_GOLDEN_AXE":
			if(bwPlayer.getAxe().getLevel() < BedwarsTools.Axe.GOLD.getLevel()) {
				// Upgrade is legal
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
				shopItem.playerPay(player);
				
				bwPlayer.setAxe(BedwarsTools.Axe.GOLD);
				bwPlayer.setPlayerAxe();
				player.sendMessage(ChatColor.GREEN + "You bought a Gold Axe for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName() + ".");
			} else {
				player.sendMessage(ChatColor.RED + "You already have a better axe!");
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
			break;
		case "SPECIAL_DIAMOND_AXE":
			if(bwPlayer.getAxe().getLevel() < BedwarsTools.Axe.DIAMOND.getLevel()) {
				// Upgrade is legal
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
				shopItem.playerPay(player);
				
				bwPlayer.setAxe(BedwarsTools.Axe.DIAMOND);
				bwPlayer.setPlayerAxe();
				player.sendMessage(ChatColor.GREEN + "You bought a Diamond Axe for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName() + ".");
			} else {
				player.sendMessage(ChatColor.RED + "You already have a better axe!");
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
			break;	
		
		/******************** SHEARS ********************/
		case "SPECIAL_SHEARS":
			if(bwPlayer.getShears().getLevel() < BedwarsTools.Shears.SHEARS.getLevel()) {
				// Upgrade is legal
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
				shopItem.playerPay(player);
				
				bwPlayer.setShears(BedwarsTools.Shears.SHEARS);
				bwPlayer.setPlayerShears();
				player.sendMessage(ChatColor.GREEN + "You bought Shears for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName() + ".");
			} else {
				player.sendMessage(ChatColor.RED + "You already have shears!");
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
			break;	
		}
	}
}
