package com.camadeusa.module.network.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.JSONObject;

import com.camadeusa.NetworkCore;
import com.camadeusa.chat.ChatManager;
import com.camadeusa.module.game.Gamemode;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.utility.command.Command;
import com.camadeusa.utility.command.CommandArgs;
import com.camadeusa.utility.command.CommandFramework;
import com.camadeusa.utility.subservers.packet.PacketDownloadServerConfigInfo;

import mkremins.fanciful.FancyMessage;
import net.ME1312.SubServers.Client.Bukkit.SubAPI;
import net.ME1312.SubServers.Client.Bukkit.Network.Packet.PacketDownloadServerList;

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
				new FancyMessage(text).tooltip("No information provided by developer...").command(commands.get(i))
						.send(args.getPlayer());

			}

		}

	}

	@SuppressWarnings("deprecation")
	@Command(name = "join", aliases = { "server" }, usage = "/join {hub/arenapvp/etc}")
	public void join(CommandArgs args) {
		Gamemode selected = null;
		HashMap<String, JSONObject> availableServers = new HashMap<>();

		for (Gamemode mode : Gamemode.values()) {
			if (args.getArgs(0).equalsIgnoreCase(mode.getValue())) {
				selected = mode;
				break;
			}
		}

		if (selected != null) {
			args.getPlayer().sendMessage(ChatManager.translateFor("en", args.getArchrPlayer(),
					NetworkCore.prefixStandard + "Searching for servers of type: " + selected.getValue()));
			Gamemode seltemp = selected;
			Bukkit.getServer().getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
				@Override
				public void run() {
					SubAPI.getInstance().getSubDataNetwork()
							.sendPacket(new PacketDownloadServerList(null, null, json -> {
								for (String server : json.getJSONObject("hosts").getJSONObject("~")
										.getJSONObject("servers").keySet()) {
									if (server.substring(0, seltemp.getValue().length())
											.equalsIgnoreCase(seltemp.getValue())) {
										SubAPI.getInstance().getSubDataNetwork()
												.sendPacket(new PacketDownloadServerConfigInfo(server, infojson -> {
													availableServers.put(server, infojson);
												}));

										// Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sub teleport "
										// + server + " " + args.getPlayer().getName());
									}
								}
							}));
				}
			});

			Bukkit.getScheduler().scheduleAsyncDelayedTask(NetworkCore.getInstance(), new Runnable() {
				@Override
				public void run() {
					boolean containsNotFullServer = false;
					HashMap<String, JSONObject> notFullServers = new HashMap<>();

					for (String key : availableServers.keySet()) {
						if (availableServers.get(key).getJSONObject("serverdata").getInt("onlineplayers") < availableServers.get(key).getJSONObject("serverdata")
								.getInt("maxplayers")) {
							notFullServers.put(key, availableServers.get(key));
							containsNotFullServer = true;
						}
					}
					if (containsNotFullServer) {
						String fullestServer = "";
						for (String key : notFullServers.keySet()) {
							if (fullestServer.equals("")) {
								fullestServer = key;
							} else {
								if (notFullServers.get(fullestServer).getJSONObject("serverdata").getInt("onlineplayers") < notFullServers.get(key).getJSONObject("serverdata")
										.getInt("onlineplayers")) {
									fullestServer = key;
								}
							}
						}
						
						args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Sending you to server: " + fullestServer + ".");
						
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
								"sub teleport " + fullestServer + " " + args.getPlayer().getName());

					} else {
						if (PlayerRank.getValueByRank(args.getArchrPlayer().getPlayerRank()) >= PlayerRank
								.getValueByRank(PlayerRank.Donator1)) {
							args.getPlayer().sendMessage(NetworkCore.prefixStandard
									+ "All servers of this type are full... Attemting to send you to any available server of this type using your join-priority perk.");
							for (String key : availableServers.keySet()) {
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
										"sub teleport " + availableServers.get(key).getString("name") + " "
												+ args.getPlayer().getName());
								double d = System.currentTimeMillis() + 1000;
								while (true) {
									if (System.currentTimeMillis() >= d) {
										break;
									}
								}
								if (!args.getPlayer().isOnline()) {
									break;
								}
							}
						} else {
							args.getPlayer().sendMessage(NetworkCore.prefixError
									+ "All servers of this type are full... Please try again later, or donate for the join priority perk to allow you to join full games. ");
						}
					}

				}
			}, 20);

		}
	}
	
	@Command(name = "hub", aliases = { "lobby" }, usage = "/hub")
	public void hub(CommandArgs args) {
		Bukkit.getServer().dispatchCommand(args.getSender(), "join hub");
	}
}
