package com.camadeusa.module.network.event;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.camadeusa.NetworkCore;
import com.camadeusa.timing.TickTenSecondEvent;

public class NetworkServerInfoEvents implements Listener {
	public static int maxplayers = 0;
	@EventHandler
	public void onTickTenSecondEvent(TickTenSecondEvent event) {
		Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
			@Override
			public void run() {
				ArrayList<Map<String, Object>> list = NetworkCore.getInstance().serversDB.queryData("where maxplayers >= 1");
				int tempmaxplayers = 0;
				for (Map<String, Object> r : list) {
					tempmaxplayers = tempmaxplayers + Integer.parseInt(r.get("maxplayers").toString());
				}
				maxplayers = tempmaxplayers;
			}
		});		
	}

	
}
