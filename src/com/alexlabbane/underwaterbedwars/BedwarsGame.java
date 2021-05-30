package com.alexlabbane.underwaterbedwars;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.alexlabbane.underwaterbedwars.util.TeamColor;
import com.alexlabbane.underwaterbedwars.util.Util;

import net.md_5.bungee.api.ChatColor;

// Only one instance will exists in the plugin; will be controlled by commands in chat like /bedwars reset, etc.
public class BedwarsGame {
	private static ArrayList<BedwarsGame> activeGames = new ArrayList<BedwarsGame>(); // Allows all games to be accessed in static context from anywhere
	private static int nextID = 0;
	
	private Plugin plugin;
	private int gameID;
	private ArrayList<BedwarsTeam> teams;	
	private ArrayList<GameGen> gens;
	
	private BukkitTask genTimerTask;
	private int genLevel;
	private int ticksToNextLevel; // How many ticks are left until the next gen upgrade
	
	public BedwarsGame(Plugin p) {
		this.plugin = p;
		activeGames.add(this);
		
		this.gameID = nextID;
		nextID++;
		
		this.genTimerTask = null;
		this.resetGame();
	}
	
	public void resetGame() {
		// Kill all non-player entities prior to start of game
		this.killEntities();
		
		// Stop all gens from spawning
		this.stopGenUpgradeTimer();
		
		if(this.gens != null) {
			for(GameGen gen : this.gens) { 
				gen.stopGen();
			}
		}

		if(this.getTeams() != null) {
			for(BedwarsTeam team : this.getTeams()) {
				team.stopGen();
				
				// Unregister team as listener
				HandlerList.unregisterAll(team);
				HandlerList.unregisterAll(team.getBed());
			}
		}
		
		this.teams = new ArrayList<BedwarsTeam>();
		this.gens = new ArrayList<GameGen>();
		this.genLevel = 0;
		this.ticksToNextLevel = 0;		
		
		// Get all the active teams from the config
		this.addActiveTeams();
	}
	
	/**
	 * Starts gens/upgrade countdown
	 * Teleports all players to starting location
	 */
	public void startGame() {
		new BukkitRunnable() {
			int secondsBeforeStart = 5;
			
			@Override
			public void run() {
				if(secondsBeforeStart > 0) {
					for(BedwarsTeam team : getTeams()) {
						for(Player player : team.getPlayers()) {
							player.sendTitle(
									"",
									ChatColor.RED + "Game starts in " 
											+ ChatColor.YELLOW + secondsBeforeStart 
											+ ChatColor.RED + " seconds",
									0,
									Util.TICKS_PER_SECOND,
									0);
						}
					}
					
					secondsBeforeStart--;
				} else {
					// Get gen locations from config
					FileConfiguration config = plugin.getConfig();
					ConfigurationSection genConfig = config.getConfigurationSection("game.gens");
					
					for(String key : genConfig.getKeys(false)) {
						gens.add(new GameGen(key));		
					}
					
					for(BedwarsTeam team : teams) {
						team.resumeGen();
						
						// Teleport all players to start location
						// Remove effects, put them in survival
						for(Player player : team.getPlayers()) {
							player.teleport(team.getSpawnLocation());
							player.setGameMode(GameMode.SURVIVAL);
							player.sendTitle(ChatColor.GREEN + "Start!", "", 0, 20, 0);
							player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
							for(PotionEffect pe : player.getActivePotionEffects()) {
								player.removePotionEffect(pe.getType());
							}
						}
					}
					
					startGenUpgradeTimer();	
					this.cancel();
				}
			}
		}.runTaskTimer(this.plugin, 0, Util.TICKS_PER_SECOND);
	}
	
	/**
	 * Get all active teams from the config and add them to the game
	 */
	private void addActiveTeams() {
		FileConfiguration config = this.plugin.getConfig();
		ConfigurationSection teamConfig = config.getConfigurationSection("teams");
		
		Bukkit.broadcastMessage("Adding teams " + teamConfig.getKeys(false).size());
		for(String key : teamConfig.getKeys(false)) {
			Bukkit.broadcastMessage("Adding " + key);
			
			if(config.getBoolean("teams." + key + ".active")) {
				BedwarsTeam newTeam = new BedwarsTeam("teams." + key, this.plugin, this);
				newTeam.pauseGen();
				
				this.teams.add(newTeam);
				
				// Register listener for team
				Bukkit.getServer().getPluginManager().registerEvents(newTeam, this.plugin);
			}
		}
	}
	
	public void stopGenUpgradeTimer() {
		if(this.genTimerTask != null) {
			this.genTimerTask.cancel();
		}
	}
	
	/**
	 * Starts BukkitTask to count down time to next gen upgrade
	 */
	public void startGenUpgradeTimer() {
		FileConfiguration config = plugin.getConfig();
		this.ticksToNextLevel = config.getInt("game.game-level.level-" + (genLevel + 1) + ".delay-ticks");		
		
		this.stopGenUpgradeTimer();
		
		this.genTimerTask = new BukkitRunnable() {
			
			@Override
			public void run() {
				ticksToNextLevel -= Util.TICKS_PER_SECOND;
				updateScoreboards();
				
				if(ticksToNextLevel <= 0) {
					upgradeGens();
					
					// Get the time to the next upgrade (or cancel the task if all upgrades reached)
					ticksToNextLevel = config.getInt("game.game-level.level-" + (genLevel + 1) + ".delay-ticks");
				}
			}
		}.runTaskTimer(this.plugin, 0, Util.TICKS_PER_SECOND);
	}
	
	public void upgradeGens() {
		this.genLevel++;
		
		for(GameGen gen : this.gens) {
			gen.setGenLevel(this.genLevel);
		}
	}
	
	public Plugin getPlugin() { return this.plugin; }
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
	
	/**
	 * Update the scoreboard for all players in the game
	 */
	public void updateScoreboards() {
		for(BedwarsTeam team : this.teams) {
			for(BedwarsPlayer bwPlayer : team.getBedwarsPlayers()) {
				bwPlayer.updateScoreboard();
			}
		}
	}
	
	/**
	 * Get scoreboard message for time left to next gen upgrade
	 */
	public String timeToNextGenUpgrade() {
		String str = "";
		FileConfiguration config = plugin.getConfig();
		String tier = config.getString("game.game-level.level-" + (genLevel + 1) + ".tier");		
		
		str += tier + " in ";
		
		// Add the timer
		int secondsToUpgrade = this.ticksToNextLevel / Util.TICKS_PER_SECOND;
		str += ChatColor.GREEN;
		str += (secondsToUpgrade / 60) + ":";
		
		int secondsTimer = secondsToUpgrade % 60;
		str += ((secondsTimer) < 10 ? "0" + secondsTimer : secondsTimer + "");
		
		return str;
	}
	
	/**
	 * Kill all entities in the game world
	 */
	public void killEntities() {
		World world = Bukkit.getServer().getWorlds().get(0);
		for(Entity e : world.getEntities()) {
			if(e instanceof Player) {
				continue;
			}
			
			e.remove();
		}
	}
	
	public static BedwarsGame getGame(int gameID) {
		for(BedwarsGame game: activeGames) {
			if(game.gameID == gameID)
				return game;
		}
		
		return null;
	}
}
