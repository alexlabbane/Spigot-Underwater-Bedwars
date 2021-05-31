package com.alexlabbane.underwaterbedwars;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import com.alexlabbane.underwaterbedwars.listeners.BlockListener;
import com.alexlabbane.underwaterbedwars.listeners.HungerListener;
import com.alexlabbane.underwaterbedwars.listeners.PlayerItemDamageListener;
import com.alexlabbane.underwaterbedwars.listeners.PlayerMoveArmorListener;
import com.alexlabbane.underwaterbedwars.listeners.WaterListener;
import com.alexlabbane.underwaterbedwars.util.TeamColor;
import com.alexlabbane.underwaterbedwars.util.Util;

/**
 * The main plugin class for Underwater Bedwars
 * @author Alex Labbane
 *
 */
public class UnderwaterBedwars extends JavaPlugin implements Listener {
	
	// Dangerous: allows anybody to access/alter game object
	public static BedwarsGame game;
	
    /**
     * Performs some initial setup and registers listeners
     * Also instantiates main game object
     */
    @Override
    public void onEnable() {
    	Util.setPlugin(this);
    	this.saveDefaultConfig();
    	
    	game = new BedwarsGame(this);
    	
    	getServer().getLogger().log(Level.WARNING, "Underwater Bedwars is enabled!");
    	getServer().getPluginManager().registerEvents(this, this);
    	getServer().getPluginManager().registerEvents(new WaterListener(this), this);
    	getServer().getPluginManager().registerEvents(new PlayerItemDamageListener(), this);
    	getServer().getPluginManager().registerEvents(new PlayerMoveArmorListener(), this);
    	getServer().getPluginManager().registerEvents(new BlockListener(), this);
    	getServer().getPluginManager().registerEvents(new HungerListener(), this);
    }
    
    // Fired when plugin is disabled
    @Override
    public void onDisable() {
    	
    }
    
    // Fired when player logs in to server
    @EventHandler
    public void onLogin(PlayerLoginEvent e) {

    }
    
    /**
     * Handles all console commands (WIP)
     */
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
    		} else if(args[0].equals("map_edit")) {
    			game.toggleMapEdit();
    			Bukkit.broadcastMessage("Map editable: " + game.mapIsEditable());
    		}
    	} else if(label.equals("setitemshoplocation") && sender instanceof Player) {
    		//getServer().broadcastMessage("Color is " + args[0]);
    		TeamColor color = TeamColor.valueOf(args[0]);
    		
    		for(BedwarsTeam team : this.game.getTeams()) {
    			if(team.getColor() == color) {
    				team.setItemShopLocation(((Player) sender).getLocation());
    			}
    		}
    	
    	} else if(label.equals("bwgame")) {
    		if(args.length == 0) {
    			return false;
    		}
    		
    		if(args[0].equals("reset")) {
    			game.resetGame();
    		} else if(args[0].equals("start")) {
    			game.startGame();
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
    
    /**
     * Retrieves the Player instance associated with a given player name
     * @param world			the world to search in
     * @param playerName	the name of the player to find
     * @return				the Player instance associated with playerName
     */
    public Player getPlayerByName(World world, String playerName) {
    	List<Player> players = world.getPlayers();
    	
    	for(Player p : players) {
    		if(p.getName().equals(playerName))
    			return p;
    	}
    	
    	return null;
    }
}
