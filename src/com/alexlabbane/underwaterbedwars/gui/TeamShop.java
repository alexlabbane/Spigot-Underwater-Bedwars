package com.alexlabbane.underwaterbedwars.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.alexlabbane.underwaterbedwars.BedwarsGame;
import com.alexlabbane.underwaterbedwars.BedwarsTeam;
import com.alexlabbane.underwaterbedwars.shoputil.ShopItem;
import com.alexlabbane.underwaterbedwars.util.TeamUpgrade;
import com.alexlabbane.underwaterbedwars.util.TrapQueue;
import com.alexlabbane.underwaterbedwars.util.Util;

import net.md_5.bungee.api.ChatColor;

public class TeamShop extends Shop implements Listener {
	public TeamShop(String color, BedwarsGame game) {
		super(45, game, "Upgrades & Traps");
		this.initializeItems(null);
	}
	
	public TeamShop(String color, BedwarsGame game, String title) {
		super(45, game, title);
		this.initializeItems(null);
	}
	
	protected ItemStack createTeamShopItem(final String matName, int amount, final String displayName, final String upgradeName, int payAmount, final String payMatName) {
		final ItemStack item = new ItemStack(Material.getMaterial(matName), amount);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		
		// String that stores purchase information for the upgrade
		String shopCostString = upgradeName + "," + amount + "," + payMatName + "," + payAmount + ",0";
		Util.addNBTTagString(item, "ShopCost", shopCostString);
		
		return item;
	}
	
	@Override
	public void initializeItems(Player player) {
		BedwarsTeam team = this.bedwarsGame.getTeam(player);
		String teamColor = "WHITE";
		if(team != null) teamColor = team.getColor().name();
		
		final int nextImpalingLevel = (team != null ? Math.min(team.getImpalingLevel() + 1, 4) : 1);
		final int curImpalingLevel = (team != null ? team.getImpalingLevel() : 0);
		
		switch(curImpalingLevel) {
		case 0:
			this.inv.setItem(10, this.createTeamShopItem("IRON_SWORD", nextImpalingLevel, "Sharpness (4 Diamonds)", "IMPALING", 4, "DIAMOND"));
			break;
		default:
			this.inv.setItem(10, this.createTeamShopItem("IRON_SWORD", nextImpalingLevel, "Sharpness (4 Diamonds)", "IMPALING", 4, "DIAMOND"));
		}
		
		final int nextProtLevel = (team != null ? Math.min(team.getProtLevel() + 1, 4) : 1);
		final int curProtLevel = (team != null ? team.getProtLevel() : 0);
				
		switch(curProtLevel) {
		case 0:
			this.inv.setItem(11, this.createTeamShopItem("IRON_CHESTPLATE", nextProtLevel, "Protection " + nextProtLevel + " (2 Diamonds)", "PROTECTION", 2, "DIAMOND"));
			break;
		case 1:
			this.inv.setItem(11, this.createTeamShopItem("IRON_CHESTPLATE", nextProtLevel, "Protection " + nextProtLevel + " (4 Diamonds)", "PROTECTION", 4, "DIAMOND"));
			break;
		case 2:
			this.inv.setItem(11, this.createTeamShopItem("IRON_CHESTPLATE", nextProtLevel, "Protection " + nextProtLevel + " (8 Diamonds)", "PROTECTION", 8, "DIAMOND"));
			break;
		case 3:
			this.inv.setItem(11, this.createTeamShopItem("IRON_CHESTPLATE", nextProtLevel, "Protection " + nextProtLevel + " (16 Diamonds)", "PROTECTION", 16, "DIAMOND"));
			break;
		default:
			this.inv.setItem(11, this.createTeamShopItem("IRON_CHESTPLATE", nextProtLevel, "Protection " + nextProtLevel + " (16 Diamonds)", "PROTECTION", 16, "DIAMOND"));
		}
		
		final int nextHasteLevel = (team != null ? Math.min(team.getHasteLevel() + 1, 2) : 1);
		final int curHasteLevel = (team != null ? team.getHasteLevel() : 0);
		
		switch(curHasteLevel) {
		case 0:
			this.inv.setItem(12, this.createTeamShopItem("GOLDEN_PICKAXE", nextHasteLevel, "Haste " + nextHasteLevel + " (2 Diamonds)", "HASTE", 2, "DIAMOND"));
			break;
		case 1:
			this.inv.setItem(12, this.createTeamShopItem("GOLDEN_PICKAXE", nextHasteLevel, "Haste " + nextHasteLevel + " (4 Diamonds)", "HASTE", 4, "DIAMOND"));
			break;
		case 2:
			this.inv.setItem(12, this.createTeamShopItem("GOLDEN_PICKAXE", nextHasteLevel, "Haste " + nextHasteLevel + " (4 Diamonds)", "HASTE", 4, "DIAMOND"));
			break;
		default:
		}
		
		final int curForgeLevel = (team != null ? team.getForgeLevel() : 0);
		
		switch(curForgeLevel) {
		case 0:
			this.inv.setItem(13, this.createTeamShopItem("FURNACE", curForgeLevel+1, "Iron Forge (2 Diamonds)", "FORGE", 2, "DIAMOND"));
			break;
		case 1:
			this.inv.setItem(13, this.createTeamShopItem("FURNACE", curForgeLevel+1, "Gold Forge (4 Diamonds)", "FORGE", 4, "DIAMOND"));
			break;
		case 2:
			this.inv.setItem(13, this.createTeamShopItem("FURNACE", curForgeLevel+1, "Spawn Emerald (6 Diamonds)", "FORGE", 6, "DIAMOND"));
			break;
		case 3:
			this.inv.setItem(13, this.createTeamShopItem("FURNACE", curForgeLevel+1, "Molten Forge (8 Diamonds)", "FORGE", 8, "DIAMOND"));
			break;
		default:
			this.inv.setItem(13, this.createTeamShopItem("FURNACE", 4, "Molten Forge (8 Diamonds)", "FORGE", 8, "DIAMOND"));
		}
		
		this.inv.setItem(14, this.createTeamShopItem("BEACON", 1, "Heal Pool (1 Diamond)", "HEAL_POOL", 1, "DIAMOND"));
		
		this.inv.setItem(15, this.createShopLink("LEATHER", 1, ChatColor.YELLOW + "Buy a trap", "SHOP_TRAP", teamColor));
	
		final int trapStart = 30;
		for(int i = 0; i < TrapQueue.MAX_NUM_TRAPS; i++) {
			ItemStack trapDisplay = new ItemStack(Material.GRAY_STAINED_GLASS);
			ItemMeta meta = trapDisplay.getItemMeta();
			meta.setDisplayName("No trap queued");
			trapDisplay.setItemMeta(meta);
			
			if(team != null && i < team.getQueuedTraps().size()) { // If a trap is queued, place it in the display
				trapDisplay = this.createTeamShopItem(
						team.getQueuedTraps().getAtPosition(i).getDisplayMaterial().name(), 
						1, 
						"Trap #" + (i+1) + ": " + team.getQueuedTraps().getAtPosition(i).getName(), 
						"", // Leave upgrade name empty so that nothing happens on purchase 
						0, // Pay amount 0
						Material.IRON_INGOT.name());
			}
			
			this.inv.setItem(i + trapStart, trapDisplay);
		}

	}

