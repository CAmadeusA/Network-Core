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
import com.camadeusa.utility.MD5;
import com.camadeusa.utility.command.Command;
import com.camadeusa.utility.command.CommandArgs;
import com.camadeusa.utility.command.CommandFramework;
import com.camadeusa.utility.subservers.packet.PacketDownloadServerConfigInfo;
import com.camadeusa.utility.subservers.packet.PacketUpdateDatabaseValue;

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

		args.getPlayer().sendMessage("=== " + NetworkCore.prefixStandard + "Help Menu: Showing Page " + page
				+ " of " + commands.size() / lineHeight + " ===");
		args.getPlayer().sendMessage(ChatColor.GRAY
				+ "Use /help <page-number> to get the page of help, or hover over the command to get known information on the command.");

		if (commands.size() == 0) {
			return;
		}
		page = page - 1;
		if (page < 0) {
			page = 0;
		}
		for (int i = (page * lineHeight); i < ((page + 1) * lineHeight) + 1; i++) {
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
						if (args.getArchrPlayer().getPlayerRank().getValue() >= PlayerRank.Donator1.getValue()) {
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

		} else {
			args.getPlayer().sendMessage(ChatManager.translateFor("en", args.getArchrPlayer(), "That is not a kind of server we support. Please try again."));
		}
	}
	
	@Command(name = "hub", aliases = { "lobby" }, usage = "/hub")
	public void hub(CommandArgs args) {
		Bukkit.getServer().dispatchCommand(args.getSender(), "join hub");
	}
	
	@Command(name = "changePassword", usage = "/changePassword {Current Password} {New Password}")
	public void changePassword(CommandArgs args) {
		if (args.getArgs().length < 1) {
			args.getPlayer().chat("/changePassword <Enter your previous password: > <Enter your NEW password>");
		} else {
			if (!args.getArchrPlayer().getData().getString("password").equals(MD5.getMD5(args.getArgs(0)))) {
				args.getPlayer().sendMessage(NetworkCore.prefixError + "Incorrect password.");
				return;
			} 
			args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Success! Your password was " + args.getArchrPlayer().getData().getString("password") + ", and is now: " + args.getArgs(1));
			SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(args.getPlayer().getUniqueId().toString(), "password", MD5.getMD5(args.getArgs(1))));
			
		}
	}
	
	@Command(name = "setPasswordPromptOnLogin", usage = "/setPasswordPromptOnLogin {true/false}")
	public void setpwpol(CommandArgs args) {
		if (args.getArgs().length < 1) {
			args.getPlayer().chat("/setPasswordPromptOnLogin <Enter Value: (true/false)>");
		} else {
			if (args.getArgs(0).equalsIgnoreCase("true") || args.getArgs(0).equalsIgnoreCase("false")) {
				SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(args.getPlayer().getUniqueId().toString(), "requirepwonlogin", args.getArgs(0)));
				args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Successfully Changed Value. Thank you!");
			} else {
				args.getPlayer().sendMessage(NetworkCore.prefixError + "Incorrect input, please try again.");
				args.getPlayer().chat("/setPasswordPromptOnLogin <Enter Value: (true/false)>");
			}
				
		}
	}
	
	@Command(name = "authenticate", usage = "/authenticate")
	public void auth(CommandArgs args) {
		if (args.getArchrPlayer().getData().has("password")) {
			if (args.getArgs().length < 1) {
				SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(args.getArchrPlayer().getPlayer().getUniqueId().toString(), "authenticated", "false"));
				args.getPlayer().chat("/authenticate <Input Your Password: >");
			} else {
				if (MD5.getMD5(args.getArgs(0)).equals(args.getArchrPlayer().getData().getString("password"))) {
					
					SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(args.getArchrPlayer().getPlayer().getUniqueId().toString(), "authenticated", "true"));
					args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Successfully Authenticated.");
				} else {
					args.getPlayer().sendMessage(NetworkCore.prefixError + "Password incorrect. Please Try again.");
					args.getPlayer().chat("/authenticate <Input Your Password: >");
					return;
				}
			}

		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + "You do not have a password.");
			return;
		}
	}
	
	@Command(name = "setuppassword", usage = "/setuppassword")
	public void setupPassword(CommandArgs args) {
			if (!args.getArchrPlayer().getData().has("password")) {
				if (args.getArgs().length < 1) {
					args.getPlayer().chat("/setupPassword <This password is unique to this network, and is case sensitive. Do you understand? (Y/N)?> <Do you want the server to require your password on login? (Y/N)?> <Input Your Password: >");
				} else {
					if (args.getArgs(0).equalsIgnoreCase("y")) {
						if (args.getArgs(1).equalsIgnoreCase("y")) {
							SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(args.getPlayer().getUniqueId().toString(), "requirepwonlogin", "TRUE"));
						} else if (args.getArgs(1).equalsIgnoreCase("n")) {
							SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(args.getPlayer().getUniqueId().toString(), "requirepwonlogin", "FALSE"));							
						} else {
							args.getPlayer().sendMessage(NetworkCore.prefixError + "Password setup failed. You are dumb.");
							return;
						}
						
						SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(args.getPlayer().getUniqueId().toString(), "password", MD5.getMD5(args.getArgs(2))));
						args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Password setup succeeded. Your password is: " + args.getArgs(2));
						args.getArchrPlayer().reloadPlayerData();
					} else {
						args.getPlayer().sendMessage(NetworkCore.prefixError + "Password setup failed. You are dumb.");
						return;
					}
				}

			} else {
				args.getPlayer().sendMessage(NetworkCore.prefixError + "You already have a password.");
				return;
			}
		
	}
}
