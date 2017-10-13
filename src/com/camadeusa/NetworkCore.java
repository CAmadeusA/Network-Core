package com.camadeusa;

import java.io.File;
import java.io.FileInputStream;

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
import com.camadeusa.utility.command.prompt.listener.CommandListener;
import com.camadeusa.utility.menu.InventoryManager;
import com.camadeusa.utility.subservers.event.SubserversEvents;
import com.camadeusa.utility.subservers.packet.PacketDownloadPlayerInfo;
import com.camadeusa.utility.subservers.packet.PacketDownloadServerConfigInfo;
import com.camadeusa.utility.subservers.packet.PacketGetServerConfigInfo;
import com.camadeusa.utility.subservers.packet.PacketPunishPlayer;
import com.camadeusa.utility.subservers.packet.PacketUpdateDatabaseValue;
import com.camadeusa.utility.xoreboard.XoreBoardUtil;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.ME1312.SubServers.Client.Bukkit.Network.SubDataClient;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolVersion;

public class NetworkCore extends JavaPlugin {
	static NetworkCore instance;
	static ConfigUtil configManager;
	static GamemodeManager gamemodeManager;
	public static String prefixStandard = ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "[" + ChatColor.GOLD + "Orion" + ChatColor.DARK_PURPLE + "]" + ChatColor.WHITE + ": " + ChatColor.RESET;
	public static String prefixError = ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "[" + ChatColor.DARK_RED + "Orion" + ChatColor.DARK_PURPLE + "]" + ChatColor.WHITE + ": " + ChatColor.RESET;
	public GSheetDBUtil playersDB;
	XoreBoardUtil xbu;
	DatabaseReference fbdb;

	
	@Override
	public void onEnable() {
		super.onEnable();
		instance = this;
		xbu = new XoreBoardUtil();
		xbu.init();
		configManager = new ConfigUtil();
		gamemodeManager = new GamemodeManager();
		gamemodeManager.activateGametype();
		registerEvents();
		initializePlugin();
		
		try {
			FileInputStream serviceAccount = new FileInputStream(new File("").getAbsolutePath() + "/resources/serviceaccountkey.json");
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
					.setDatabaseUrl("https://voltcube-network.firebaseio.com")
					.build();
			
			FirebaseApp.initializeApp(options);
		} catch (Exception e) {}

		fbdb = FirebaseDatabase.getInstance("https://voltcube-network.firebaseio.com").getReference();
		
	}
	
	
	public void initializePlugin() {
		CoreLoop coreloop = new CoreLoop();
		coreloop.init();
		playersDB = new GSheetDBUtil("archrplayers", "players");
		CommandFramework frameWork = new CommandFramework(this);
		frameWork.registerCommands(new StaffCommands());
		frameWork.registerCommands(new NetworkCommands());
		
		ProtocolSupportAPI.disableProtocolVersion(ProtocolVersion.MINECRAFT_1_4_7);
		ProtocolSupportAPI.disableProtocolVersion(ProtocolVersion.MINECRAFT_1_5_1);
		ProtocolSupportAPI.disableProtocolVersion(ProtocolVersion.MINECRAFT_1_5_2);
		ProtocolSupportAPI.disableProtocolVersion(ProtocolVersion.MINECRAFT_1_6_1);
		ProtocolSupportAPI.disableProtocolVersion(ProtocolVersion.MINECRAFT_1_6_2);
		ProtocolSupportAPI.disableProtocolVersion(ProtocolVersion.MINECRAFT_1_6_4);
		
		SubDataClient.registerPacket(new PacketGetServerConfigInfo(), "PacketGetServerConfigInfo");
		SubDataClient.registerPacket(PacketGetServerConfigInfo.class, "PacketGetServerConfigInfo");
		SubDataClient.registerPacket(new PacketDownloadServerConfigInfo(), "PacketDownloadServerConfigInfo");
		SubDataClient.registerPacket(PacketDownloadServerConfigInfo.class, "PacketDownloadServerConfigInfo");
		SubDataClient.registerPacket(new PacketDownloadPlayerInfo(), "PacketDownloadPlayerInfo");
		SubDataClient.registerPacket(PacketDownloadPlayerInfo.class, "PacketDownloadPlayerInfo");
		SubDataClient.registerPacket(new PacketUpdateDatabaseValue(), "PacketUpdateDatabaseValue");
		SubDataClient.registerPacket(PacketUpdateDatabaseValue.class, "PacketUpdateDatabaseValue");
		SubDataClient.registerPacket(new PacketPunishPlayer(), "PacketPunishPlayer");
		SubDataClient.registerPacket(PacketPunishPlayer.class, "PacketPunishPlayer");
			
	}
	
	public void registerEvents() {
		getServer().getPluginManager().registerEvents(new ArchrPlayer(), this);
		getServer().getPluginManager().registerEvents(new ChatManager(), this);
		getServer().getPluginManager().registerEvents(new NetworkCommandEvents(), this);
		getServer().getPluginManager().registerEvents(new InventoryManager(), this);
		getServer().getPluginManager().registerEvents(new NetworkServerInfoEvents(), this);
		getServer().getPluginManager().registerEvents(new SubserversEvents(), this);
		getServer().getPluginManager().registerEvents(new CommandListener(), this);

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

	public XoreBoardUtil getXoreBoardUtil() {
		return xbu;
	}


	public DatabaseReference getDatabase() {
		return fbdb;
	}
	
}
