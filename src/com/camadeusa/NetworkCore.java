package com.camadeusa;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.camadeusa.chat.ChatManager;
import com.camadeusa.module.game.GamemodeManager;
import com.camadeusa.module.network.command.NetworkCommands;
import com.camadeusa.module.network.command.StaffCommands;
import com.camadeusa.module.network.event.NetworkCommandEvents;
import com.camadeusa.module.network.event.NetworkServerInfoEvents;
import com.camadeusa.player.ArchrPlayer;
import com.camadeusa.timing.CoreLoop;
import com.camadeusa.utility.ConfigUtil;
import com.camadeusa.utility.GSheetDBUtil;
import com.camadeusa.utility.command.CommandFramework;
import com.camadeusa.utility.menu.InventoryManager;
import com.google.gdata.data.spreadsheet.ListEntry;

public class NetworkCore extends JavaPlugin {
	static NetworkCore instance;
	static ConfigUtil configManager;
	static GamemodeManager gamemodeManager;
	public static String prefixStandard = ChatColor.BOLD + "" + ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "AR" + ChatColor.GOLD + "CHR" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + ": " + ChatColor.RESET;
	public static String prefixError = ChatColor.BOLD + "" + ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "AR" + ChatColor.GOLD + "CHR" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + ": " + ChatColor.RESET;
	public GSheetDBUtil playersDB;
	public GSheetDBUtil serversDB;
	
	@Override
	public void onEnable() {
		super.onEnable();
		instance = this;
		configManager = new ConfigUtil();
		gamemodeManager = new GamemodeManager();
		registerEvents();
		initializePlugin();
	}
	
	public void initializePlugin() {
		CoreLoop coreloop = new CoreLoop();
		coreloop.init();
		playersDB = new GSheetDBUtil("archrplayers", "players");
		serversDB = new GSheetDBUtil("archrservers", "servers");
		CommandFramework frameWork = new CommandFramework(this);
		frameWork.registerCommands(new StaffCommands());
		frameWork.registerCommands(new NetworkCommands());
		
		
	}
	
	public void registerEvents() {
		getServer().getPluginManager().registerEvents(new ArchrPlayer(), this);
		getServer().getPluginManager().registerEvents(new ChatManager(), this);
		getServer().getPluginManager().registerEvents(new NetworkCommandEvents(), this);
		getServer().getPluginManager().registerEvents(new InventoryManager(), this);
		getServer().getPluginManager().registerEvents(new NetworkServerInfoEvents(), this);
		}
	
	
	@Override
	public void onDisable() {
		ListEntry row;
		try {
			row = NetworkCore.getInstance().serversDB.getRow("uuid",
					NetworkCore.getConfigManger().getConfig("server", NetworkCore.getInstance()).getString("uuid"));
			Map<String, Object> data = NetworkCore.getInstance().serversDB.getRowData(row);
			boolean changed = false;
			if (Integer.parseInt(data.get("onlineplayers").toString()) > 0) {
				data.put("onlineplayers", 0);
				changed = true;
			}
			if (Boolean.parseBoolean(data.get("serveronline").toString()) == true) {
				data.put("serveronline", false);
				changed = true;
			}
			
			if (changed) {
				NetworkCore.getInstance().serversDB.updateRow(row, data);
				row.update();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.onDisable();
	}
	
	public static NetworkCore getInstance() {
		return instance;
	}
	
	public static ConfigUtil getConfigManger() {
		return configManager;
	}
	
}
