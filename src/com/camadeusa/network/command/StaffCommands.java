package com.camadeusa.network.command;

import java.text.SimpleDateFormat;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.json.JSONException;

import com.camadeusa.NetworkCore;
import com.camadeusa.chat.ChatManager;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.utility.Encryption;
import com.camadeusa.utility.MD5;
import com.camadeusa.utility.command.Command;
import com.camadeusa.utility.command.CommandArgs;
import com.camadeusa.utility.fetcher.UUIDFetcher;
import com.camadeusa.utility.menu.Inventory;
import com.camadeusa.utility.menu.InventoryRunnable;
import com.camadeusa.utility.menu.InventoryS;
import com.camadeusa.utility.menu.SlotItem;
import com.camadeusa.utility.subservers.packet.PacketPunishPlayer;
import com.camadeusa.utility.subservers.packet.PacketPunishPlayer.PunishType;

import net.ME1312.SubServers.Client.Bukkit.SubAPI;
import protocolsupport.libs.org.apache.commons.lang3.StringUtils;

public class StaffCommands {

	/*
	 * StaffCommands#kickPlayer()
	 * ---
	 * Makes sure has correct arguments
	 * makes sure commandsender has permission to use command
	 * makes sure sender knows password set for this user.
	 * assembles data necessary to make punishment
	 * sends punishment packet to be handled by bungee.
	 * 
	 */
	
