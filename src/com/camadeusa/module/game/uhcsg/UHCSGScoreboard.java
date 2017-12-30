package com.camadeusa.module.game.uhcsg;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import com.camadeusa.NetworkCore;
import com.camadeusa.network.Leaderboard;
import com.camadeusa.network.points.Basepoint;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerState;
import com.camadeusa.timing.TickSecondEvent;

public class UHCSGScoreboard extends Leaderboard {

	HashMap<UUID, Integer> eloLogs = new HashMap<>();
	private static UHCSGScoreboard instance;
	
	public UHCSGScoreboard() {
		instance = this;
	}
	
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
	public void onTickSecond(TickSecondEvent event) {
		NetworkPlayer.getOnlinePlayers().forEach(np -> {
			this.sendToPlayer(np);
		});
	}
	
	@Override
	public HashMap<String, Integer> getCustomLines(NetworkPlayer np) {
		HashMap<String, Integer> lines = new HashMap<>();
		lines.put(ChatColor.GRAY + ">> " + ChatColor.RESET + "" + "Players", 12);
		lines.put(ChatColor.BLUE + "" + NetworkPlayer.getOnlinePlayersByState(PlayerState.NORMAL).size() + "", 11);
		lines.put("", 10);
		lines.put(ChatColor.GRAY + ">> " + ChatColor.RESET + "" + "Rank: " + getRank(np), 9);
		lines.put(" ", 8);
		lines.put(ChatColor.GRAY + ">> " + ChatColor.RESET + "" + "Coins: " + ChatColor.GOLD + (np.getData().has("uhcsgStats") ? np.getData().getJSONObject("uhcsgStats").getInt("coins") + "": 0 + ""), 7);		
		
		return lines;
	}
	
	public String getRank(NetworkPlayer np) {
		if (np.getData().has("uhcsgStats")) {
			int games = np.getData().getJSONObject("uhcsgStats").getInt("games");
			if (games <= 10) {
				return ChatColor.DARK_GRAY + "Placement: " + ChatColor.RESET + "" + games;
			} else {
				int elo = Basepoint.fromString(np.getData().getJSONObject("uhcsgStats").getString("elo")).getElo();
				if (eloLogs.containsKey(np.getPlayer().getUniqueId())) {
					elo = eloLogs.get(np.getPlayer().getUniqueId());
				} else {
					eloLogs.put(np.getPlayer().getUniqueId(), elo);
				}
				if (elo <= 500) {
					return ChatColor.DARK_GREEN + "Bronze " + ChatColor.GOLD + "(" + elo + ")";
				} else if (elo > 500 && elo <= 1000) {
					return ChatColor.GRAY + "Silver " + ChatColor.GOLD + "(" + elo + ")";
				} else if (elo > 1000 && elo <= 1500) {
					return ChatColor.GOLD + "Gold " + ChatColor.GOLD + "(" + elo + ")";
				} else if (elo > 1500 && elo <= 2000) {
					return ChatColor.DARK_AQUA + "Diamond " + ChatColor.GOLD + "(" + elo + ")";
				} else if (elo > 2000 && elo <= 2500) {
					return ChatColor.AQUA + "Platinum " + ChatColor.GOLD + "(" + elo + ")";
				} else if (elo > 2500 && elo <= 3000) {
					return ChatColor.RED + "Master " + ChatColor.GOLD + "(" + elo + ")";
				} else if (elo > 3000) {
					return ChatColor.DARK_RED + "Elite " + ChatColor.GOLD + "(" + elo + ")";
				}
			}
		} else {
			return ChatColor.DARK_GRAY + "Placement:" + ChatColor.RESET + " 0";
		}
		return "";
	}
	
	public void resetEloLog() {
		eloLogs.clear();
	}
	
	public void removeFromLog(UUID uuid) {
		eloLogs.remove(uuid);
	}
	
	public static UHCSGScoreboard getInstance() {
		return instance;
	}
}
