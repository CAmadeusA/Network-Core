package com.camadeusa.module.anticheat;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.anticheat.checks.SpeedCheck;

public class AnticheatCore {
	String prefixDefault = ChatColor.GOLD + "" + ChatColor.BOLD + "ZUES: " + ChatColor.RESET;
	private static AnticheatCore instance;
	private HashMap<CheckType, ArrayList<Check>> activeChecks = new HashMap<>();
	
	public static enum HackingStatus {
		ISHACKING,
		ISNOTHACKING;
	}
	
	public AnticheatCore() {
		instance = this;
		
		Bukkit.getLogger().info("/ _  /_   _  ___  ___ \n" + 
				"\\// /| | | |/ _ \\/ __|\n" + 
				" / //\\ |_| |  __/\\__ \\\n" + 
				"/____/\\__,_|\\___||___/\n\n");
		
		//Register checks
		registerCheck(new SpeedCheck()/*do more here if necessary*/);
		
		
		//Activates all the necessary checks
		activeChecks.keySet().forEach(key -> {
			activeChecks.get(key).forEach(check -> {
				check.activateModule();
				Bukkit.getPluginManager().registerEvents(check, NetworkCore.getInstance());
			});
		});
		
	}
	
	private void registerCheck(Check ...c) {
		boolean shouldRegister = true;
		CheckType cT = null;
		ArrayList<Check> list = new ArrayList<>();
		for (Check check : c) {
			if (!check.checkType.isEnabled()) {
				shouldRegister = false;
			} else {
				list.add(check);	
				cT = check.getCheckType();
			}
		}
		if (cT != null && shouldRegister) {
			activeChecks.put(cT, list);			
		}
	}
	
	public AnticheatCore getInstance() {
		return instance;
	}
}
