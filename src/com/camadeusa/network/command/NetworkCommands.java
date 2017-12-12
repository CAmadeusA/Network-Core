package com.camadeusa.network.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.JSONObject;

import com.camadeusa.NetworkCore;
import com.camadeusa.chat.ChatManager;
import com.camadeusa.module.game.Gamemode;
import com.camadeusa.network.ServerMode;
import com.camadeusa.network.ServerMode.ServerJoinMode;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.utility.Encryption;
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
	@Command(name = "join", aliases = { "server" }, usage = "/join {hub/uhcsg/etc}")
	public void join(CommandArgs args) {
		
		if (args.getArgs().length != 1) {
			args.getPlayer().chat("/join <What Kind of server would you like to join? (Hub/UHCSG): >");
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
					Gamemode seltemp = selected;
					SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketDownloadServerList(null, null, json -> {
						ArrayList<JSONObject> list = new ArrayList<>();
						AtomicLong timeSinceLast = new AtomicLong(System.currentTimeMillis());
						

						AtomicInteger ii = new AtomicInteger(0);
						for (int i = 0; i < json.getJSONObject("hosts").getJSONObject("~").getJSONObject("servers").keySet().size(); i++) {	
							ii.set(i);
							if (((String) json.getJSONObject("hosts").getJSONObject("~").getJSONObject("servers").keySet().toArray()[i]).replaceAll("[-+]?[0-9]*\\.?[0-9]+", "").equalsIgnoreCase(seltemp.getValue())) {
								SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketDownloadServerConfigInfo(((String) json.getJSONObject("hosts").getJSONObject("~").getJSONObject("servers").keySet().toArray()[i]), infojson -> {
									if (infojson.has("serverdata")) {
										if (ServerMode.canJoin(ServerJoinMode.fromString(infojson.getJSONObject("serverdata").getString("servermode")), args.getNetworkPlayer().getPlayerRank())) {
											if (infojson.getJSONObject("serverdata").getInt("onlineplayers") < infojson.getJSONObject("serverdata").getInt("maxplayers")) {
												if (list.size() == 0) {
													list.add(infojson);
												} else {
													if (list.get(0).getJSONObject("serverdata").getInt("onlineplayers") < infojson.getJSONObject("serverdata").getInt("onlineplayers")) {
														list.set(0, infojson);													
													}
												}
											} else {
												if (list.size() == 0) {
													list.set(0, infojson);
												}
											}											
										}
									} else {
										args.getPlayer().sendMessage(NetworkCore.prefixError + "No servers of that type are available or there is no data for that kind of server available. Please try again later.");
									}
									//Bukkit.broadcastMessage(ii.get() + " ||| " + json.getJSONObject("hosts").getJSONObject("~").getJSONObject("servers").keySet().size());
									if (ii.get() + 1 == json.getJSONObject("hosts").getJSONObject("~").getJSONObject("servers").keySet().size()) {
										if (list.size() > 0) {
											args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Sending you to server: " + list.get(0).getJSONObject("serverdata").getString("server") + ".");
											Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
													"sub teleport " + list.get(0).getJSONObject("serverdata").getString("server") + " " + args.getPlayer().getName());
											
										} else {
											args.getPlayer().sendMessage(NetworkCore.prefixStandard + "No servers of that type found.");
											
										}										
									}
								}));
							} else {
								timeSinceLast.set(System.currentTimeMillis());
							}
						}														
					}));				
				} else {
					args.getPlayer().sendMessage(ChatManager.translateFor("en", args.getNetworkPlayer(), "That is not a kind of server we support. Please try again."));
				}				
			}
		});
		
		
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
