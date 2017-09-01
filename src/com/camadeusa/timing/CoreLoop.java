package com.camadeusa.timing;

import org.bukkit.Bukkit;

import com.camadeusa.NetworkCore;

public class CoreLoop {
	// 50 Ticks per second just to overlap minecraft's 20. 
	private double lastTick = 0;
	public CoreLoop() {}
	public void init() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(NetworkCore.getInstance(), new Runnable() {
			@Override
			public void run() {
				update();
				if (System.currentTimeMillis() > (lastTick + 1000)) {
					Bukkit.getServer().getPluginManager().callEvent(new TickSecondEvent("tick"));
					lastTick = System.currentTimeMillis();
				}
			}
		}, 0, 1); 
	}
	
	// Put tasks here: 
	private void update() {
		
	}
}
