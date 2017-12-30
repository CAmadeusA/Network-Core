package com.camadeusa.network;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.ChatColor;

import com.camadeusa.module.Module;
import com.camadeusa.module.game.GamemodeManager;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.utility.xoreboard.XoreBoard;
import com.camadeusa.utility.xoreboard.XoreBoardPlayerSidebar;
import com.camadeusa.utility.xoreboard.XoreBoardUtil;

public class Leaderboard extends Module {
	XoreBoard xb = XoreBoardUtil.getNextXoreBoard();
	String scoreboardTitle = "   " + ChatColor.GRAY + "" + ChatColor.BOLD + "–––– " + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Orion" + ChatColor.GRAY + " ––––" + "   ";
	
	public void sendToPlayer(NetworkPlayer np) {
		HashMap<String, Integer> lines = new HashMap<>();
		xb.addPlayer(np.getPlayer());
		XoreBoardPlayerSidebar xbps = xb.getSidebar(np.getPlayer());
		xbps.setDisplayName(scoreboardTitle);
		
		lines.put((ChatColor.GRAY + ">> " + ChatColor.RESET + "" + PlayerRank.formatTextByRank(np.getPlayerRank(), "You")), 15);
		lines.put(ChatColor.stripColor(np.getPlayer().getDisplayName()), 14);
		lines.put("            ", 13);
		lines.putAll(getCustomLines(np));
		lines.put("             ", 4);
		lines.put(ChatColor.GRAY + ">> " + ChatColor.RESET + "" + "Server", 3);
		lines.put(ChatColor.RED + "" + GamemodeManager.getInstance().getServer(), 2);
		lines.put("              ", 1);
		lines.put(ChatColor.DARK_GRAY + new SimpleDateFormat("hh:mm:ssa yyyy/MM/dd").format(new Date(System.currentTimeMillis())), 0);
		lines.put(ChatColor.YELLOW + "" + ChatColor.BOLD + "OrionMC.net", -1);
		
		xbps.rewriteLines(lines);
		xbps.showSidebar();
		
		
	}
	
	public HashMap<String, Integer> getCustomLines(NetworkPlayer np) {
		return new HashMap<String, Integer>();
	}
	
}
