package com.alexlabbane.underwaterbedwars.util;

import org.bukkit.entity.Player;

import com.alexlabbane.underwaterbedwars.BedwarsPlayer;
import com.alexlabbane.underwaterbedwars.BedwarsTeam;
import com.alexlabbane.underwaterbedwars.UnderwaterBedwars;

import net.md_5.bungee.api.ChatColor;

// TODO: Add custom death messages
public class ChatMessages {
	
	/**
	 * Colors a chat message with player names
	 * depending on their team color
	 * @param chatMessage
	 * @return colored chat message
	 */
	public static String getColoredChatMessage(String chatMessage) {
		for(BedwarsTeam team : UnderwaterBedwars.game.getTeams()) {
			ChatColor color = ChatColor.valueOf(team.getColor().getColor());
			
			for(Player player : team.getPlayers()) {
				int startIndex = chatMessage.indexOf(player.getName());
				int endIndex = startIndex + player.getName().length();
				
				// If player is not in the death message
				if(startIndex == -1)
					continue;
				
				chatMessage = 
						ChatColor.WHITE + chatMessage.substring(0, startIndex) + 
						color + chatMessage.substring(startIndex, endIndex) + 
						ChatColor.WHITE + chatMessage.substring(endIndex);
			}
		}
		
		return chatMessage;
	}
}
