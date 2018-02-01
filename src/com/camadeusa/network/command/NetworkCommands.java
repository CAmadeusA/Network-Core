package com.camadeusa.network.command;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.camadeusa.NetworkCore;
import com.camadeusa.chat.ChatManager;
import com.camadeusa.module.game.Gamemode;
import com.camadeusa.module.game.GamemodeManager;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.utility.Encryption;
import com.camadeusa.utility.MD5;
import com.camadeusa.utility.command.Command;
import com.camadeusa.utility.command.CommandArgs;
import com.camadeusa.utility.command.CommandFramework;
import com.camadeusa.utility.subservers.packet.PacketDownloadOrionServerList;
import com.camadeusa.utility.subservers.packet.PacketUpdateDatabaseValue;

import mkremins.fanciful.FancyMessage;
import net.ME1312.SubServers.Client.Bukkit.SubAPI;
import net.ME1312.SubServers.Client.Bukkit.Library.JSONCallback;

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

		ArrayList<String> commands = PlayerRank.getCommandsAvailable(args.getNetworkPlayer().getPlayerRank());
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
	@Command(name = "join", aliases = { "server" }, usage = "/join {hub/usg/etc}")
	public void join(CommandArgs args) {
		
		if (args.getArgs().length != 1) {
			args.getPlayer().chat("/join <What Kind of server would you like to join? (Hub/USG): >");
		}
		
		Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
			@Override
			public void run() {
				Gamemode selected = null;
				for (Gamemode mode : Gamemode.values()) {
					if (args.getArgs(0).equalsIgnoreCase(mode.getValue())) {
						selected = mode;
						break;
					}
				}
				if (selected != null) {
					args.getPlayer().sendMessage(ChatManager.translateFor("en", args.getNetworkPlayer(),
							NetworkCore.prefixStandard + "Searching for servers of type: " + selected.getValue()));
					SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketDownloadOrionServerList(selected.getValue(), args.getNetworkPlayer().getPlayerRank().getValue(), json -> {
						String mostFullServer = "";
						for (String key : json.keySet()) {
							if (!key.equals("id")) {
								if (mostFullServer.equals("")) {
									mostFullServer = key;
								} else {
									if (json.getJSONObject(mostFullServer).getInt("onlineplayers") < json.getJSONObject(key).getInt("onlineplayers")) {
										mostFullServer = key;										
									} else if (json.getJSONObject(mostFullServer).getInt("maxplayers") == json.getJSONObject(key).getInt("maxplayers") && json.getJSONObject(mostFullServer).getInt("onlineplayers") == json.getJSONObject(mostFullServer).getInt("maxplayers") && json.getJSONObject(key).getInt("onlineplayers") == json.getJSONObject(key).getInt("maxplayers")) {
										mostFullServer = key;										
									}
								}
							}
						}
						args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Found server " + mostFullServer + ". Teleporting...");
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
								"sub teleport " + mostFullServer + " " + args.getPlayer().getName());
					}));				
				} else {
					args.getPlayer().sendMessage(NetworkCore.prefixStandard + ChatManager.translateFor("en", args.getNetworkPlayer(), "That is not a kind of server we support. Please try again."));
				}				
			}
		});
		
		
	}
	
	@Command(name = "directserver", usage = "/directserver {server name}")
	public void directServer(CommandArgs args) {
		if (args.getArgs().length == 1) {
			SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketDownloadOrionServerList("null", args.getNetworkPlayer().getPlayerRank().getValue(), json -> {
				Bukkit.broadcastMessage(json.toString());
				boolean found = false;
				for (String key : json.keySet()) {
					if (key.equalsIgnoreCase(args.getArgs(0)) && !key.equals("id")) {
						args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Found server " + key + ". Teleporting...");
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
								"sub teleport " + key + " " + args.getPlayer().getName());
						found = true;
						break;
					}
				}
				if (!found) {
					args.getPlayer().sendMessage(NetworkCore.prefixStandard + "The server you requested could not be found, or you cannot access this server at this time. Please try again later.");					
				}
			}));				
		} else {
			args.getPlayer().chat("/directserver <" + NetworkCore.prefixStandard + "What is the server id?>");
		}
	}
	
	@Command(name = "hub", aliases = { "lobby" }, usage = "/hub")
	public void hub(CommandArgs args) {
		Bukkit.getServer().dispatchCommand(args.getSender(), "join hub");
	}
	
	@Command(name = "changePassword", usage = "/changePassword {Current Password} {New Password}")
	public void changePassword(CommandArgs args) {
		if (args.getArgs().length != 1) {
			args.getPlayer().chat("/changePassword <Enter your previous password: > <Enter your NEW password>");
		} else {
			try {
				if (!args.getNetworkPlayer().getData().getString("password").equals(Encryption.decrypt(MD5.getMD5(args.getArgs(0)), Encryption.getKey()))) {
					args.getPlayer().sendMessage(NetworkCore.prefixError + "Incorrect password.");
					return;
				}
				args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Success! Your password was " + Encryption.decrypt(args.getNetworkPlayer().getData().getString("password"), Encryption.getKey()) + ", and is now: " + args.getArgs(1));
				SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(args.getPlayer().getUniqueId().toString(), "password", Encryption.encrypt(MD5.getMD5(args.getArgs(1)), Encryption.getKey())));
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
		}
	}
	
	@Command(name = "setPasswordPromptOnLogin", usage = "/setPasswordPromptOnLogin {true/false}")
	public void setpwpol(CommandArgs args) {
		if (args.getArgs().length != 1) {
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
		if (args.getNetworkPlayer().getData().has("password")) {
			if (args.getArgs().length != 1) {
				SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(args.getNetworkPlayer().getPlayer().getUniqueId().toString(), "authenticated", "false"));
				args.getPlayer().chat("/authenticate <Input Your Password: >");
			} else {
				try {
					if (MD5.getMD5(args.getArgs(0)).equals(Encryption.decrypt(args.getNetworkPlayer().getData().getString("password"), Encryption.getKey()))) {
						
						SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(args.getNetworkPlayer().getPlayer().getUniqueId().toString(), "authenticated", "true"));
						args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Successfully Authenticated.");
					} else {
						args.getPlayer().sendMessage(NetworkCore.prefixError + "Password incorrect. Please Try again.");
						args.getPlayer().chat("/authenticate <Input Your Password: >");
						return;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + "You do not have a password.");
			return;
		}
	}
	
	@Command(name = "setuppassword", usage = "/setuppassword")
	public void setupPassword(CommandArgs args) {
			if (!args.getNetworkPlayer().getData().has("password")) {
				if (args.getArgs().length != 3) {
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
						
						try {
							SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(args.getPlayer().getUniqueId().toString(), "password", Encryption.encrypt(MD5.getMD5(args.getArgs(2)), Encryption.getKey())));
						} catch (Exception e) {
							e.printStackTrace();
						}
						args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Password setup succeeded. Your password is: " + args.getArgs(2));
						args.getNetworkPlayer().reloadPlayerData();
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
