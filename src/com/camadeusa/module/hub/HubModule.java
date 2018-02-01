package com.camadeusa.module.hub;

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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
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
import com.camadeusa.player.PlayerState;
import com.camadeusa.timing.TickSecondEvent;
import com.camadeusa.utility.MathUtil;
import com.camadeusa.utility.Random;
import com.camadeusa.utility.subservers.packet.PacketUpdateDatabaseValue;
import com.camadeusa.world.OrionMap;
import com.camadeusa.world.WorldManager;

import io.github.theluca98.textapi.Title;
import net.ME1312.SubServers.Client.Bukkit.SubAPI;


public class HubModule extends Module {
	String scoreboardTitle = ChatColor.GRAY + "" + ChatColor.BOLD + "--- " + ChatColor.LIGHT_PURPLE + "Orion" + ChatColor.GRAY + " ---";
	OrionMap hubConfig;
	
	public HubModule() {}
	
	@Override
	public void activateModule() {
		this.setTag(Gamemode.Hub.getValue());
		Bukkit.getLogger().info("Activated");
		
		hubConfig = WorldManager.loadWorld("Hub");
		hubConfig.createWorld();
		
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
		if (!aP.getData().has("requirepwonlogin")) {
			SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(aP.getPlayer().getUniqueId().toString(), "requirepwonlogin", "false"));			
		}
		
		if (aP.getData().has("requirepwonlogin") && (aP.getData().getString("requirepwonlogin").equalsIgnoreCase("true") || aP.getPlayerRank().getValue() >= PlayerRank.Helper.getValue())) {
			aP.getPlayer().chat("/authenticate");
		} else {
			SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(aP.getPlayer().getUniqueId().toString(), "authenticated", "true"));
		}
		if (aP.getPlayer() != null) {
			aP.getPlayer().teleport(hubConfig.getWorldSpawn().add((Random.instance().nextInt(6) - 3) * (Random.instance().nextDouble() + 0.5), 0, (Random.instance().nextInt(6) - 3) * (Random.instance().nextDouble() + 0.5)).toLocation());
			aP.getPlayer().setHealth(20);
			aP.getPlayer().setFoodLevel(20);
			aP.getPlayer().setAllowFlight(true);
			aP.getPlayer().getInventory().clear();
			aP.getPlayer().getActivePotionEffects().forEach(pe -> {
				aP.getPlayer().removePotionEffect(pe.getType());
			});	
		}
		if (aP.getPlayerState() != PlayerState.GHOST) {
			aP.updatePlayerstate(PlayerState.NORMAL);
		}
		
		
	}
	
	@EventHandler
	public void onPlayerMoveOutOfBounds(PlayerMoveEvent event) {
		// 100 is arbitrary hub size. 
		if ((MathUtil.distance(hubConfig.getWorldSpawn().toLocation().getX(), event.getTo().getX(), hubConfig.getWorldSpawn().toLocation().getZ(), event.getTo().getZ())) > hubConfig.getRadius()) {
			event.getPlayer().teleport(hubConfig.getWorldSpawn().toLocation());
			new Title("", ChatColor.DARK_RED + "You cannot go past the hub boundary.", 10, 20, 10).send(event.getPlayer());
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
		hubConfig.getWorldSpawn().toLocation().getWorld().setTime(6000L);
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
	public void onBlockGrow(BlockGrowEvent event) {
		event.setCancelled(true);
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
