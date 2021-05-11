package com.alexlabbane.underwaterbedwars.shoputil;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class ShopWare {
	protected int amount;
	protected String matName;
	protected int payAmount;
	protected String payMatName;
	
	public ShopWare(String shopString) {
		String[] splitString = shopString.split(",");
		
		this.matName = splitString[0];
		this.amount = Integer.parseInt(splitString[1]);
		this.payMatName = splitString[2];
		this.payAmount = Integer.parseInt(splitString[3]);
	}
	
	public int getAmount() { return this.amount; }
	public int getPayAmount() { return this.payAmount; }
	public String getMatName() { return this.matName; }
	public String getPayMatName() { return this.payMatName; }
	public Material getMat() { return Material.getMaterial(this.matName); }
	public Material getPayMat() { return Material.getMaterial(this.payMatName); }
	
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
