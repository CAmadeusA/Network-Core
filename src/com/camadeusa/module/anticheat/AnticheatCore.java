package com.camadeusa.module.anticheat;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.anticheat.checks.ForcefieldCheck;
import com.camadeusa.module.anticheat.checks.SpeedCheck;
import com.camadeusa.module.anticheat.checks.VapeCheck;

public class AnticheatCore {
	String prefixDefault = ChatColor.GOLD + "" + ChatColor.BOLD + "ZUES: " + ChatColor.RESET;
	private static AnticheatCore instance;
	private HashMap<CheckType, ArrayList<Check>> activeChecks = new HashMap<>();
	
	
	public AnticheatCore() {
		instance = this;
		
		Bukkit.getLogger().info(
				"\n\n _____\n" +
				"/ _  /__ __  ___   ___ \n" + 
				"\\// /| | | |/ _ \\/ __|\n" + 
				" / //\\ |_| |  __/\\__ \\\n" + 
				"/____/\\__,_|\\___||___/\n\n");
		
		//Register checks
		registerCheck(new SpeedCheck());
		registerCheck(new VapeCheck());
		registerCheck(new ForcefieldCheck());
		
		
		//Activates all the necessary checks
		activeChecks.keySet().forEach(key -> {
			activeChecks.get(key).forEach(check -> {
				check.activateModule();
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
