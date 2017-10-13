package com.camadeusa.utility.subservers.packet;

import java.util.UUID;

import org.json.JSONObject;

import net.ME1312.SubServers.Client.Bukkit.Library.Version.Version;
import net.ME1312.SubServers.Client.Bukkit.Network.PacketIn;
import net.ME1312.SubServers.Client.Bukkit.Network.PacketOut;

public class PacketPunishPlayer implements PacketIn, PacketOut {

	public enum PunishType {
		BAN("ban"),
		KICK("kick"),
		MUTE("mute");
		
		String value;
		
		PunishType(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}
	
	String uuid;
	PunishType type;
	long length;
	String reason;
	String id;
	String punisheruuid;
	
	public PacketPunishPlayer() {}
	
	public PacketPunishPlayer(String uuid, PunishType type, long length, String reason, String punisheruuid) {
		this.uuid = uuid;
		this.type = type;
		this.length = length;
		this.reason = reason;
		this.punisheruuid = punisheruuid;
		this.id = UUID.randomUUID().toString();
	}
	
	@Override
	public JSONObject generate() throws Throwable {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("uuid", uuid);
		json.put("type", type.getValue());
		json.put("reason", reason);
		json.put("length", length);
		json.put("punisheruuid", punisheruuid);
		
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
