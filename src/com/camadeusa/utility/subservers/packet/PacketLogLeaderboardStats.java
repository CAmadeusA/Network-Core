package com.camadeusa.utility.subservers.packet;

import org.json.JSONObject;

import net.ME1312.SubServers.Client.Bukkit.Library.Version.Version;
import net.ME1312.SubServers.Client.Bukkit.Network.PacketOut;

public class PacketLogLeaderboardStats implements PacketOut{
	String table;
	String uuid;
	JSONObject json;
	
	public PacketLogLeaderboardStats() {}
	
	public PacketLogLeaderboardStats(String table, String uuid, String json) {
		this.table = table;
		this.uuid = uuid;
		this.json = new JSONObject(json);
	}
	
	@Override
	public JSONObject generate() throws Throwable {
		JSONObject json = new JSONObject();
		json.put("id", uuid);
		json.put("table", table);
		json.put("wins", this.json.getInt("wins"));
		json.put("kills", this.json.getInt("kills"));
		json.put("deaths", this.json.getInt("deaths"));
		json.put("games", this.json.getInt("games"));
		json.put("averageKillsPerGame", this.json.getDouble("averageKillsPerGame"));
		json.put("coins", this.json.getInt("coins"));
		json.put("elo", this.json.getInt("elo"));
		json.put("lastelo", this.json.getInt("lastelo"));
		json.put("arrowsFired", this.json.getInt("arrowsFired"));
		json.put("arrowsLanded", this.json.getInt("arrowsLanded"));
		return json;
		
	}

	@Override
	public Version getVersion() {
		return new Version("2.11.0a");
	}

}
