package com.camadeusa.timing;

import org.bukkit.Bukkit;

import com.camadeusa.NetworkCore;
import com.camadeusa.player.NetworkPlayer;

public class CoreLoop {
	private double lastQuarterTick = 0;
	private double lastTick = 0;
	private double lastThreeTick = 0;
	private double lastTenTick = 0;
	public CoreLoop() {}
	public void init() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(NetworkCore.getInstance(), new Runnable() {
			@Override
			public void run() {
				update();
				if (System.currentTimeMillis() > (lastQuarterTick + 250)) {
					Bukkit.getServer().getPluginManager().callEvent(new TickQuarterSecondEvent("tick"));
					lastQuarterTick = System.currentTimeMillis();
				}
				if (System.currentTimeMillis() > (lastTick + 1000)) {
					Bukkit.getServer().getPluginManager().callEvent(new TickSecondEvent("tick"));
					lastTick = System.currentTimeMillis();
				}
				if (System.currentTimeMillis() > (lastThreeTick + 3000)) {
					Bukkit.getServer().getPluginManager().callEvent(new TickThreeSecondEvent("tick"));
					lastThreeTick = System.currentTimeMillis();
				}
				if (System.currentTimeMillis() > (lastTenTick + 10000)) {
					Bukkit.getServer().getPluginManager().callEvent(new TickTenSecondEvent("tick"));
					lastTenTick = System.currentTimeMillis();
				}
			}
		}, 0, 1); 
	}
	
	// Put tasks here: 
	private void update() {
		NetworkPlayer.correctArchrPlayerList();
	}
}
