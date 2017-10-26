package com.camadeusa.module.hub;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.Module;
import com.camadeusa.module.game.Gamemode;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.timing.TickSecondEvent;
import com.camadeusa.utility.MathUtil;
import com.camadeusa.utility.subservers.packet.PacketUpdateDatabaseValue;
import com.camadeusa.utility.xoreboard.XoreBoard;
import com.camadeusa.utility.xoreboard.XoreBoardPlayerSidebar;
import com.camadeusa.utility.xoreboard.XoreBoardUtil;

import net.ME1312.SubServers.Client.Bukkit.SubAPI;


public class HubModule extends Module implements Listener {
	XoreBoard xb;
	String scoreboardTitle = ChatColor.GRAY + "" + ChatColor.BOLD + " ---- " + ChatColor.LIGHT_PURPLE + "Orion" + ChatColor.GRAY + " ---- ";
	
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
		NetworkPlayer aP = NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString());
		event.setJoinMessage("");
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
		lines.put(ChatColor.GOLD + "https://orionmc.net", -1);
		xbps.rewriteLines(lines);
		
		xbps.showSidebar();
		
		if (!aP.getData().has("requirepwonlogin")) {
			SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(aP.getPlayer().getUniqueId().toString(), "requirepwonlogin", "false"));			
		}
		
		if (aP.getData().has("requirepwonlogin") && (aP.getData().getString("requirepwonlogin").equalsIgnoreCase("true") || aP.getPlayerRank().getValue() >= PlayerRank.Helper.getValue())) {
			aP.getPlayer().chat("/authenticate");
		} else {
			SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(aP.getPlayer().getUniqueId().toString(), "authenticated", "true"));
		}
		
	}
	
	@EventHandler
	public void onPlayerMoveOutOfBounds(PlayerMoveEvent event) {
		// 250 is arbitrary hub size. 
		/*World w = event.getPlayer().getWorld();
		if ((MathUtil.distance(w.getSpawnLocation().getX(), event.getTo().getX(), w.getSpawnLocation().getZ(), event.getTo().getZ())) > 250) {
			event.setCancelled(true);
		}*/
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
	public void tickSecondSetTime(TickSecondEvent event) {
		for (World w : Bukkit.getWorlds()) {
			w.setTime(6000L);
		}
	}
	
	@EventHandler
	public void onWeatherChangge(WeatherChangeEvent event) {
		if (event.toWeatherState()) {
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
	public void onLeave(PlayerQuitEvent event) {
		event.setQuitMessage("");
	}
	@EventHandler
	public void onLeave(PlayerKickEvent event) {
		event.setLeaveMessage("");
	}

}
