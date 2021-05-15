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
		
		final int nextProtLevel = (team != null ? Math.min(team.getProtLevel() + 1, 4) : 1);
		final int curProtLevel = (team != null ? team.getProtLevel() : 0);
		
		// TODO Auto-generated method stub
		this.inv.setItem(10, this.createTeamShopItem("IRON_SWORD", 1, "Impaling (4 Diamonds)", "IMPALING", 4, "DIAMOND"));
		
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
		
		this.inv.setItem(12, this.createTeamShopItem("GOLDEN_PICKAXE", 1, "Haste (2 Diamonds)", "HASTE", 2, "DIAMOND"));
		this.inv.setItem(13, this.createTeamShopItem("FURNACE", 1, "Forge Upgrade (2 Diamonds)", "FORGE", 2, "DIAMOND"));
		this.inv.setItem(14, this.createTeamShopItem("BEACON", 1, "Heal Pool (1 Diamond)", "HEAL_POOL", 1, "DIAMOND"));
		
		this.inv.setItem(15, this.createShopLink("LEATHER", 1, ChatColor.YELLOW + "Buy a trap", "SHOP_TRAP", teamColor));
	}

	@Override
	public void handleTransaction(Player player, String shopCostString) {
		if(this.bedwarsGame.hasTeam(player)) {
			// Note -> access the team upgrade name with getMatName()
			ShopItem shopItem = new ShopItem(shopCostString);
			if(shopItem.canPlayerAfford(player)) {
				TeamUpgrade upgradeType = TeamUpgrade.getByName(shopItem.getMatName());
				this.handleTeamUpgrade(player, shopItem, upgradeType);
			} else {
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
		case PROTECTION:
			final int currentProt = team.getProtLevel();
			if(currentProt < 4) {
				shopItem.playerPay(player);
				team.setProtectionLevel(currentProt + 1);
			} else {
				player.sendMessage(ChatColor.RED + "You already have maximum protection!");
			}
			
			break;
		}		
	}
}
