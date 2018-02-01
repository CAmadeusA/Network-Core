package com.camadeusa.module.hub;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import com.camadeusa.NetworkCore;
import com.camadeusa.network.Leaderboard;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.timing.TickThreeSecondEvent;

public class HubScoreboard extends Leaderboard {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Bukkit.getScheduler().scheduleAsyncDelayedTask(NetworkCore.getInstance(), new Runnable() {
			@Override
			public void run() {
				sendToPlayer(NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString()));
			}
		}, 5);
	}
	
	@EventHandler
	public void onTickSecond(TickThreeSecondEvent event) {
		NetworkPlayer.getOnlinePlayers().forEach(np -> {
			this.sendToPlayer(np);
		});
	}
	
	@Override
	public HashMap<String, Integer> getCustomLines(NetworkPlayer np) {
		HashMap<String, Integer> lines = new HashMap<>();
		lines.put(ChatColor.GRAY + ">> " + ChatColor.RESET + "" + "Players", 12);
		lines.put(ChatColor.BLUE + "" + NetworkCore.getInstance().getTotalNetworkPlayers(), 11);
		return lines;
	}
	
}