	@Command(name = "punish", usage = "/punish")
	public void punishPlayer(CommandArgs args) throws NumberFormatException, JSONException, Exception {
		if (args.getNetworkPlayer().getPlayerRank().getValue() >= PlayerRank.Helper.getValue()) {

			if (args.getArgs().length < 1) {
				args.getPlayer().chat("/punish <What is your password?: (This is secure and will not be shared) > <Who would you like to punish?: (Player Name) > <For what type of punishment?: (kick/ban/mute) > <How Long?: (1-permanent) > <Units of time? (minutes/hours/days/weeks/months/permanent): > <For what reason?: >");
			} else if (args.getArgs().length > 1) {
				if (args.getNetworkPlayer().getData().has("password") && MD5.getMD5(args.getArgs(0)).equals(Encryption.decrypt(args.getNetworkPlayer().getData().getString("password"), Encryption.getKey()))) {
					String uuid = "";
					if (Bukkit.getPlayer(args.getArgs(1)).isOnline()) {
						uuid = Bukkit.getPlayer(args.getArgs(1)).getUniqueId().toString();
					} else if (Bukkit.getOfflinePlayer(args.getArgs(1)).hasPlayedBefore()) {
						uuid = Bukkit.getOfflinePlayer(args.getArgs(1)).getUniqueId().toString();
					} else {
						uuid = UUIDFetcher.getUUID(args.getArgs(1)).toString();
					}
					
					if (!uuid.isEmpty()) {
						PunishType modifier;
						if (args.getArgs(2).equalsIgnoreCase("kick") || args.getArgs(2).equalsIgnoreCase("ban") || args.getArgs(2).equalsIgnoreCase("mute")) {
							switch (args.getArgs(2).toLowerCase()) {
							case "kick":
								modifier = PunishType.KICK;
								break;
							case "ban":
								modifier = PunishType.BAN;
								break;
							case "mute":
								modifier = PunishType.MUTE;
								break;
								default:
									args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "Incorrect punishment type input. "));
									return;
							}
							boolean perm = false;
							int amount = 0;
							if (StringUtils.isNumeric(args.getArgs(3)) ||  args.getArgs(3).equalsIgnoreCase("permanent")) {
								if (args.getArgs(3).equalsIgnoreCase("permanent")) {
									perm = true;
									amount = 999;
								} else {
									amount = Integer.parseInt(args.getArgs(3));
								}
								
								double mult = 0;
								
								switch (args.getArgs(4).toLowerCase()) {
								case "minutes":
									mult = (1000 * 60);
									break;
								case "hours":
									mult = (1000 * 60 * 60);
									break;
								case "days":
									mult = (1000 * 60 * 60 * 24);
									break;
								case "weeks":
									mult = (1000 * 60 * 60 * 24 * 7);
									break;
								case "months":
									mult = (1000 * 60 * 60 * 24 * 30);
									break;							
								case "permanent":
									mult = -1;
									break;
								default:
									args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "Incorrect time units input. "));
									return;
										
								}
								
								String reason = "";
								for (int i = 5; i < args.getArgs().length; i++) {
									reason = reason + args.getArgs(i) + " ";
								}
								
								if (!reason.isEmpty()) {
																	
									if (perm || amount * mult < 0) {
										SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketPunishPlayer(uuid, modifier, Long.MAX_VALUE, reason, args.getPlayer().getUniqueId().toString()));																				
									} else {
										SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketPunishPlayer(uuid, modifier, (long) (System.currentTimeMillis() + (amount * mult)), reason, args.getPlayer().getUniqueId().toString()));										
									}
									
									args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Player " + args.getArgs(1) + " has been given action " + modifier + " until " + new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format((long) (System.currentTimeMillis() + (amount * mult))));
									
								} else {
									args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "You must supply a reason."));																								
								}
								
							} else {
								args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "You inputted an amount that was neither numeric or \"permanent\". "));															
							}
							
						} else {
							args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "Invalid Punishment Type Input."));							
						}
					} else {
						args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "Player could not be found."));
						
					}
					
				} else {
					args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "Invalid Password."));
				}
			}

		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "You do not have permission to use this command."));			
		}
	}
	
	@Command(name = "setRank", usage = "/setRank {Player Name} {Player Rank}")
	public void setRank(CommandArgs args) {
		if (args.getArgs().length != 2) {
			args.getPlayer().chat("/setRank <" + NetworkCore.prefixStandard + "What is the name of the player you wish to set?> <" + NetworkCore.prefixStandard + "What Rank?>");
		} else {
			NetworkPlayer target = null;
			if (Bukkit.getPlayer(args.getArgs(0)).isOnline()) {
				target = NetworkPlayer.getNetworkPlayerByUUID(Bukkit.getPlayer(args.getArgs(0)).getUniqueId().toString());
				PlayerRank selected = null;
				for (PlayerRank pr : PlayerRank.valuesordered()) {
					if (args.getArgs(1).equalsIgnoreCase(pr.toString()) && args.getNetworkPlayer().getPlayerRank().getValue() > pr.getValue()) {
						selected = pr;
						break;
					}
				}
				if (selected != null) {
					target.updateRank(selected);
					args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Rank Updated!");
				} else {
					args.getPlayer().sendMessage(NetworkCore.prefixError + "That rank could not be found or you do not have permission to set an equal or higher rank. Please try again.");
				}
			} else {
				args.getPlayer().sendMessage(NetworkCore.prefixError + "Setting the ranks of offline players has yet to be implemented. Please contact a developer if this is urgent, or try again later.");
			}
		}
	}
	
	@Command(name = "openPlayerManagmentMenu", usage = "/openPlayerManagmentMenu {Player Name}")
	public void oPMM(CommandArgs args) {
		if (args.getNetworkPlayer().getPlayerRank().getValue() >= PlayerRank.Helper.getValue()) {
			Inventory inv = new Inventory(args.getArgs(0) + "'s Player info:", 3);
			SlotItem item = new SlotItem("Player Information", args.getArgs(0), 0, Material.HOPPER);
			inv.addSlotItem(0, item);
			
			item.setOnClick(new InventoryRunnable() {
				@Override
				public void runOnClick(InventoryClickEvent e) {
					Bukkit.broadcastMessage("Test");
				}
				
			});
			
			InventoryS.registerInventory(NetworkCore.getInstance(), inv);
			InventoryS.openInventory(args.getPlayer(), args.getArgs(0) + "'s Player info:");
		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "You do not have permission to use this command."));			
		}
	}

	/*
	 * update /lookup player
	 * 
	 * add gui for in game staff managment.
	 *   
	 * add commands for setting language type, defaulting to their minecraft locale. 
	 * 
	 * 
	 */


	@Command(name = "checkdata", usage = "/checkdata")
	public void checkData(CommandArgs args) {
		
	}
}
