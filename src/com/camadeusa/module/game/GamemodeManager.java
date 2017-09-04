package com.camadeusa.module.game;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.Module;
import com.camadeusa.module.network.ModuleManager;

public class GamemodeManager {
	ModuleManager modulemanager;
	String server;
	Gamemode gamemode;
	
	public GamemodeManager() throws Exception {
		modulemanager = new ModuleManager();
		if (NetworkCore.getConfigManger().getConfig("server.yml", NetworkCore.getInstance()) != null) {
			FileConfiguration serverconfig = NetworkCore.getConfigManger().getConfig("server.yml", NetworkCore.getInstance());
			server = serverconfig.getString("server");
			gamemode = Gamemode.valueof(serverconfig.getString("gamemode"));
			setMaxPlayers(serverconfig.getInt("maxplayers"));
		} else {
			FileConfiguration serverconfig = NetworkCore.getConfigManger().createConfig("server.yml", NetworkCore.getInstance());
			serverconfig.addDefault("server", "UNKNOWN");
			server = "UNKNOWN";
			serverconfig.addDefault("gamemode", Gamemode.Hub.getValue());
			gamemode = Gamemode.Hub;
			serverconfig.addDefault("maxplayers", 200);	
			setMaxPlayers(200);
			
			
		}
		
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

	public static void setMaxPlayers(int maxPlayers)
	            throws ReflectiveOperationException {
	        String bukkitversion = Bukkit.getServer().getClass().getPackage()
	                .getName().substring(23);
	        Object playerlist = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".CraftServer")
	                .getDeclaredMethod("getHandle", null).invoke(Bukkit.getServer(), null);
	        Field maxplayers = playerlist.getClass().getSuperclass()
	                .getDeclaredField("maxPlayers");
	        maxplayers.setAccessible(true);
	        maxplayers.set(playerlist, maxplayers);
	    }
	
}
