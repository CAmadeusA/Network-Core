package com.camadeusa.module.network;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.Module;
import com.camadeusa.module.hub.HubModule;

public class ModuleManager {
	ArrayList<? super Module> modules = new ArrayList<>();
	
	public void init() {
		gatherModules();
		registerModules();
	}
	
	public void gatherModules() {
		modules.add(new HubModule());
		
	}
	
	public void registerModules() {
		modules.forEach(m -> {
			NetworkCore.getInstance().getServer().getPluginManager().registerEvents((Listener) m, NetworkCore.getInstance());
		});
	}
	
	public String formatModulesForChat() {
		String s = "";
		for (Object m : modules) {
			if (((Module) m).isActive()) {
				s = ChatColor.GREEN + ((Module) m).getTag() + ", " + ChatColor.RESET + s;
			} else {
				s = ChatColor.RED + ((Module) m).getTag() + ", " + ChatColor.RESET + s;
			}
		}
		return s;
	}
}
