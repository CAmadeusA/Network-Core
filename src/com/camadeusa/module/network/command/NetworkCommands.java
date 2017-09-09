package com.camadeusa.module.network.command;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.camadeusa.NetworkCore;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.utility.command.Command;
import com.camadeusa.utility.command.CommandArgs;
import com.camadeusa.utility.command.CommandFramework;

import mkremins.fanciful.FancyMessage;

public class NetworkCommands {
	@Command(name = "help", aliases = { "h", "?" }, usage = "/help")
	public void help(CommandArgs args) {
		int lineHeight = 7;
		int page;
		if (args.length() < 1 || !StringUtils.isNumeric(args.getArgs(0))) {
			page = 1;
		} else {
			page = Integer.parseInt(args.getArgs(0));
		}

		ArrayList<String> commands = PlayerRank.getCommandsAvailable(args.getArchrPlayer().getPlayerRank());
		Collections.sort(commands);
		if (page > commands.size() / lineHeight) {
			page = commands.size() / lineHeight;
		}

		args.getPlayer().sendMessage("------=== " + NetworkCore.prefixStandard + "Help Menu: Showing Page " + page
				+ " of " + commands.size() / lineHeight + " ===------");
		args.getPlayer().sendMessage(ChatColor.GRAY
				+ "Use /help <page-number> to get the page of help, or hover over the command to get known information on the command.");

		if (commands.size() == 0) {
			return;
		}
		page = page - 1;
		for (int i = (page * lineHeight); i < ((page + 1) * lineHeight); i++) {			
			if (CommandFramework.getCommandsByString().contains(commands.get(i))) {
				String text = (Object) ChatColor.BOLD + "" + (Object) ChatColor.GOLD + "/" + commands.get(i);
				new FancyMessage(text).tooltip(CommandFramework.getCommand(commands.get(i)).usage())
						.suggest(CommandFramework.getCommand(commands.get(i)).usage()).send(args.getPlayer());

			} else if (CommandFramework.getCommandStringsNotRegisteredByMe().contains(commands.get(i))) {
				String text = (Object) ChatColor.BOLD + "" + (Object) ChatColor.GOLD + "/" + commands.get(i);
				new FancyMessage(text).tooltip(CommandFramework.getCommandNotRegisteredByMe(commands.get(i)).getUsage())
						.suggest(CommandFramework.getCommandNotRegisteredByMe(commands.get(i)).getUsage())
						.send(args.getPlayer());

			} else {
				String text = (Object) ChatColor.BOLD + "" + (Object) ChatColor.GOLD + "/" + commands.get(i);
				new FancyMessage(text).tooltip("No information provided by developer...").command(commands.get(i)).send(args.getPlayer());

			}

		}

	}
}
