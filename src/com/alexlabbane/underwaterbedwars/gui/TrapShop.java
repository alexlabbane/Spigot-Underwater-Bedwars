package com.alexlabbane.underwaterbedwars.gui;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.alexlabbane.underwaterbedwars.BedwarsGame;
import com.alexlabbane.underwaterbedwars.BedwarsTeam;
import com.alexlabbane.underwaterbedwars.shoputil.ShopItem;
import com.alexlabbane.underwaterbedwars.util.TeamTrap;

import net.md_5.bungee.api.ChatColor;

public class TrapShop extends TeamShop {
	public TrapShop(String color, BedwarsGame game) {
		super(color, game, "Queue a trap");
	}
	
	public TrapShop(String color, BedwarsGame game, String title) {
		super(color, game, title);
	}	
	
	@Override
	public void initializeItems(Player player) {
		BedwarsTeam team = this.bedwarsGame.getTeam(player);
		if(team == null)
			return;
		
		int trapCost;
		switch(team.getQueuedTraps().size()) {
		case 0:
			trapCost = 1;
			break;
		case 1:
			trapCost = 2;
			break;
		default:
			trapCost = 4;		
		}
		
		this.inv.setItem(10, this.createTeamShopItem("TRIPWIRE_HOOK", 1, ChatColor.RED + "It's a trap! (" + trapCost + " Diamond)", "BLINDNESS_SLOWNESS", trapCost, "DIAMOND"));
		this.inv.setItem(11, this.createTeamShopItem("FEATHER", 1, ChatColor.RED + "Counter-Offensive Trap (" + trapCost + " Diamond)", "COUNTER_OFFENSIVE", trapCost, "DIAMOND"));
		this.inv.setItem(12, this.createTeamShopItem("REDSTONE_TORCH", 1, ChatColor.RED + "Alarm Trap (" + trapCost + " Diamond)", "ALARM", trapCost, "DIAMOND"));
		this.inv.setItem(13, this.createTeamShopItem("IRON_PICKAXE", 1, ChatColor.RED + "Miner Fatigue Trap (" + trapCost + " Diamond)", "MINER_FATIGUE", trapCost, "DIAMOND"));
		this.inv.setItem(22, this.createShopLink("ARROW", 1, ChatColor.GREEN + "Go back", "SHOP_TEAM", team.getColor().name()));
	}	
	
	@Override
	public void handleTransaction(Player player, String shopCostString) {
		BedwarsTeam team = this.bedwarsGame.getTeam(player);
		
		if(team == null) return;
		
		ShopItem trapItem = new ShopItem(shopCostString);
		if(trapItem.canPlayerAfford(player) && !team.getQueuedTraps().full()) {
			// Note: Access trap name with -> getMatName()
			TeamTrap trap = TeamTrap.getByName(trapItem.getMatName());
			this.handleTrapQueue(player, trapItem, trap);
		} else {
			player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			if(!trapItem.canPlayerAfford(player))
				player.sendMessage(ChatColor.RED + "You can't afford this upgrade!");
			else if(team.getQueuedTraps().full())
				player.sendMessage(ChatColor.RED + "No free trap slots!");
		}
	}
	
	private void handleTrapQueue(Player player, ShopItem shopItem, TeamTrap trapType) {
		BedwarsTeam team = this.bedwarsGame.getTeam(player);
		
		if(team == null) return;
		
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
		player.sendMessage(ChatColor.GREEN + "You purchased a trap for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName());
		
		team.getQueuedTraps().push(trapType);
		shopItem.playerPay(player);
		
		this.initializeItems(player);
	}
}
