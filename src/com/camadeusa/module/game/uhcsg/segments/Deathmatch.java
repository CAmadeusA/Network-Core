package com.camadeusa.module.game.uhcsg.segments;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.game.OrionSegment;
import com.camadeusa.module.game.uhcsg.UHCSGOrionGame;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerState;
import com.camadeusa.timing.TickSecondEvent;
import com.camadeusa.utility.MathUtil;

import io.github.theluca98.textapi.ActionBar;

public class Deathmatch extends OrionSegment {
	@Override
	public void activate() {
		UHCSGOrionGame.getInstance().setCurrentSegment(this);
		this.activateModule();
		
		getNextSegment().setOrionMap(getOrionMap());
		
	}
	
	@Override
	public void deactivate() {
		this.deactivateModule();
	}
	
	@EventHandler
	public void onTickSecond(TickSecondEvent event) {
		if (NetworkPlayer.getOnlinePlayersByState(PlayerState.NORMAL).size() > 1) {
			if (NetworkPlayer.getOnlinePlayersByState(PlayerState.NORMAL).size() == 2) {
				getOrionMap().getWall().forEach(sl -> {
					if (sl.toLocation().getBlock().getType() == Material.GLASS) {
						sl.toLocation().getBlock().setType(Material.AIR);						
					}
				});
			}
			if (getTime() > 0) {
				setTime(getTime() - 1);
				new ActionBar(ChatColor.LIGHT_PURPLE + "Time Remaining: " + ChatColor.RESET + "" + String.format("%02d:%02d", getTime() / 60, getTime() % 60)).sendToAll();				
			} else {
				resetTimer();
			}			
		} else {
			if (NetworkPlayer.getOnlinePlayersByState(PlayerState.NORMAL).size() == 1) {
				NetworkPlayer.getOnlinePlayersByState(PlayerState.NORMAL).forEach(xp -> {
					Bukkit.broadcastMessage(NetworkCore.prefixStandard +  xp.getPlayer().getDisplayName() + " has won the game!");		
					UHCSGOrionGame.getLeaderboardToken().endGame(true, xp.getPlayer().getUniqueId());

				});
			}
			nextSegment();
		}
		
	}
	
	
	@EventHandler
	public void onPlayerMoveOutOfBounds(PlayerMoveEvent event) {
		if ((MathUtil.distance(getOrionMap().getWorldSpawn().toLocation().getX(), event.getTo().getX(), getOrionMap().getWorldSpawn().toLocation().getZ(), event.getTo().getZ())) > getOrionMap().getRadius()) {
			event.setCancelled(true);
		}
	}
}
