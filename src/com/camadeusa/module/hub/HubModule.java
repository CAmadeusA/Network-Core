package com.camadeusa.module.hub;

import org.bukkit.event.Listener;

import com.camadeusa.module.Module;
import com.camadeusa.module.game.Gamemode;

import net.ME1312.SubServers.Client.Bukkit.SubAPI;

public class HubModule extends Module implements Listener {

	public HubModule() {
		this.setTag(Gamemode.Hub.getValue());
	}

}
