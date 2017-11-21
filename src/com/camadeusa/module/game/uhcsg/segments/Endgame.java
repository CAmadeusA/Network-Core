package com.camadeusa.module.game.uhcsg.segments;

import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.camadeusa.module.game.OrionSegment;
import com.camadeusa.module.game.uhcsg.UHCSGOrionGame;
import com.camadeusa.timing.TickSecondEvent;
import com.camadeusa.utility.MathUtil;

public class Endgame extends OrionSegment {
	@Override
	public void activate() {
		UHCSGOrionGame.getInstance().setCurrentSegment(this);
		this.activateModule();
		
		Bukkit.getOnlinePlayers().forEach(p -> {
			p.getInventory().clear();
		});
		

		
	}
	
	@Override
	public void deactivate() {
		this.deactivateModule();
		Bukkit.getOnlinePlayers().forEach(p -> {
			p.chat("/hub");
		});
		Lobby.votes = new LinkedHashMap<>();
		Lobby.top = new LinkedHashMap<>();
	}
	
	@EventHandler
	public void onTickSecond(TickSecondEvent event) {
		if (getTime() > 0) {
			setTime(getTime() - 1);
		} else {
			nextSegment();
		}
	}
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPickupItem(EntityPickupItemEvent event) {
		event.setCancelled(true);		
	}
	
	@EventHandler
	public void onPlayerFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerMoveOutOfBounds(PlayerMoveEvent event) {
		if ((MathUtil.distance(getOrionMap().getWorldSpawn().toLocation().getX(), event.getTo().getX(), getOrionMap().getWorldSpawn().toLocation().getZ(), event.getTo().getZ())) > getOrionMap().getRadius()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}
}
