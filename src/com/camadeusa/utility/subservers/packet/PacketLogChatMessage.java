package com.camadeusa.utility.subservers.packet;

import java.util.UUID;

import org.json.JSONObject;

import net.ME1312.SubServers.Client.Bukkit.Library.Version.Version;
import net.ME1312.SubServers.Client.Bukkit.Network.PacketIn;
import net.ME1312.SubServers.Client.Bukkit.Network.PacketOut;

public class PacketLogChatMessage implements PacketIn, PacketOut{
	String uuid;
	long currentTime;
	String message;
	
	public PacketLogChatMessage() {}
	
	public PacketLogChatMessage(UUID uuid, long time, String message) {
		this.uuid = uuid.toString();
		this.currentTime = time;
		this.message = message;
	}
	
	@Override
	public JSONObject generate() throws Throwable {
		JSONObject json = new JSONObject();
		json.put("uuid", uuid);
		json.put("currentTime", currentTime);
		json.put("message", message);
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
