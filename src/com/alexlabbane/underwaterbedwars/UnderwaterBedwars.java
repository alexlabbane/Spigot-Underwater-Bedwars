package com.alexlabbane.underwaterbedwars;
import java.lang.System.Logger;
import java.util.List;
import java.util.logging.Level;

import org.apache.logging.log4j.io.LoggerBufferedInputStream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import com.alexlabbane.underwaterbedwars.gui.ItemShop;
import com.alexlabbane.underwaterbedwars.listeners.PlayerItemDamageListener;
import com.alexlabbane.underwaterbedwars.listeners.WaterListener;
import com.alexlabbane.underwaterbedwars.util.TeamColor;
import com.alexlabbane.underwaterbedwars.util.Util;

public class UnderwaterBedwars extends JavaPlugin implements Listener {
	
	private static ItemShop testShop;
	private static BedwarsGame game;
	
    // Fired when plugin is first enabled
    @Override
    public void onEnable() {
    	Util.setPlugin(this);
    	this.saveDefaultConfig();
    	
    	game = new BedwarsGame(this);
    	
//    	game.addTeam(TeamColor.PINK);
//    	game.addTeam(TeamColor.AQUA);
    	game.addTeam(TeamColor.BLUE);
//    	game.addTeam(TeamColor.GRAY);
//    	game.addTeam(TeamColor.GREEN);
//    	game.addTeam(TeamColor.RED);
//    	game.addTeam(TeamColor.WHITE);
//    	game.addTeam(TeamColor.YELLOW);

    	
    	testShop = new ItemShop("PINK", game);
    	getServer().getLogger().log(Level.WARNING, "Underwater Bedwars is enabled!");
    	getServer().getPluginManager().registerEvents(this, this);
    	getServer().getPluginManager().registerEvents(new WaterListener(this), this);
    	getServer().getPluginManager().registerEvents(new PlayerItemDamageListener(), this);
    	getServer().getPluginManager().registerEvents(testShop, this);
    }
    // Fired when plugin is disabled
    @Override
    public void onDisable() {
    	
    }
    
    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
    	game.getTeams().get(0).addPlayer(e.getPlayer());
    	Bukkit.broadcastMessage("Debug: Added to white team");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if(sender instanceof Player) {
    		Player p = ((Player) sender);
    		if(!p.isOp()) {
    			p.sendMessage(ChatColor.RED + "Only operators can use commands!");
    			return false;
    		}
    	}
    	
    	if(label.equals("toggle")) {
    		if(args.length == 0)
    			return false;
    		
    		if(args[0].equals("water_flow")) {
    			WaterListener.toggle();
    			Bukkit.broadcastMessage(ChatColor.YELLOW + "Toggled water flow.");
    		}
    	} else if(label.equals("testshop")) {
    		if(sender instanceof HumanEntity)
    			testShop.openInventory((HumanEntity)sender);
    		
    		return true;
    	} else if(label.equals("setitemshoplocation") && sender instanceof Player) {
    		//getServer().broadcastMessage("Color is " + args[0]);
    		TeamColor color = TeamColor.valueOf(args[0]);
    		
    		for(BedwarsTeam team : this.game.getTeams()) {
    			if(team.getColor() == color) {
    				team.setItemShopLocation(((Player) sender).getLocation());
    			}
    		}
    		
    	} else if(label.equals("bwteam")) {
    		if(args.length == 0) {
    			if(sender instanceof Player) {
    				Player player = (Player)sender;
    				if(game.hasTeam(player))
    					player.sendMessage(ChatColor.YELLOW + "You are on " + game.getTeam(player).getColor().toString());
    				else
    					player.sendMessage(ChatColor.RED + "You are not on a team!");
    				
    				return true;
    			}
    		}
    		
    		if(args[0].equals("add")) {
    			if(args.length <= 2)
    				return false;
    			
    			// Add a player to a team
    			Player p = sender.getServer().getPlayer(args[1]);
    			getServer().getLogger().log(Level.WARNING, p.getName());
    			if(p == null) {
    				sender.sendMessage(ChatColor.RED + "No player with the name " + args[1] + " could be found!");
    				return false;
    			}
    			
    			if(game.hasTeam(p)) {
    				sender.sendMessage(ChatColor.RED + p.getName() + " is already on " + game.getTeam(p).getColor() + "!");
    				return false;
    			}
    			
    			TeamColor color = TeamColor.valueOf(args[2].toUpperCase());
    			BedwarsTeam addedTeam = null;
    			for(BedwarsTeam team : game.getTeams())
    				if(team.getColor() == color)
    					addedTeam = team;
    			
    			addedTeam.addPlayer(p);
    			
    			if(p != sender)
    				sender.sendMessage(ChatColor.GREEN + p.getName() + " has been added to " + addedTeam.getColor());
    			p.sendMessage(ChatColor.GREEN + "You have been added to " + addedTeam.getColor());
    			return true;
    		} else if(args[0].equals("create")) {
    			if(args.length < 2)
    				return false;
    			
    			// Create a new team
    			TeamColor color = TeamColor.valueOf(args[1].toUpperCase());
    			for(BedwarsTeam team : game.getTeams())
    				if(team.getColor() == color) {
    					sender.sendMessage(ChatColor.RED + color.toString() + " team already exists!");
    					return false;
    				}
    			
    			game.addTeam(color);
    			return true;
    		} else if(args[0].equals("remove")) { //team remove <player> <team>
    			if(args.length <= 2)
    				return false;
    			
    			Player p = sender.getServer().getPlayer(args[1]);
    			TeamColor color = TeamColor.valueOf(args[2].toUpperCase());
    			if(p == null) {
    				sender.sendMessage(ChatColor.RED + "No player with the name " + args[1] + " could be found!");
    				return false;
    			}
    			for(BedwarsTeam team : game.getTeams()) {
    				if(team.getColor() == color) {
    					team.removePlayer(p);
    					
    					if(p != sender)
    						sender.sendMessage(ChatColor.GREEN + p.getName() + " has been removed from " + team.getColor());
    					p.sendMessage(ChatColor.RED + "You have been removed from " + team.getColor());
    				}
    			}
    			
    			return true;
    		} else if(args[0].equals("delete")) {
    			if(args.length < 2)
    				return false;
    			
    			TeamColor color = TeamColor.valueOf(args[1].toUpperCase());
    			game.removeTeam(color);
    			sender.sendMessage(ChatColor.RED + color.toString() + " has been deleted");
    			return true;
    		} else if(args[0].equals("list")) {
    			if(args.length < 2)
    				return false;
    			
    			TeamColor color = TeamColor.valueOf(args[1].toUpperCase());
    			
    			String response = color.getColor() + ": ";
    			for(BedwarsTeam team : game.getTeams())
    				if(team.getColor() == color)
    					for(Player p : team.getPlayers())
    						response += p.getName() + ", ";
    			
    			sender.sendMessage(ChatColor.YELLOW + response);
    			return true;
    		} else if(args[0].equals("teams")) {
    			String response = "Teams: ";
    			for(BedwarsTeam team : game.getTeams()) {
    				response += team.getColor().toString() + ", ";
    			}
    			
    			sender.sendMessage(ChatColor.YELLOW + response);
    			return true;
    		}
    	}

    	return false;
    }
    
    public Player getPlayerByName(World world, String playerName) {
    	List<Player> players = world.getPlayers();
    	
    	for(Player p : players) {
    		if(p.getName().equals(playerName))
    			return p;
    	}
    	
    	return null;
    }
}
