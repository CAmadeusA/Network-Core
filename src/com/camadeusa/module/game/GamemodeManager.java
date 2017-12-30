package com.camadeusa.module.game;

import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.ModuleManager;
import com.camadeusa.module.game.uhcsg.UHCSGOrionGame;
import com.camadeusa.module.game.uhcsg.UHCSGScoreboard;
import com.camadeusa.module.hub.HubHotbar;
import com.camadeusa.module.hub.HubModule;
import com.camadeusa.module.hub.HubScoreboard;
import com.camadeusa.module.mapeditor.MapEditorModule;

public class GamemodeManager {
	ModuleManager modulemanager;
	private static GamemodeManager instance;
	String server;
	Gamemode gamemode;
	String serveruuid;
	int maxplayers;
	public static int currentplayers;
	
	OrionGame currentGame;

	@SuppressWarnings("deprecation")
	public GamemodeManager() {
		instance = this;
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
			modulemanager.modulesToRegister.add(new HubScoreboard());
			modulemanager.modulesToRegister.add(new HubHotbar());
			break;
		case UHCSG:
			UHCSGOrionGame game = new UHCSGOrionGame();
			game.activateModule();
			game.initializeGame();
			NetworkCore.getInstance().getServer().addRecipe(GoldenHead.getRecipe());
			currentGame = game;
			modulemanager.modulesToRegister.add(new GoldenHead());
			modulemanager.modulesToRegister.add(new GameTime());
			modulemanager.modulesToRegister.add(new UHCSGScoreboard());
			break;
		case MAPEDITOR:
			modulemanager.modulesToRegister.add(new MapEditorModule());
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

	public static GamemodeManager getInstance() {
		return instance;
	}
}
