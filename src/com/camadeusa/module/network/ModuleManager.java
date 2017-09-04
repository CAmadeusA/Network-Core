package com.camadeusa.module.network;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.Module;
import com.camadeusa.module.hub.HubModule;

public class ModuleManager {
	HashMap<String, ? super Module> modules = new HashMap<>();
	public ArrayList<? super Module> modulesToRegister = new ArrayList<>();

	public ModuleManager() {
		gatherModules();	
	}
	
	public HashMap<String, ? super Module> gatherModules() {
		modules.put("hubmodule", new HubModule());
		return modules;
	}
	
	public void registerModules() {
		modulesToRegister.forEach(m -> {
			NetworkCore.getInstance().getServer().getPluginManager().registerEvents((Listener) m, NetworkCore.getInstance());
			((Module) m).activateModule();
		});
	}
}
