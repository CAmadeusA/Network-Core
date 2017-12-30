package com.camadeusa.module.game.uhcsg;

import java.util.ArrayList;

import org.bukkit.ChatColor;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.game.uhcsg.segments.Lobby;
import com.camadeusa.utility.command.Command;
import com.camadeusa.utility.command.CommandArgs;
import com.camadeusa.world.OrionMap;

import mkremins.fanciful.FancyMessage;

public class UHCSGCommands {
	public static ArrayList<String> debugList = new ArrayList<>();
	
	@Command(name = "nextSegment", usage = "/nextSegment")
	public void hub(CommandArgs args) {
		UHCSGOrionGame.getInstance().getCurrentSegment().nextSegment();
	}
	
	@Command(name = "debug", usage = "/debug <Time/Tier/Playercount/(More to be added)>")
	public void debug(CommandArgs args) {
		if (args.getArgs().length == 0) {
			args.getPlayer().chat("/debug <What would you like to debug? (Time/Tier/Playercount)>");
		} else {
			for (String db : args.getArgs()) {
				switch (db.toLowerCase()) {
				case "time":
					if (!debugList.contains("time")) {
						debugList.add("time");
					}
					break;
				case "tier":
					if (!debugList.contains("tier")) {
						debugList.add("tier");
					}
					break;
				case "playercount":
					if (!debugList.contains("playercount")) {
						debugList.add("playercount");
					}
					break;
				case "clear":
					debugList = new ArrayList<>();
					break;
				
				}
			}
		}
	}
	
	@Command(name = "vote", usage = "/vote [Map-Name]")
	public void vote(CommandArgs args) {
		if (args.getArgs().length == 0) {
			args.getPlayer().chat("/vote <" + NetworkCore.prefixStandard + "What map would you like to vote for?: >");
		} else {
			Lobby.instance.voteMap(args.getNetworkPlayer(), args.getArgs(0));
			int totalVotes = 0;
			for (int j = 0; j < Lobby.instance.top.size(); j++) {
				totalVotes += (int) Lobby.instance.top.values().toArray()[j];
			}
			for (int i = (Lobby.instance.top.size() >= 5 ? 5 : Lobby.instance.top.size()) -1; i >= 0; i--) {
				FancyMessage fm = new FancyMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RESET + "" + Lobby.instance.top.values().toArray()[i] + "/" + totalVotes + ChatColor.DARK_GRAY + ']' + ChatColor.RESET + ": " + ChatColor.LIGHT_PURPLE + ((OrionMap) Lobby.instance.top.keySet().toArray()[i]).getMapName());
				fm.tooltip(ChatColor.GOLD + "Click To Vote!", "", ChatColor.GOLD + "Author: " + ChatColor.RESET + ((OrionMap) Lobby.instance.top.keySet().toArray()[i]).getMapAuthor(), ChatColor.GOLD + "Link: " + ChatColor.RESET + ((OrionMap) Lobby.instance.top.keySet().toArray()[i]).getMapLink(), ChatColor.GOLD + "Size: " + ChatColor.RESET + ((OrionMap) Lobby.instance.top.keySet().toArray()[i]).getRadius());
				fm.command("/vote " + ((OrionMap) Lobby.instance.top.keySet().toArray()[i]).getMapName());
				fm.send(args.getPlayer());
			}
		}
	}
}
