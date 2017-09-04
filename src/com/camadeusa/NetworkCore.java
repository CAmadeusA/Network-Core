package com.camadeusa;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.camadeusa.chat.ChatManager;
import com.camadeusa.module.network.command.NetworkCommandEvents;
import com.camadeusa.module.network.command.StaffCommands;
import com.camadeusa.player.ArchrPlayer;
import com.camadeusa.timing.CoreLoop;
import com.camadeusa.utility.ConfigUtil;
import com.camadeusa.utility.GSheetDBUtil;
import com.camadeusa.utility.command.CommandFramework;

public class NetworkCore extends JavaPlugin {
	static NetworkCore instance;
	static ConfigUtil configManager;
	public static String prefixStandard = ChatColor.BOLD + "" + ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "AR" + ChatColor.GOLD + "CHR" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + ": " + ChatColor.RESET;
	public static String prefixError = ChatColor.BOLD + "" + ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "AR" + ChatColor.GOLD + "CHR" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + ": " + ChatColor.RESET;
	public GSheetDBUtil playersDB;
	
	@Override
	public void onEnable() {
		super.onEnable();
		instance = this;
		configManager = new ConfigUtil();
		registerEvents();
		initializePlugin();
	}
	
	public void initializePlugin() {
		CoreLoop coreloop = new CoreLoop();
		coreloop.init();
		playersDB = new GSheetDBUtil("archrplayers", "players");
		CommandFramework frameWork = new CommandFramework(this);
		frameWork.registerCommands(new StaffCommands());
		
	}
	
	public void registerEvents() {
		getServer().getPluginManager().registerEvents(new ArchrPlayer(), this);
		getServer().getPluginManager().registerEvents(new ChatManager(), this);
		getServer().getPluginManager().registerEvents(new NetworkCommandEvents(), this);
	}
	
	
	@Override 
	public void onDisable() {
		super.onDisable();
	}
	
	public static NetworkCore getInstance() {
		return instance;
	}
	
	public static ConfigUtil getConfigManger() {
		return configManager;
	}
	
}
