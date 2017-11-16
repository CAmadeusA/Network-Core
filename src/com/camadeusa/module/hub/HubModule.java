package com.camadeusa.module.hub;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
import com.camadeusa.world.WorldManager;

import net.ME1312.SubServers.Client.Bukkit.SubAPI;


public class HubModule extends Module {
	XoreBoard xb;
	String scoreboardTitle = ChatColor.GRAY + "" + ChatColor.BOLD + "--- " + ChatColor.LIGHT_PURPLE + "Orion" + ChatColor.GRAY + " ---";
	
	public HubModule() {}
	
	@Override
	public void activateModule() {
		this.setTag(Gamemode.Hub.getValue());
		Bukkit.getLogger().info("Activated");
		xb = XoreBoardUtil.getNextXoreBoard();
		Bukkit.getLogger().info(xb.getBukkitScoreboard().toString());
		
		WorldManager.loadWorld("Hub");
		
		Bukkit.getWorld("Hub").setDifficulty(Difficulty.PEACEFUL);
		
		for (Entity e : Bukkit.getWorld("Hub").getEntities()) {
			if ((e.getType() != EntityType.PLAYER) && e instanceof LivingEntity) {
				e.remove();
			}
		}
		super.activateModule();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent event) {
		NetworkPlayer aP = NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString());
		event.setJoinMessage("");
		aP.getPlayer().teleport(Bukkit.getWorld("Hub").getSpawnLocation());
		aP.getPlayer().setHealth(20);
		aP.getPlayer().setFoodLevel(20);
		aP.getPlayer().setAllowFlight(true);
		aP.getPlayer().getInventory().clear();
		aP.getPlayer().getActivePotionEffects().forEach(pe -> {
			aP.getPlayer().removePotionEffect(pe.getType());
		});
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
		lines.put(ChatColor.GOLD + "orionmc.net", -1);
		xbps.rewriteLines(lines);
		
		xbps.showSidebar();
		Bukkit.getScheduler().scheduleAsyncDelayedTask(NetworkCore.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (!aP.getData().has("requirepwonlogin")) {
					SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(aP.getPlayer().getUniqueId().toString(), "requirepwonlogin", "false"));			
				}
				
				if (aP.getData().has("requirepwonlogin") && (aP.getData().getString("requirepwonlogin").equalsIgnoreCase("true") || aP.getPlayerRank().getValue() >= PlayerRank.Helper.getValue())) {
					aP.getPlayer().chat("/authenticate");
				} else {
					SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(aP.getPlayer().getUniqueId().toString(), "authenticated", "true"));
				}
				
			}
		}, 7);
		
	}
	
	@EventHandler
	public void onPlayerMoveOutOfBounds(PlayerMoveEvent event) {
		// 100 is arbitrary hub size. 
		World w = event.getPlayer().getWorld();
		if ((MathUtil.distance(w.getSpawnLocation().getX(), event.getTo().getX(), w.getSpawnLocation().getZ(), event.getTo().getZ())) > 100) {
			event.setCancelled(true);
		}
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
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
    public void FrameRotate(PlayerInteractEntityEvent e) {
        if (e.getRightClicked().getType().equals(EntityType.ITEM_FRAME)) {
            if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                e.setCancelled(true);
            }
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
