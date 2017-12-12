package com.camadeusa.utility.subservers.event;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.camadeusa.NetworkCore;
import com.camadeusa.network.NetworkSettings;

import net.ME1312.SubServers.Client.Bukkit.SubAPI;
import net.ME1312.SubServers.Client.Bukkit.Event.SubNetworkConnectEvent;

public class SubserversEvents implements Listener {
	public static boolean connected = false;
	
	
	@EventHandler
	public void onConnect(SubNetworkConnectEvent event) {
		Bukkit.getScheduler().scheduleAsyncDelayedTask(NetworkCore.getInstance(), new Runnable() {
			@Override
			public void run() {				
				connected = true;
				new NetworkSettings();
			}
		}, 20);
	}
	
}
