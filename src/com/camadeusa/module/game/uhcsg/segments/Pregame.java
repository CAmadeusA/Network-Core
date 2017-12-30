package com.camadeusa.module.game.uhcsg.segments;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
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

import com.camadeusa.module.game.GameTime;
import com.camadeusa.module.game.OrionSegment;
import com.camadeusa.module.game.uhcsg.UHCSGOrionGame;
import com.camadeusa.network.ServerMode;
import com.camadeusa.network.ServerMode.ServerJoinMode;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerState;
import com.camadeusa.timing.TickSecondEvent;
import com.camadeusa.utility.MathUtil;
import com.camadeusa.utility.Random;
import com.camadeusa.world.OrionMap;

import io.github.theluca98.textapi.Title;

public class Pregame extends OrionSegment {

	GameTime gt;
	
	@Override
	public void activate() {
		UHCSGOrionGame.getInstance().setCurrentSegment(this);
		this.activateModule();
		
		ServerMode.setMode(ServerJoinMode.STAFF);
		
		if (Lobby.instance.top.size() > 0) {
			setOrionMap(((OrionMap) Lobby.instance.top.keySet().toArray()[Lobby.instance.top.size() - 1]));
		} else {
			setOrionMap((OrionMap) Lobby.instance.votes.keySet().toArray()[Random.instance().nextInt(Lobby.instance.votes.keySet().size())]);
		}
		getOrionMap().getWorld().setDifficulty(Difficulty.PEACEFUL);
		getOrionMap().getWorld().getWorldBorder().setCenter(getOrionMap().getWorldSpawn().toLocation());
		getOrionMap().getWorld().getWorldBorder().setSize(getOrionMap().getRadius() * 2);
		getOrionMap().getWorld().getWorldBorder().setDamageBuffer(0.5);
		
		List<NetworkPlayer> normalList = NetworkPlayer.getOnlinePlayersByState(PlayerState.NORMAL);
		
		for (int i = 0; i < normalList.size(); i++) {
			normalList.get(i).getPlayer().teleport(getOrionMap().getSpawns().get(i).toLocation());
			UHCSGOrionGame.getLeaderboardToken().addPlayer(normalList.get(i));
		}
		for (int i = 0; i < NetworkPlayer.getOnlinePlayersByState(PlayerState.SPECTATOR).size(); i++) {
			NetworkPlayer.getOnlinePlayersByState(PlayerState.SPECTATOR).get(i).getPlayer().teleport(getOrionMap().getWorldSpawn().toLocation());
		}
		for (int i = 0; i < NetworkPlayer.getOnlinePlayersByState(PlayerState.GHOST).size(); i++) {
			NetworkPlayer.getOnlinePlayersByState(PlayerState.GHOST).get(i).getPlayer().teleport(getOrionMap().getWorldSpawn().toLocation());
		}
		
		Bukkit.getOnlinePlayers().forEach(p -> {
			p.getInventory().clear();
		});
		
		getNextSegment().setOrionMap(getOrionMap());

		GameTime.getInstance().setDayLength(480);
		GameTime.getInstance().setNightLength(120);
		GameTime.getInstance().setOrionMap(getOrionMap());
		GameTime.getInstance().setFrozen(false);
		
	}
	
	@Override
	public void deactivate() {
		this.deactivateModule();
	}
	
	@EventHandler
	public void onTickSecond(TickSecondEvent event) {
		if (getTime() > 1) {
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
		if ((MathUtil.distance(event.getPlayer().getLocation().getBlockX() + 0.5, event.getTo().getX(), event.getPlayer().getLocation().getBlockZ() + 0.5, event.getTo().getZ())) >= 0.48) {
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
