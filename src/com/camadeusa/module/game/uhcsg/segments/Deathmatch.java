package com.camadeusa.module.game.uhcsg.segments;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import com.camadeusa.module.game.OrionSegment;
import com.camadeusa.module.game.uhcsg.UHCSGOrionGame;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerState;
import com.camadeusa.timing.TickSecondEvent;
import com.camadeusa.utility.MathUtil;

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
			} else {
				resetTimer();
			}			
		} else {
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
