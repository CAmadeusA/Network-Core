package com.camadeusa.module.game.uhcsg.segments;

import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.camadeusa.NetworkCore;
import com.camadeusa.chat.ChatManager;
import com.camadeusa.module.game.GameTime;
import com.camadeusa.module.game.OrionSegment;
import com.camadeusa.module.game.uhcsg.UHCSGOrionGame;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerState;
import com.camadeusa.timing.TickSecondEvent;
import com.camadeusa.utility.MathUtil;
import com.camadeusa.world.WorldManager;

public class Endgame extends OrionSegment {
	@Override
	public void activate() {
		UHCSGOrionGame.getInstance().setCurrentSegment(this);
		this.activateModule();
		
		Bukkit.getOnlinePlayers().forEach(p -> {
			p.getInventory().clear();
		});
		
		GameTime.getInstance().setFrozen(true);
		
	}
	
	@Override
	public void deactivate() {
		this.deactivateModule();
	
		for (World w : Bukkit.getWorlds()) {
			if (!w.getName().contains("world")) {
				WorldManager.unloadWorld(w.getName(), false);
				Bukkit.unloadWorld(w, false);
			}
		}
		Lobby.votes = new LinkedHashMap<>();
		Lobby.top = new LinkedHashMap<>();
		UHCSGOrionGame.getInstance().chests = new LinkedHashMap<>();
		UHCSGOrionGame.getInstance().loadMaps();
	}
	
	@EventHandler
	public void onTickSecond(TickSecondEvent event) {
		if (getTime() > 0) {
			if (getTime() == 5) {
				Bukkit.getOnlinePlayers().forEach(p -> {
					p.chat("/hub");
				});
			}
			if (getTime() == 1) {
				Bukkit.getOnlinePlayers().forEach(p -> {
					p.kickPlayer(NetworkCore.prefixStandard + ChatManager.translateFor("en", NetworkPlayer.getNetworkPlayerByUUID(p.getUniqueId().toString()), "The game has ended, and you were unable to be sent to the Hub."));
				});				
			}
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
