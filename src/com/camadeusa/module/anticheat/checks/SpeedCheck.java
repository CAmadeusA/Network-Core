package com.camadeusa.module.anticheat.checks;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import com.camadeusa.module.anticheat.Check;
import com.camadeusa.module.anticheat.CheckType;
import com.camadeusa.timing.TickSecondEvent;
import com.camadeusa.utility.MathUtil;

public class SpeedCheck extends Check {

	HashMap<UUID, Double> distList = new HashMap<>();
	
	public SpeedCheck() {
		this.setCheckType(CheckType.SPEED);		
	}
	
	@EventHandler
	public void onTickSecond(TickSecondEvent event) {
		distList.keySet().forEach(uuid -> {
			Player p = Bukkit.getPlayer(uuid);
			
			p = null;
		});
		distList.clear();
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		if (distList.containsKey(uuid)) {
			double current = distList.get(uuid);
			distList.put(uuid, current + (MathUtil.distance(event.getFrom().getX(), event.getTo().getX(), event.getFrom().getZ(), event.getTo().getZ())));
			
		} else {
			distList.put(uuid, MathUtil.distance(event.getFrom().getX(), event.getTo().getX(), event.getFrom().getZ(), event.getTo().getZ()));
		}
	}
}
