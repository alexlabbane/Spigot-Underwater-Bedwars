package com.alexlabbane.underwaterbedwars;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.alexlabbane.underwaterbedwars.util.TeamColor;

// Only one instance will exists in the plugin; will be controlled by commands in chat like /bedwars reset, etc.
public class BedwarsGame {
	private static ArrayList<BedwarsGame> activeGames = new ArrayList<BedwarsGame>(); // Allows all games to be accessed in static context from anywhere
	private static int nextID = 0;
	
	private Plugin plugin;
	private int gameID;
	private ArrayList<BedwarsTeam> teams;
	
	public BedwarsGame(Plugin p) {
		this.plugin = p;
		activeGames.add(this);
		this.gameID = nextID;
		nextID++;
		this.resetGame();
	}
	
	public void resetGame() {
		this.teams = new ArrayList<BedwarsTeam>();
	}
	
	public ArrayList<BedwarsTeam> getTeams() { return this.teams; }
	
	public void addTeam(TeamColor color) {
		BedwarsTeam team = new BedwarsTeam(color.getColor(), this.plugin, this);
		Bukkit.getServer().getPluginManager().registerEvents(team, this.plugin);
		this.teams.add(team);
	}
	
	// Get the team of player
	public BedwarsTeam getTeam(Player player) {
		for(BedwarsTeam team: this.teams) {
			if(team.hasPlayer(player))
				return team;
		}
		
		return null;
	}
	
	// Tell if player already has a team or not
	public boolean hasTeam(Player player) {
		if(this.getTeam(player) != null)
			return true;
		return false;
	}
	
	public void removeTeam(TeamColor color) {
		for(BedwarsTeam team : this.teams) {
			if(team.getColor() == color) {
				this.teams.remove(team);
				return;
			}
		}
	}
	
	/**
	 * Get instance of BedwarsPlayer associated with a specific player
	 * Returns null if no such BedwarsPlayer exists
	 * @param p (the player)
	 * @return BedwarsPlayer of param p
	 */
	public BedwarsPlayer getBedwarsPlayer(Player p) {
		for(BedwarsTeam team : this.teams) { 
			for(BedwarsPlayer bwPlayer : team.getBedwarsPlayers()) {
				if(bwPlayer.getPlayer() == p)
					return bwPlayer;
			}
		}
		
		return null;
	}
	
	public static BedwarsGame getGame(int gameID) {
		for(BedwarsGame game: activeGames) {
			if(game.gameID == gameID)
				return game;
		}
		
		return null;
	}
}
