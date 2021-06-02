package com.alexlabbane.underwaterbedwars;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

import com.alexlabbane.underwaterbedwars.util.TeamColor;
import com.alexlabbane.underwaterbedwars.util.Util;

import net.md_5.bungee.api.ChatColor;

/**
 * Represents a BedwarsGame; generally should only be one instance per server
 * @author Alex Labbane
 *
 */
public class BedwarsGame {
	private Plugin plugin;
	private int gameID;
	private boolean mapEdit;
	private ArrayList<BedwarsTeam> teams;	
	private ArrayList<GameGen> gens;
	
	private BukkitTask genTimerTask;
	private int genLevel;
	private int ticksToNextLevel; // How many ticks are left until the next gen upgrade
	
	/************* Static members *************/
	
	private static ArrayList<BedwarsGame> activeGames = new ArrayList<BedwarsGame>(); // Allows all games to be accessed in static context from anywhere
	private static Material[] currencies = new Material[] { Material.IRON_INGOT, Material.GOLD_INGOT, Material.DIAMOND, Material.EMERALD };
	private static int nextID = 0;
	
	/**
	 * Create a 	new BedwarsGame
	 * @param p 	reference to the plugin
	 */
	public BedwarsGame(Plugin p) {
		this.plugin = p;
		this.mapEdit = false;
		activeGames.add(this);
		
		this.gameID = nextID;
		nextID++;
		
		this.genTimerTask = null;
		this.resetGame();
	}
	
	/************* Getters/setters *************/
	
	public Plugin getPlugin() { return this.plugin; }
	public ArrayList<BedwarsTeam> getTeams() { return this.teams; }
	public boolean mapIsEditable() { return this.mapEdit; }
	public void toggleMapEdit() { this.mapEdit = !this.mapEdit; }
	
	// Static getters/setters
	
	public static Material[] getCurrentcies() { return currencies; }
	
	/**
	 * Reset all elements of the game. Does not start the next game (i.e. all gens/upgrades are paused)
	 * To start the game, call startGame()
	 */
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
		
		// Reset member variables
		this.teams = new ArrayList<BedwarsTeam>();
		this.gens = new ArrayList<GameGen>();
		this.genLevel = 0;
		this.ticksToNextLevel = 0;		
		
		// Get all the active teams from the config
		this.addActiveTeams();
	}
	
	/**
	 * Starts a game (gens/upgrade countdown)
	 * Teleports all players to starting location
	 */
	public void startGame() {
		// Wrap everything in a BukkitRunnable to delay start by 5 seconds
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
							player.setFoodLevel(20);
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
		
		for(String key : teamConfig.getKeys(false)) {			
			if(config.getBoolean("teams." + key + ".active")) {
				BedwarsTeam newTeam = new BedwarsTeam("teams." + key, this.plugin, this);
				newTeam.pauseGen();
				
				this.teams.add(newTeam);
				
				// Register listener for team
				Bukkit.getServer().getPluginManager().registerEvents(newTeam, this.plugin);
			}
		}
	}
	
	/**
	 * If the upgrade timer has been started, stop it
	 * The next time the timer starts, all progress towards
	 * the next phase of the game will have been lost
	 */
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
	
	/**
	 * Upgrade all gens in the game (except team gens)
	 */
	public void upgradeGens() {
		this.genLevel++;
		
		for(GameGen gen : this.gens) {
			gen.setGenLevel(this.genLevel);
		}
	}
	
	/**
	 * Find the team a given player belongs to
	 * @param player 	the player to find the team for
	 * @return 			the team or null if no team is found
	 */
	public BedwarsTeam getTeam(Player player) {
		for(BedwarsTeam team: this.teams) {
			if(team.hasPlayer(player))
				return team;
		}
		
		return null;
	}
	
	/**
	 * Determine if a player is on a team or not
	 * @param player	the player to check
	 * @return			true if the player is on a team
	 */
	public boolean hasTeam(Player player) {
		if(this.getTeam(player) != null)
			return true;
		return false;
	}
	
	/**
	 * Remove the first team with a given color from the game
	 * @param color		the color of the team to remove
	 */
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
	 * @param p		The player to retrieve
	 * @return 		BedwarsPlayer instance associated with p
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
	 * @return		the message to be displayed
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
	 * Kill all entities in the world the game is hosted in
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
	
	/**
	 * Get the BedwarsGame with a given ID
	 * @param gameID	the ID of the game to retrieve
	 * @return			the BedwarsGame associated with the given gameID
	 */
	public static BedwarsGame getGame(int gameID) {
		for(BedwarsGame game: activeGames) {
			if(game.gameID == gameID)
				return game;
		}
		
		return null;
	}
}
