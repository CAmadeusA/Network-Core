package com.camadeusa.utility.subservers.packet;

import java.util.HashMap;
import java.util.UUID;

import org.json.JSONObject;

import com.camadeusa.module.game.Gamemode;

import net.ME1312.SubServers.Client.Bukkit.Library.JSONCallback;
import net.ME1312.SubServers.Client.Bukkit.Library.Util;
import net.ME1312.SubServers.Client.Bukkit.Library.Version.Version;
import net.ME1312.SubServers.Client.Bukkit.Network.PacketIn;
import net.ME1312.SubServers.Client.Bukkit.Network.PacketOut;

public class PacketDownloadOrionServerList implements PacketIn, PacketOut {
	private static HashMap<String, JSONCallback[]> callbacks = new HashMap<String, JSONCallback[]>();
	String gameMode;
	int playerRank;
	String id;

	public PacketDownloadOrionServerList() {}
	
	public PacketDownloadOrionServerList(String gameMode, int playerRank, JSONCallback... callback) {
		id = Util.getNew(callbacks.keySet(), UUID::randomUUID).toString();
		this.gameMode = gameMode;
		this.playerRank = playerRank;
		callbacks.put(id, callback);
	}
	
	@Override
	public void execute(JSONObject data) throws Throwable {
		for (JSONCallback callback : callbacks.get(data.getString("id"))) callback.run(data);
        callbacks.remove(data.getString("id"));
	}
	
	@Override
	public JSONObject generate() throws Throwable {
		JSONObject json = new JSONObject();
		json.put("gamemode", gameMode);
		json.put("playerrank", playerRank);
		json.put("id", id);
		return json;
	}

	@Override
	public Version getVersion() {
		return new Version("2.11.0a");

	}


}
