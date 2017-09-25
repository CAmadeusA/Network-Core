package com.camadeusa.utility.subservers.packet;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.json.JSONObject;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.game.GamemodeManager;
import com.camadeusa.utility.Random;

import net.ME1312.SubServers.Client.Bukkit.Library.JSONCallback;
import net.ME1312.SubServers.Client.Bukkit.Library.Util;
import net.ME1312.SubServers.Client.Bukkit.Library.Version.Version;
import net.ME1312.SubServers.Client.Bukkit.Network.PacketIn;
import net.ME1312.SubServers.Client.Bukkit.Network.PacketOut;

public class PacketGetServerConfigInfo implements PacketIn, PacketOut {
	private String server;
	private String id;
	
	public PacketGetServerConfigInfo() {}

	public PacketGetServerConfigInfo(String server, JSONCallback... callback) {
		if (Util.isNull(server, callback)) throw new NullPointerException();
        this.server = server;
        this.id = UUID.randomUUID().toString();
	}

	@Override
	public JSONObject generate() throws Throwable {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("server", server);
		json.put("maxplayers", NetworkCore.getConfigManger().getConfig("server", NetworkCore.getInstance()).getInt("maxplayers"));
		json.put("serveruuid", NetworkCore.getConfigManger().getConfig("server", NetworkCore.getInstance()).getString("uuid"));
		json.put("gamemode", NetworkCore.getConfigManger().getConfig("server", NetworkCore.getInstance()).getString("gamemode"));
		json.put("onlineplayers", GamemodeManager.currentplayers);
		json.put("timestamp", System.currentTimeMillis());
		return json;
	}

	@Override
	public Version getVersion() {
		return new Version("2.11.0a");
	}

	@Override
	public void execute(JSONObject data) throws Throwable {
	
	}
	
	

}
