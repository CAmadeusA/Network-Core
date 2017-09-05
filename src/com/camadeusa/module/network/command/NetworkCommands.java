package com.camadeusa.module.network.command;

import com.camadeusa.utility.command.Command;
import com.camadeusa.utility.command.CommandArgs;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class NetworkCommands {
	//Party and friend system here maybe.
	
	
	public static void sendPlayerToServer(ProxiedPlayer p, String server){
        p.connect(ProxyServer.getInstance().getServers().get(server));
	}
}
