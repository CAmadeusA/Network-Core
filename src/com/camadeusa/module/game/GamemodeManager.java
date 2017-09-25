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

	@SuppressWarnings("deprecation")
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
	}

	public void activateGametype() {
		switch (gamemode) {
		case Hub:
			HashMap<String, ? extends Module> modules = new HashMap<>();
			modulemanager.modulesToRegister.add(modules.get("hubmodule"));
			break;
		case ArenaPVP:

			break;

		default:
			break;
		}
		getModulemanager().registerModules();
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
