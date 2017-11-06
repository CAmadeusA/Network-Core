package com.camadeusa.utility.subservers.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.camadeusa.network.NetworkSettings;

import net.ME1312.SubServers.Client.Bukkit.Event.SubNetworkConnectEvent;

public class SubserversEvents implements Listener {
	public static boolean connected = false;
	
	
	@EventHandler
	public void onConnect(SubNetworkConnectEvent event) {
		connected = true;
		new NetworkSettings();
	}
	
}
