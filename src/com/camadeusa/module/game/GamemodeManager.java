package com.camadeusa.module.game;

import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.Module;
import com.camadeusa.module.ModuleManager;
import com.camadeusa.module.game.uhcsg.UHCSGOrionGame;
import com.camadeusa.module.hub.HubModule;

public class GamemodeManager {
	ModuleManager modulemanager;
	String server;
	Gamemode gamemode;
	String serveruuid;
	int maxplayers;
	public static int currentplayers;
	
	OrionGame currentGame;

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
			modulemanager.modulesToRegister.add(new HubModule());
			break;
		case UHCSG:
			UHCSGOrionGame game = new UHCSGOrionGame();
			game.initializeGame();
			currentGame = game;
			break;
		default:
			break;
		}
		if (modulemanager.modulesToRegister.size() > 0) {
			modulemanager.registerModules();
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
