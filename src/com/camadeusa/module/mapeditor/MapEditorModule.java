package com.camadeusa.module.mapeditor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import com.camadeusa.NetworkCore;
import com.camadeusa.chat.ChatManager;
import com.camadeusa.module.Module;
import com.camadeusa.module.game.Gamemode;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.utility.subservers.packet.PacketDownloadPlayerInfo;

import net.ME1312.SubServers.Client.Bukkit.SubAPI;

public class MapEditorModule extends Module {

	@Override
	public void activateModule() {
		this.setTag(Gamemode.MAPEDITOR.getValue());
		Bukkit.getLogger().info("Activated");
		
		super.activateModule();
	}
	
	@EventHandler
	public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
		AtomicInteger intA = new AtomicInteger();
		intA.set(1);
		Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
			@Override 
			public void run() {
				SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketDownloadPlayerInfo(event.getUniqueId().toString(), event.getName(), "-1", jsoninfo -> {
					if (PlayerRank.fromString(jsoninfo.getJSONObject("data").getString("rank")).getValue() < PlayerRank.Admin.getValue()) {
						event.disallow(Result.KICK_BANNED, NetworkCore.prefixStandard + ChatManager.translateFor("en", jsoninfo.getJSONObject("data").getString("locale"), "You do not have permission to be on this server."));
					}

				}));				
			}
		});
	}
}
