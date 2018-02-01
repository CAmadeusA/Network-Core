package com.camadeusa.module.game.usg.segments;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import com.camadeusa.module.game.OrionSegment;
import com.camadeusa.module.game.usg.USGCommands;
import com.camadeusa.module.game.usg.USGOrionGame;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerState;
import com.camadeusa.timing.TickSecondEvent;
import com.camadeusa.utility.MathUtil;

import io.github.theluca98.textapi.ActionBar;
import io.github.theluca98.textapi.Title;

public class Livegame extends OrionSegment {
	@Override
	public void activate() {
		USGOrionGame.getInstance().setCurrentSegment(this);
		this.activateModule();		
		getNextSegment().setOrionMap(getOrionMap());
		
	}
	
	@Override
	public void deactivate() {
		this.deactivateModule();
	}
	
	@EventHandler
	public void onTickSecond(TickSecondEvent event) {
		if (NetworkPlayer.getOnlinePlayersByState(PlayerState.NORMAL).size() <= 4 && getTime() > 30 && !USGCommands.debugList.contains("time")) {
			setTime(30);
		}
		
		if (getTime() > 1) {
			new ActionBar(ChatColor.LIGHT_PURPLE + "Time Remaining: " + ChatColor.RESET + "" + String.format("%02d:%02d", getTime() / 60, getTime() % 60)).sendToAll();				
			setTime(getTime() - 1);
		} else {
			nextSegment();
			NetworkPlayer.getOnlinePlayers().forEach(np -> {
				np.getPlayer().playSound(np.getPlayer().getLocation(), Sound.BLOCK_NOTE_SNARE, 1f, 1f);
			});
		}
	}
	
	
	@EventHandler
	public void onPlayerMoveOutOfBounds(PlayerMoveEvent event) {
		if ((MathUtil.distance(getOrionMap().getWorldSpawn().toLocation().getX(), event.getTo().getX(), getOrionMap().getWorldSpawn().toLocation().getZ(), event.getTo().getZ())) > getOrionMap().getRadius()) {
			event.setCancelled(true);
			new Title("", ChatColor.DARK_RED + "You cannot go past the hub boundary.", 10, 20, 10).send(event.getPlayer());

		}
	}
}
