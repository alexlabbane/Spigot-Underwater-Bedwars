package com.alexlabbane.underwaterbedwars.shoputil;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Abstract class to construct purchased items from shop metadata strings
 * @author Alex Labbane
 *
 */
public abstract class ShopWare {
	protected int amount;
	protected String matName;
	protected int payAmount;
	protected String payMatName;
	
	/**
	 * Create a new ShopWare
	 * @param shopString	the metadata string defining the purchased item
	 */
	public ShopWare(String shopString) {
		String[] splitString = shopString.split(",");
		
		this.matName = splitString[0];
		this.amount = Integer.parseInt(splitString[1]);
		this.payMatName = splitString[2];
		this.payAmount = Integer.parseInt(splitString[3]);
	}
	
	/************* Getters/Setters *************/
	
	public int getAmount() { return this.amount; }
	public int getPayAmount() { return this.payAmount; }
	public String getMatName() { return this.matName; }
	public String getPayMatName() { return this.payMatName; }
	public Material getMat() { return Material.getMaterial(this.matName); }
	public Material getPayMat() { return Material.getMaterial(this.payMatName); }
	
	/**
	 * Determine whether or not a player can afford to puchase the ShopWare
	 * @param p		the player to check
	 * @return		true if the player can afford the ShopWare
	 */
	public boolean canPlayerAfford(Player p) {
		Material payMat = this.getPayMat();
		Inventory playerInv = p.getInventory();
		
		int numNeeded = this.getPayAmount();
		for(ItemStack i : playerInv.getContents()) {
			if(i == null)
				continue;
			
			if(i.getType() == payMat) {
				int canRemove = Math.min(numNeeded, i.getAmount());
				numNeeded -= canRemove;
				
				if(numNeeded == 0)
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Remove the items required to purchase the ShopWare from the player inventory
	 * @param p		the player to remove the items from
	 * @return		true if the player successfully was able to pay
	 */
	public boolean playerPay(Player p) {
		Material payMat = this.getPayMat();
		Inventory playerInv = p.getInventory();
		
		int numNeeded = this.getPayAmount();
		for(ItemStack i : playerInv.getContents()) {
			if(i == null)
				continue;
			
			if(i.getType() == payMat) {
				int toRemove = Math.min(numNeeded, i.getAmount());
				i.setAmount(i.getAmount() - toRemove);
				numNeeded -= toRemove;
				
				if(numNeeded == 0)
					return true;
			}
		}
		
		return false;
	}
}
