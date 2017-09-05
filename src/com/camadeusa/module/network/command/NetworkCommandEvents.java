package com.camadeusa.module.network.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import com.camadeusa.player.ArchrPlayer;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.player.PlayerState;

public class NetworkCommandEvents implements Listener {

	@EventHandler
	public void onChatTab(TabCompleteEvent event) {
		List<String> completions = new ArrayList<String>();
		if (event.getSender() instanceof Player) {
			ArchrPlayer player = ArchrPlayer.getArchrPlayerByUUID(((Player) event.getSender()).getUniqueId().toString());
			String[] args = event.getBuffer().split(" ");
			if (event.getBuffer().equalsIgnoreCase("/") || event.getBuffer().equalsIgnoreCase("/ ")) {
				for (String s : PlayerRank.getCommandsAvailable(player.getPlayerRank())) {
					completions.add("/" + s);
				}
			} else if (event.getBuffer().substring(event.getBuffer().length()-1, event.getBuffer().length()).equals(" ")) {
				for (ArchrPlayer ap : ArchrPlayer.getArchrPlayerList()) {
					if (PlayerState.canSee(player.getPlayerState(), ap.getPlayerState())) {
						completions.add(ap.getPlayer().getName());
					}
				}
			} else if (!args[args.length-1].substring(args[args.length-1].length()-1, args[args.length-1].length()).equals(" ")) {
				String text = "";
				for (int i = 0; i < args.length-1; i++) {
					text = text + args[i] + " ";
				}
				for (ArchrPlayer ap : ArchrPlayer.getArchrPlayerList()) {
					if (PlayerState.canSee(player.getPlayerState(), ap.getPlayerState()) && ap.getPlayer().getName().toLowerCase().contains(args[args.length-1].toLowerCase())) {
						completions.add(ap.getPlayer().getName());
					}
				}
			}

		}
		event.setCompletions(completions);
	}

}
