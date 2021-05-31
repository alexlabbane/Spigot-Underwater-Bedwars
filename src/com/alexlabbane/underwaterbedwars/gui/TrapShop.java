package com.alexlabbane.underwaterbedwars.gui;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.alexlabbane.underwaterbedwars.BedwarsGame;
import com.alexlabbane.underwaterbedwars.BedwarsTeam;
import com.alexlabbane.underwaterbedwars.shoputil.ShopItem;
import com.alexlabbane.underwaterbedwars.util.TeamTrap;

import net.md_5.bungee.api.ChatColor;

/**
 * Shop inventory for all trap team upgrades
 * @author Alex Labbane
 *
 */
public class TrapShop extends TeamShop {
	/**
	 * Create a new trap shop
	 * @param color		the color of the team the shop is for
	 * @param game		reference to the game the shop is a part of
	 */
	public TrapShop(String color, BedwarsGame game) {
		super(color, game, "Queue a trap");
	}
	
	/**
	 * Create a new trap shop with a custom title
	 * @param color		the color of the team the shop is for
	 * @param game		reference to the game the shop is a part of
	 * @param title		the custom title for the shop
	 */
	public TrapShop(String color, BedwarsGame game, String title) {
		super(color, game, title);
	}	
	
	/**
	 * Initialize all of the items/links that should be in the shop
	 * @param player	the player to create the shop for
	 */
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
	
	/**
	 * Handle attempted transactions in the shop (attempts to queue a trap), which are then further
	 * handled with {@link #handleTrapQueue(Player, ShopItem, TeamTrap)}
	 * @param player			the player attempting to make the transaction
	 * @param shopCostString	the string in the item metadata the determines shop item parameters
	 */
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
	
	/**
	 * Place a purchased trap in the team's trap queue
	 * @param player	the player who purchased the trap
	 * @param shopItem	the object representing the purchased trap
	 * @param trapType	the type of trap purchased
	 * @see com.alexlabbane.underwaterbedwars.util.TeamTrap
	 */
	private void handleTrapQueue(Player player, ShopItem shopItem, TeamTrap trapType) {
		BedwarsTeam team = this.bedwarsGame.getTeam(player);
		
		if(team == null) return;
		
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
		player.sendMessage(ChatColor.GREEN + "You purchased a trap for " + ChatColor.YELLOW + shopItem.getPayAmount() + "x " + shopItem.getPayMatName());
		
		team.getQueuedTraps().push(trapType);
		shopItem.playerPay(player);
		
		this.initializeItems(player);
		team.getTeamShop().initializeItems(player);
	}
}
