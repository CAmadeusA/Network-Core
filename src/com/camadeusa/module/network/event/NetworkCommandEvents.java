package com.camadeusa.module.network.event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;

import com.camadeusa.NetworkCore;
import com.camadeusa.chat.ChatManager;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.player.PlayerState;

public class NetworkCommandEvents implements Listener {

	@EventHandler
	public void onChatTab(TabCompleteEvent event) {
		List<String> completions = new ArrayList<String>();
		if (event.getSender() instanceof Player) {
			NetworkPlayer player = NetworkPlayer.getNetworkPlayerByUUID(((Player) event.getSender()).getUniqueId().toString());
			String[] args = event.getBuffer().split(" ");
			if (event.getBuffer().equalsIgnoreCase("/") || event.getBuffer().equalsIgnoreCase("/ ")) {
				for (String s : PlayerRank.getCommandsAvailable(player.getPlayerRank())) {
					completions.add("/" + s);
				}
			} else if (event.getBuffer().substring(event.getBuffer().length()-1, event.getBuffer().length()).equals(" ")) {
				for (NetworkPlayer ap : NetworkPlayer.getNetworkPlayerList()) {
					if (PlayerState.canSee(player.getPlayerState(), ap.getPlayerState())) {
						completions.add(ap.getPlayer().getName());
					}
				}
			} else if (!args[args.length-1].substring(args[args.length-1].length()-1, args[args.length-1].length()).equals(" ")) {
				String text = "";
				for (int i = 0; i < args.length-1; i++) {
					text = text + args[i] + " ";
				}
				for (NetworkPlayer ap : NetworkPlayer.getNetworkPlayerList()) {
					if (PlayerState.canSee(player.getPlayerState(), ap.getPlayerState()) && ap.getPlayer().getName().toLowerCase().contains(args[args.length-1].toLowerCase())) {
						completions.add(ap.getPlayer().getName());
					}
				}
			}

		}
		event.setCompletions(completions);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		String[] args = event.getMessage().split(" ");
		String command = args[0].replace("/", "");
		NetworkPlayer aP = NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString());
		if (!PlayerRank.canUseCommand(aP.getPlayerRank(), command)) {
			event.setCancelled(true);
			aP.getPlayer().sendMessage(ChatManager.translateFor("en", aP, NetworkCore.prefixError + "You do not have permission to use this command. If you believe this to be an error, please contact the administration."));
		}
		if (!aP.getData().getString("authenticated").equalsIgnoreCase("true")) {
			if (!command.equalsIgnoreCase("authenticate")) {
				event.setCancelled(true);
				aP.getPlayer().sendMessage(ChatManager.translateFor("en", aP, NetworkCore.prefixError + "You are not authenticated to use this command. "));
			}
		}
	}

}
