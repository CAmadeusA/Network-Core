package com.camadeusa.module.game.usg.segments;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import com.camadeusa.module.game.usg.USGOrionGame;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerState;
import com.camadeusa.timing.TickSecondEvent;
import com.camadeusa.utility.MathUtil;

import io.github.theluca98.textapi.Title;

public class Predeathmatch extends OrionSegment {
	@Override
	public void activate() {
		USGOrionGame.getInstance().setCurrentSegment(this);
		this.activateModule();
		
		for (int i = 0; i < NetworkPlayer.getOnlinePlayersByState(PlayerState.NORMAL).size(); i++) {
			NetworkPlayer.getOnlinePlayersByState(PlayerState.NORMAL).get(i).getPlayer().teleport(getOrionMap().getDeathmatchSpawns().get(i).toLocation());
		}
		for (int i = 0; i < NetworkPlayer.getOnlinePlayersByState(PlayerState.SPECTATOR).size(); i++) {
			NetworkPlayer.getOnlinePlayersByState(PlayerState.SPECTATOR).get(i).getPlayer().teleport(getOrionMap().getDeathmatchSpawn().toLocation());
		}
		for (int i = 0; i < NetworkPlayer.getOnlinePlayersByState(PlayerState.GHOST).size(); i++) {
			NetworkPlayer.getOnlinePlayersByState(PlayerState.GHOST).get(i).getPlayer().teleport(getOrionMap().getDeathmatchSpawn().toLocation());
		}
		
		if (NetworkPlayer.getOnlinePlayersByState(PlayerState.NORMAL).size() > 2) {			
			getOrionMap().getWall().forEach(softLocation -> {
				if (!softLocation.toLocation().getBlock().getType().isSolid()) {
					softLocation.toLocation().getBlock().setType(Material.GLASS);				
				}
			});
		}
		
		getNextSegment().setOrionMap(getOrionMap());
		
	}
	
	@Override
	public void deactivate() {
		this.deactivateModule();
	}
	
	@EventHandler
	public void onTickSecond(TickSecondEvent event) {
		if (getTime() > 0) {
			setTime(getTime() - 1);
			new Title(ChatColor.DARK_RED + "" + getTime(), "", 5, 10, 5).sendToAll();
			NetworkPlayer.getOnlinePlayers().forEach(np -> {
				np.getPlayer().playSound(np.getPlayer().getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 1f);
			});
		} else {
			nextSegment();
			NetworkPlayer.getOnlinePlayers().forEach(np -> {
				np.getPlayer().playSound(np.getPlayer().getLocation(), Sound.BLOCK_NOTE_SNARE, 1f, 1f);
			});
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
		if ((MathUtil.distance(event.getPlayer().getLocation().getBlockX() + 0.5, event.getTo().getX(), event.getPlayer().getLocation().getBlockZ() + 0.5, event.getTo().getZ())) > 0.5) {
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
