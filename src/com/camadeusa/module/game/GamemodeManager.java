package com.camadeusa.module.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.Module;
import com.camadeusa.module.network.ModuleManager;
import com.google.gdata.data.spreadsheet.ListEntry;

import net.md_5.bungee.api.ProxyServer;

public class GamemodeManager {
	ModuleManager modulemanager;
	String server;
	Gamemode gamemode;
	String serveruuid;
	int maxplayers;
	public static int currentplayers;

	public GamemodeManager() {
		modulemanager = new ModuleManager();
		if (NetworkCore.getConfigManger().getConfig("server", NetworkCore.getInstance()) != null) {
			FileConfiguration serverconfig = NetworkCore.getConfigManger().getConfig("server",
					NetworkCore.getInstance());
			server = serverconfig.getString("server");
			gamemode = Gamemode.valueof(serverconfig.getString("gamemode"));
			serveruuid = serverconfig.getString("uuid");
			maxplayers = serverconfig.getInt("maxplayers");

		} else {
			FileConfiguration serverconfig;
			try {
				serverconfig = NetworkCore.getConfigManger().createConfig("server", NetworkCore.getInstance());
				serverconfig.set("server", "UNKNOWN");
				server = "UNKNOWN";
				serverconfig.set("gamemode", Gamemode.Hub.getValue());
				gamemode = Gamemode.Hub;
				serverconfig.set("maxplayers", 200);
				maxplayers = 200;
				UUID uuid = UUID.randomUUID();
				serverconfig.set("uuid", uuid.toString());
				serveruuid = uuid.toString();
				NetworkCore.getConfigManger().saveConfig(serverconfig, "server", NetworkCore.getInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Logging servers to DB.
		Bukkit.getScheduler().scheduleAsyncDelayedTask(NetworkCore.getInstance(), new Runnable() {
			@Override
			public void run() {
				try {
					ListEntry row = NetworkCore.getInstance().serversDB.getRow("uuid", serveruuid);
					Map<String, Object> data = NetworkCore.getInstance().serversDB.getRowData(row);
					boolean changed = false;
					if (!data.get("uuid").toString().equals(serveruuid)) {
						data.put("uuid", serveruuid);
						changed = true;
					}
					if (!data.get("server").toString().equals(server)) {
						data.put("server", server);
						changed = true;
					}
					if (!data.get("gamemode").toString().equals(gamemode.getValue())) {
						data.put("gamemode", gamemode.getValue());
						changed = true;
					}
					if (Integer.parseInt(data.get("maxplayers").toString()) != maxplayers) {
						data.put("maxplayers", maxplayers);
					}
					
					NetworkCore.getInstance().serversDB.updateRow(row, data);
					row.update();
					
				} catch (Exception e) {
					Map<String, Object> data = new HashMap<>();
					data.put("uuid", serveruuid);
					data.put("server", server);
					data.put("gamemode", gamemode.getValue());
					data.put("maxplayers", maxplayers);
					
					NetworkCore.getInstance().serversDB.addData(data);
				}
			}
		}, 20);
		
		
	}

	public void activateGametype() {
		switch (gamemode) {
		case Hub:
			HashMap<String, ? extends Module> modules = new HashMap<>();
			modulemanager.modulesToRegister.add(modules.get("hubmodule"));
			getModulemanager().registerModules();
			break;
		case ArenaPVP:

			break;

		default:
			break;
		}
	}

	public ModuleManager getModulemanager() {
		return modulemanager;
	}

	public String getServer() {
		return server;
	}

	public Gamemode getGamemode() {
		return gamemode;
	}

}
