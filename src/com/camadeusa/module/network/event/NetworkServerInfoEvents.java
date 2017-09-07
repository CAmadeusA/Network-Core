package com.camadeusa.module.network.event;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.game.GamemodeManager;
import com.camadeusa.player.ArchrPlayer;
import com.camadeusa.timing.TickSecondEvent;
import com.camadeusa.timing.TickThreeSecondEvent;
import com.google.gdata.data.spreadsheet.ListEntry;

public class NetworkServerInfoEvents implements Listener {
	@EventHandler
	public void onTickThreeSecondEvent(TickThreeSecondEvent event) {
		GamemodeManager.currentplayers = ArchrPlayer.getOnlinePlayers();
		Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
			@Override
			public void run() {
				try {
					ListEntry row = NetworkCore.getInstance().serversDB.getRow("uuid",
							NetworkCore.getConfigManger().getConfig("server", NetworkCore.getInstance()).getString("uuid"));
					Map<String, Object> data = NetworkCore.getInstance().serversDB.getRowData(row);
					if (Integer.parseInt(data.get("onlineplayers").toString()) != GamemodeManager.currentplayers) {
						data.put("onlineplayers", GamemodeManager.currentplayers);
						NetworkCore.getInstance().serversDB.updateRow(row, data);
						row.update();
					}
					if (Boolean.parseBoolean(data.get("serveronline").toString()) == false) {
						data.put("serveronline", true);
						NetworkCore.getInstance().serversDB.updateRow(row, data);
						row.update();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@EventHandler
	public void onTickSecond(TickSecondEvent event) {
		ArchrPlayer.correctArchrPlayerList();
	}

}
