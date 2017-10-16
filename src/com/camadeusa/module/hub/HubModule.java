package com.camadeusa.module.hub;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.Module;
import com.camadeusa.module.game.Gamemode;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.utility.xoreboard.XoreBoard;
import com.camadeusa.utility.xoreboard.XoreBoardPlayerSidebar;
import com.camadeusa.utility.xoreboard.XoreBoardUtil;


public class HubModule extends Module implements Listener {
	XoreBoard xb;
	
	public HubModule() {}
	
	@Override
	public void activateModule() {
		this.setTag(Gamemode.Hub.getValue());
		Bukkit.getLogger().info("Activated");
		xb = XoreBoardUtil.getNextXoreBoard();
		Bukkit.getLogger().info(xb.getBukkitScoreboard().toString());
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent event) {
		xb.addPlayer(event.getPlayer());
		XoreBoardPlayerSidebar xbps = xb.getSidebar(event.getPlayer());
		xbps.setDisplayName(NetworkCore.prefixStandard);
		HashMap<String, Integer> lines = new HashMap<>();
		lines.put(ChatColor.GOLD + "Name: ", 20);
		lines.put(StringUtils.abbreviate(PlayerRank.formatNameByRankWOIcon(NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString())), 40), 19);
		lines.put(" ", 18);
		lines.put(ChatColor.GOLD + "Rank: ", 17);
		lines.put(StringUtils.abbreviate(ChatColor.BLUE + NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString()).getPlayerRank().toString(), 40), 16);
		lines.put("  ", 15);
		lines.put(ChatColor.GOLD + "https://orionmc.net", -1);
		xbps.rewriteLines(lines);
		
		xbps.showSidebar();
		
	}

}
