package com.camadeusa.module.game.uhcsg;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import com.camadeusa.module.game.OrionGame;
import com.camadeusa.module.game.OrionSegment;
import com.camadeusa.module.game.uhcsg.segments.Deathmatch;
import com.camadeusa.module.game.uhcsg.segments.Endgame;
import com.camadeusa.module.game.uhcsg.segments.Livegame;
import com.camadeusa.module.game.uhcsg.segments.Lobby;
import com.camadeusa.module.game.uhcsg.segments.Predeathmatch;
import com.camadeusa.module.game.uhcsg.segments.Pregame;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.timing.TickSecondEvent;
import com.camadeusa.utility.xoreboard.XoreBoard;
import com.camadeusa.utility.xoreboard.XoreBoardPlayerSidebar;
import com.camadeusa.utility.xoreboard.XoreBoardUtil;

public class UHCSGOrionGame extends OrionGame {
	
	XoreBoard xb;
	String scoreboardTitle = ChatColor.GRAY + "" + ChatColor.BOLD + "--- " + ChatColor.LIGHT_PURPLE + "Orion" + ChatColor.GRAY + " ---";
	
	int LOBBYTIME = 120;
	int PREGAMETIME = 30;
	int LIVEGAMETIME = 1200;
	int PREDMTIME = 30;
	int DMTIME = 240;
	int ENDGAMETIME = 30;
	
	private static UHCSGOrionGame instance;
	private OrionSegment currentSegment;
	
	public UHCSGOrionGame() {
	
	}
	
	public void initializeGame() {
		this.activateModule();
		
		xb = XoreBoardUtil.getNextXoreBoard();

		
		Lobby lobby = new Lobby();
		lobby.setTime(LOBBYTIME);
		lobby.setTag("Lobby");
		
		Pregame pregame = new Pregame();
		pregame.setTime(PREGAMETIME);
		pregame.setTag("Pre-game");
		
		Livegame livegame = new Livegame();
		livegame.setTime(LIVEGAMETIME);
		livegame.setTag("Live-game");
		
		Predeathmatch predm = new Predeathmatch();
		predm.setTime(PREDMTIME);
		predm.setTag("Pre-Deathmatch");
		
		Deathmatch dm = new Deathmatch();
		dm.setTime(DMTIME);
		dm.setTag("Deathmatch");
		
		Endgame endgame = new Endgame();
		endgame.setTime(ENDGAMETIME);
		endgame.setTag("Endgame");
		
		lobby.setNextSegment(pregame);
		pregame.setNextSegment(livegame);
		livegame.setNextSegment(predm);
		predm.setNextSegment(dm);
		dm.setNextSegment(endgame);
		endgame.setNextSegment(lobby);
		
		this.addSegment(lobby, pregame, livegame, predm, dm, endgame);
		instance = this;
		
		currentSegment = lobby;
		currentSegment.activate();
		
	}
	
	@EventHandler
	public void onTickSecond(TickSecondEvent event) {
		// Runs timing and segment transitions
		if (currentSegment.getTime() > 0) {
			currentSegment.setTime(currentSegment.getTime() - 1);
		} else {
			currentSegment.resetTimer();
			currentSegment.deactivate();
			currentSegment.getNextSegment().activate();
			currentSegment = currentSegment.getNextSegment();
		}
		
		for (NetworkPlayer np : NetworkPlayer.getNetworkPlayerList()) {
			XoreBoardPlayerSidebar xbps = xb.getSidebar(np.getPlayer());
			xbps.setDisplayName(scoreboardTitle);
			HashMap<String, Integer> lines = new HashMap<>();
			lines.put(ChatColor.GOLD + "Name: ", 20);
			lines.put(StringUtils.abbreviate(PlayerRank.formatNameByRankWOIcon(NetworkPlayer.getNetworkPlayerByUUID(np.getPlayer().getUniqueId().toString())), 40), 19);
			lines.put(" ", 18);
			lines.put(ChatColor.GOLD + "Rank: ", 17);
			lines.put(StringUtils.abbreviate(ChatColor.BLUE + NetworkPlayer.getNetworkPlayerByUUID(np.getPlayer().getUniqueId().toString()).getPlayerRank().toString(), 40), 16);
			lines.put("  ", 15);
			lines.put(currentSegment.getTag() + ": " + currentSegment.getTime(), 14);
			lines.put(ChatColor.GOLD + "orionmc.net", -1);
			xbps.rewriteLines(lines);
			
			xbps.showSidebar();
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		xb.addPlayer(event.getPlayer());
		XoreBoardPlayerSidebar xbps = xb.getSidebar(event.getPlayer());
		xbps.setDisplayName(scoreboardTitle);
		HashMap<String, Integer> lines = new HashMap<>();
		lines.put(ChatColor.GOLD + "Name: ", 20);
		lines.put(StringUtils.abbreviate(PlayerRank.formatNameByRankWOIcon(NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString())), 40), 19);
		lines.put(" ", 18);
		lines.put(ChatColor.GOLD + "Rank: ", 17);
		lines.put(StringUtils.abbreviate(ChatColor.BLUE + NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString()).getPlayerRank().toString(), 40), 16);
		lines.put("  ", 15);
		lines.put(currentSegment.getTag() + ": " + currentSegment.getTime(), 14);
		lines.put(ChatColor.GOLD + "orionmc.net", -1);
		xbps.rewriteLines(lines);
		
		xbps.showSidebar();
	}
	
	public OrionSegment getCurrentSegment() {
		return currentSegment;
	}

	public <T extends OrionSegment> void setCurrentSegment(T currentSegment) {
		this.currentSegment = currentSegment;
	}

	public static UHCSGOrionGame getInstance() {
		return instance;
	}
}
