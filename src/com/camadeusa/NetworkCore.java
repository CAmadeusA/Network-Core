package com.camadeusa;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.camadeusa.chat.ChatManager;
import com.camadeusa.chat.Profanity;
import com.camadeusa.module.anticheat.AnticheatCore;
import com.camadeusa.module.game.GamemodeManager;
import com.camadeusa.module.game.uhcsg.UHCSGCommands;
import com.camadeusa.module.mapeditor.MapEditorCommands;
import com.camadeusa.network.command.NetworkCommands;
import com.camadeusa.network.command.StaffCommands;
import com.camadeusa.network.event.NetworkCommandEvents;
import com.camadeusa.network.event.NetworkServerInfoEvents;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.timing.CoreLoop;
import com.camadeusa.utility.ConfigUtil;
import com.camadeusa.utility.command.CommandFramework;
import com.camadeusa.utility.command.prompt.listener.CommandListener;
import com.camadeusa.utility.subservers.event.SubserversEvents;
import com.camadeusa.utility.subservers.packet.PacketDownloadNetworkSettings;
import com.camadeusa.utility.subservers.packet.PacketDownloadPlayerInfo;
import com.camadeusa.utility.subservers.packet.PacketDownloadServerConfigInfo;
import com.camadeusa.utility.subservers.packet.PacketGetServerConfigInfo;
import com.camadeusa.utility.subservers.packet.PacketLogChatMessage;
import com.camadeusa.utility.subservers.packet.PacketLogLeaderboardStats;
import com.camadeusa.utility.subservers.packet.PacketPunishPlayer;
import com.camadeusa.utility.subservers.packet.PacketUpdateDatabaseValue;
import com.camadeusa.utility.xoreboard.XoreBoardUtil;
import com.camadeusa.world.WorldManager;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import net.ME1312.SubServers.Client.Bukkit.Network.SubDataClient;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolVersion;

public class NetworkCore extends JavaPlugin {
	static NetworkCore instance;
	static ConfigUtil configManager;
	static GamemodeManager gamemodeManager;
	public static String prefixStandard = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Orion" + ChatColor.DARK_GRAY + ">> " + ChatColor.RESET;
	public static String prefixError = ChatColor.RED + "" + ChatColor.BOLD + "Orion" + ChatColor.GRAY + ">> " + ChatColor.RESET;
	XoreBoardUtil xbu;
	Connection con;
	
	int totalNetworkPlayers = 0;

	
	@Override
	public void onEnable() {
		super.onEnable();
		instance = this;
		
		new AnticheatCore();
		Profanity.loadConfigs();
		
		con = RethinkDB.r.connection().hostname("na-central.orionmc.net").db("Orion_Network").connect();
		con.use("Orion_Network");
		xbu = new XoreBoardUtil();
		xbu.init();
		configManager = new ConfigUtil();
		new WorldManager();
		gamemodeManager = new GamemodeManager();
		gamemodeManager.activateGametype();
		registerEvents();
		initializePlugin();
		
	}
	
	
	public void initializePlugin() {
		CoreLoop coreloop = new CoreLoop();
		coreloop.init();
		
		CommandFramework frameWork = new CommandFramework(this);
		frameWork.registerCommands(new StaffCommands());
		frameWork.registerCommands(new NetworkCommands());
		frameWork.registerCommands(new MapEditorCommands());
		frameWork.registerCommands(new UHCSGCommands());
		
		ProtocolSupportAPI.disableProtocolVersion(ProtocolVersion.MINECRAFT_1_7_10);
		ProtocolSupportAPI.disableProtocolVersion(ProtocolVersion.MINECRAFT_1_7_5);
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
		SubDataClient.registerPacket(new PacketDownloadNetworkSettings(), "PacketDownloadNetworkSettings");
		SubDataClient.registerPacket(PacketDownloadNetworkSettings.class, "PacketDownloadNetworkSettings");
		SubDataClient.registerPacket(new PacketLogChatMessage(), "PacketLogChatMessage");
		SubDataClient.registerPacket(PacketLogChatMessage.class, "PacketLogChatMessage");
		SubDataClient.registerPacket(PacketLogLeaderboardStats.class, "PacketLogLeaderboardStats");
			
	}
	
	public void registerEvents() {
		getServer().getPluginManager().registerEvents(new NetworkPlayer(), this);
		getServer().getPluginManager().registerEvents(new ChatManager(), this);
		getServer().getPluginManager().registerEvents(new NetworkCommandEvents(), this);
		getServer().getPluginManager().registerEvents(new NetworkServerInfoEvents(), this);
		getServer().getPluginManager().registerEvents(new SubserversEvents(), this);
		getServer().getPluginManager().registerEvents(new CommandListener(), this);

		}
	
	
	@Override
	public void onDisable() {
		Bukkit.getWorlds().forEach(w -> {
			if (!w.getName().equals("world") && !w.getName().equals("world_nether") && !w.getName().equals("world_the_end")) {
				WorldManager.unloadWorld(w.getName());
			}
		});
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


	public Connection getCon() {
		return con;
	}


	public void setCon(Connection con) {
		this.con = con;
	}


	public int getTotalNetworkPlayers() {
		return totalNetworkPlayers;
	}


	public void setTotalNetworkPlayers(int totalNetworkPlayers) {
		this.totalNetworkPlayers = totalNetworkPlayers;
	}
	
}
