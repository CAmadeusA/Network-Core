package com.camadeusa.network;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.json.JSONObject;

import com.camadeusa.NetworkCore;
import com.camadeusa.utility.subservers.packet.PacketDownloadNetworkSettings;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;

import net.ME1312.SubServers.Client.Bukkit.SubAPI;

public class NetworkSettings {

	HashMap<String, JSONObject> settings = new HashMap<>();

	public NetworkSettings() {

		SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketDownloadNetworkSettings(callback -> {
			for (String key : callback.getJSONObject("data").keySet()) {
				if (!key.equals("id")) 
					settings.put(key, callback.getJSONObject("data").getJSONObject(key));					
				}
			}
		));

		Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
			@Override
			public void run() {
				Connection con = RethinkDB.r.connection().hostname("192.168.1.100").db("Orion_Network")
						.user("admin", "61797Caa").connect();
				con.use("Orion_Network");

				Cursor<JSONObject> cur = RethinkDB.r.db("Orion_Network").table("networksettings").get("settings").changes().run(con);
				for (JSONObject change : cur) {
					if (settings.containsKey(change.getString("id"))) {
						settings.replace(change.getString("id"), change);
					} else {
						settings.put(change.getString("id"), change);
					}
				}

			}
		});
	}
	
	public HashMap<String, JSONObject> getSettings() {
		return settings;
	}
}