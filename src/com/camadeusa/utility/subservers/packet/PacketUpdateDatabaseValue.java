package com.camadeusa.utility.subservers.packet;

import java.util.UUID;

import org.json.JSONObject;

import net.ME1312.SubServers.Client.Bukkit.Library.JSONCallback;
import net.ME1312.SubServers.Client.Bukkit.Library.Version.Version;
import net.ME1312.SubServers.Client.Bukkit.Network.PacketIn;
import net.ME1312.SubServers.Client.Bukkit.Network.PacketOut;

public class PacketUpdateDatabaseValue implements PacketIn, PacketOut {
	String uuid;
	String key;
	String value;
	String id;
	
	public PacketUpdateDatabaseValue() {}
	
	public PacketUpdateDatabaseValue(String uuid, String key, String value) {
		this.uuid = uuid;
		this.key = key;
		this.value = value;
		this.id = UUID.randomUUID().toString();
	}

	@Override
	public JSONObject generate() throws Throwable {
		JSONObject json = new JSONObject();
		json.put("key", this.key);
		json.put("value", this.value);
		json.put("uuid", this.uuid);
		json.put("id", this.id);
		
		return json;
	}
	
	@Override
	public void execute(JSONObject arg0) throws Throwable {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Version getVersion() {
		return new Version("2.11.0a");
	}


}
