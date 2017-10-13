package com.camadeusa.utility.subservers.packet;

import java.util.HashMap;
import java.util.UUID;

import org.json.JSONObject;

import net.ME1312.SubServers.Client.Bukkit.Library.JSONCallback;
import net.ME1312.SubServers.Client.Bukkit.Library.Util;
import net.ME1312.SubServers.Client.Bukkit.Library.Version.Version;
import net.ME1312.SubServers.Client.Bukkit.Network.PacketIn;
import net.ME1312.SubServers.Client.Bukkit.Network.PacketOut;

public class PacketDownloadPlayerInfo implements PacketIn, PacketOut {
	private static HashMap<String, JSONCallback[]> callbacks = new HashMap<String, JSONCallback[]>();
	private String playerUUID;
	private String playerName;
	private String playerIP;
	private String id;
	
	public PacketDownloadPlayerInfo() {}
	
	public PacketDownloadPlayerInfo(String uuid, String playerName, String playerIP, JSONCallback... callback) {
		playerUUID = uuid;
		this.playerName = playerName;
		id = Util.getNew(callbacks.keySet(), UUID::randomUUID).toString();
		this.playerIP = playerIP;
		callbacks.put(id, callback);
	}

	@Override
	public JSONObject generate() throws Throwable {
		JSONObject json = new JSONObject();
		json.put("uuid", playerUUID);
		json.put("id", id);
		json.put("name", playerName);
		json.put("ip", playerIP);
		return json;

	}

	@Override
	public void execute(JSONObject data) throws Throwable {
		for (JSONCallback callback : callbacks.get(data.getString("id"))) callback.run(data);
        callbacks.remove(data.getString("id"));
	}

	@Override
	public Version getVersion() {
		return new Version("2.11.0a");
	}
	
	
}