	@Override
	public void handleTransaction(Player player, String shopCostString) {
		if(this.bedwarsGame.hasTeam(player)) {
			// Note -> access the team upgrade name with getMatName()
			ShopItem shopItem = new ShopItem(shopCostString);
			if(shopItem.canPlayerAfford(player)) {
				TeamUpgrade upgradeType = TeamUpgrade.getByName(shopItem.getMatName());
				this.handleTeamUpgrade(player, shopItem, upgradeType);
			} else if(shopItem.getPayAmount() > 0) {
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
				player.sendMessage(ChatColor.RED + "You can't afford this upgrade!");
			}
		}
		
		this.initializeItems(player);
		player.updateInventory();
	}
	
	@Override
	public void handleLink(Player player, String shopLinkString) {
		BedwarsTeam team = this.bedwarsGame.getTeam(player);
		
		if(team == null)
			return;
		
		// Open GUI of the other shop
		String[] shopLinkItems = shopLinkString.split(",");
		final String linkedShop = shopLinkItems[3];
		
		TeamShop newShop = null;
		switch(linkedShop) {
		case "SHOP_TRAP":
			newShop = team.getTrapShop();
			break;
		case "SHOP_TEAM":
			newShop = team.getTeamShop();
			break;
		}
		
		if(newShop == null)
			return;
		
		newShop.initializeItems(player);
		if(newShop != null)
			newShop.openInventory((HumanEntity)player);
	}
	
	private void handleTeamUpgrade(Player player, ShopItem shopItem, TeamUpgrade upgradeType) {
		BedwarsTeam team = this.bedwarsGame.getTeam(player);

		switch(upgradeType) {
		case IMPALING:
			final int currentImpaling = team.getImpalingLevel();
			if(currentImpaling < 1) {
				shopItem.playerPay(player);
				team.setImpalingLevel(currentImpaling + 1);
			} else {
				player.sendMessage(ChatColor.RED + "You already have impaling!");
			}
			
			break;
		case PROTECTION:
			final int currentProt = team.getProtLevel();
			if(currentProt < 4) {
				shopItem.playerPay(player);
				team.setProtectionLevel(currentProt + 1);
			} else {
				player.sendMessage(ChatColor.RED + "You already have maximum protection!");
			}
			
			break;
		case HASTE:
			final int currentHaste = team.getHasteLevel();
			if(currentHaste < 2) {
				shopItem.playerPay(player);
				team.setHasteLevel(currentHaste + 1);
			} else {
				player.sendMessage(ChatColor.RED + "You already have maximum haste!");
			}
		
			break;
		case FORGE:
			final int currentForge = team.getForgeLevel();
			if(currentForge < 4) {
				shopItem.playerPay(player);
				team.setForgeLevel(currentForge + 1);
				team.initializeGen();
			} else {
				player.sendMessage(ChatColor.RED + "You already have maximum forge!");
			}
			
			break;
		case HEAL_POOL:
			final int currentHealPoolLevel = team.getHealPoolLevel();
			if(currentHealPoolLevel < 1) {
				shopItem.playerPay(player);
				team.setHealPoolLevel(currentHealPoolLevel + 1);
				team.startHealPool();
			} else {
				player.sendMessage(ChatColor.RED + "You already have heal pool!");
			}
			
			break;
		}
	}
	
	@Override
	public void openInventory(final HumanEntity ent) {
		if(ent instanceof Player) {
			this.initializeItems((Player)ent);
		}
		
		ent.openInventory(this.inv);
	}
}
