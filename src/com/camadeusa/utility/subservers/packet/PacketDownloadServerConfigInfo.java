package com.camadeusa.utility.subservers.packet;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.json.JSONObject;

import net.ME1312.SubServers.Client.Bukkit.Library.JSONCallback;
import net.ME1312.SubServers.Client.Bukkit.Library.Util;
import net.ME1312.SubServers.Client.Bukkit.Library.Version.Version;
import net.ME1312.SubServers.Client.Bukkit.Network.PacketIn;
import net.ME1312.SubServers.Client.Bukkit.Network.PacketOut;

public class PacketDownloadServerConfigInfo implements PacketIn, PacketOut {
	private static HashMap<String, JSONCallback[]> callbacks = new HashMap<String, JSONCallback[]>();
	private String serverName;
	private String id;

	public PacketDownloadServerConfigInfo() {
	}

	public PacketDownloadServerConfigInfo(String name, JSONCallback... callback) {
		serverName = name;
		id = Util.getNew(callbacks.keySet(), UUID::randomUUID).toString();
		callbacks.put(id, callback);
	}

	@Override
	public JSONObject generate() throws Throwable {
		JSONObject json = new JSONObject();
		json.put("name", serverName);
		json.put("id", id);
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
