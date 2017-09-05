package com.camadeusa.module.network.command;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.JSONArray;
import org.json.JSONObject;

import com.camadeusa.NetworkCore;
import com.camadeusa.player.ArchrPlayer;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.player.PlayerState;
import com.camadeusa.utility.command.Command;
import com.camadeusa.utility.command.CommandArgs;
import com.camadeusa.utility.fetcher.UUIDFetcher;
import com.google.gdata.data.spreadsheet.ListEntry;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class StaffCommands {

	@Command(name = "kick", aliases = { "kik", "boot", "gtfo", "bye", "slap" }, usage = "/kick {playername} {reason}")
	public void kickPlayer(CommandArgs args) throws Exception {
		if (args.getArgs().length > 1) {
			ArchrPlayer kicker = ArchrPlayer.getArchrPlayerByUUID(args.getPlayer().getUniqueId().toString());
			if (PlayerRank.canUseCommand(kicker.getPlayerRank(), "kick")) {
				if (Bukkit.getPlayer(args.getArgs(0)).isOnline()) {

					ArchrPlayer kicked = ArchrPlayer
							.getArchrPlayerByUUID(Bukkit.getPlayer(args.getArgs(0)).getUniqueId().toString());
					// if they are at least a helper, and the person they are kicking has a lower
					// rank than them.
					if (PlayerRank.getValueByRank(kicker.getPlayerRank()) > PlayerRank
							.getValueByRank(kicked.getPlayerRank())) {
						if (kicked != null) {
							String reason = "";
							for (int i = 1; i < args.getArgs().length; i++) {
								reason = reason + args.getArgs(i) + " ";
							}
							Map<String, Object> data = ArchrPlayer
									.getArchrPlayerByUUID(kicked.getPlayer().getUniqueId().toString()).getData();
							kicked.getPlayer().kickPlayer(reason);
							JSONArray kicks = new JSONArray((String) data.get("kicks").toString());
							JSONObject kick = new JSONObject();
							kick.put("kicker", args.getPlayer().getUniqueId().toString());
							kick.put("reason", reason);
							kick.put("time", System.currentTimeMillis());

							kicks.put(kick);

							data.put("kicks", kicks);

							Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
								@Override
								public void run() {
									try {
										ListEntry row = NetworkCore.getInstance().playersDB.getRow("uuid",
												kicked.getPlayer().getUniqueId());
										NetworkCore.getInstance().playersDB.updateRow(row, data);
										row.update();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
						}
					} else {
						args.getPlayer().sendMessage("No.");
					}
				} else {
					args.getPlayer().sendMessage(NetworkCore.prefixError + "Player is not online. ");
				}
			} else {
				args.getPlayer()
						.sendMessage(NetworkCore.prefixError + "You do not have permission to use this command.");
			}
		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + args.getCommand().getUsage());
		}
	}

	@Command(name = "mute", aliases = {
			"gag" }, usage = "/mute {playername} {integer/permanent} {units for integer} {reason}")
	public void mutePlayer(CommandArgs args) throws Exception {
		if (PlayerRank.canUseCommand(args.getArchrPlayer().getPlayerRank(), "mute")) {

			if (args.getArgs().length > 3) {
				ArchrPlayer muter = args.getArchrPlayer();
				// if they are at least a helper, and the person they are kicking has a lower
				// rank than them.
				if (Bukkit.getPlayer(args.getArgs(0)) != null) {
					ArchrPlayer muted = ArchrPlayer
							.getArchrPlayerByUUID(Bukkit.getPlayer(args.getArgs(0)).getUniqueId().toString());
					if (PlayerRank.getValueByRank(muter.getPlayerRank()) > PlayerRank
							.getValueByRank(muted.getPlayerRank())) {
						Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
							@Override
							public void run() {
								String reason = "";
								Long muteLength = 0L;
								if (args.getArgs(1).equalsIgnoreCase("permanent")) {
									for (int i = 2; i < args.getArgs().length; i++) {
										reason = reason + args.getArgs(i) + " ";
									}
									muteLength = 9999999999999L;
								} else {
									for (int i = 3; i < args.getArgs().length; i++) {
										reason = reason + args.getArgs(i) + " ";
									}
									if (args.getArgs(1).matches("\\d+")) {
										int num = Integer.parseInt(args.getArgs(1));
										switch (args.getArgs(2)) {
										case "minute":
											muteLength = (long) ((num * 1000) * 60);
											break;
										case "minutes":
											muteLength = (long) ((num * 1000) * 60);
											break;
										case "hour":
											muteLength = (long) (((num * 1000) * 60) * 60);
											break;
										case "hours":
											muteLength = (long) (((num * 1000) * 60) * 60);
											break;
										case "day":
											muteLength = (long) ((((num * 1000) * 60) * 60) * 24);
											break;
										case "days":
											muteLength = (long) ((((num * 1000) * 60) * 60) * 24);
											break;
										default:
											args.getPlayer()
													.sendMessage("You must supply minute(s), hour(s), or day(s)");
											break;
										}
									} else {
										args.getPlayer()
												.sendMessage("You must supply either \"permanent\" or a number.");
									}
								}

								muted.getData().put("muteexpiredate", muteLength + System.currentTimeMillis());

								JSONArray mutes = new JSONArray(muted.getData().get("mutes").toString());

								JSONObject mute = new JSONObject();
								mute.put("muter", args.getPlayer().getUniqueId().toString());
								mute.put("reason", reason);
								mute.put("time", System.currentTimeMillis());
								mute.put("amount", muteLength);
								mute.put("name", args.getPlayer().getName());

								mutes.put(mute);
								Date date = new Date(System.currentTimeMillis() + muteLength);
								String myDateStr = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(date);

								muted.getData().put("mutes", mutes);

								try {
									ListEntry row = NetworkCore.getInstance().playersDB.getRow("uuid",
											muted.getPlayer().getUniqueId().toString());
									NetworkCore.getInstance().playersDB.updateRow(row, muted.getData());
									row.update();

									muter.getPlayer().sendMessage(
											NetworkCore.prefixStandard + "Player " + muted.getPlayer().getName()
													+ " for " + reason + " until " + myDateStr + ". ");
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						});

					}
				} else {
					OfflinePlayer oP = Bukkit.getOfflinePlayer(args.getArgs(0));
					if (oP.hasPlayedBefore()) {
						try {
							Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
								@Override
								public void run() {

									String reason = "";
									Long muteLength = 0L;
									if (args.getArgs(1).equalsIgnoreCase("permanent")) {
										for (int i = 2; i < args.getArgs().length; i++) {
											reason = reason + args.getArgs(i) + " ";
										}
										muteLength = 9999999999999L;
									} else {
										for (int i = 3; i < args.getArgs().length; i++) {
											reason = reason + args.getArgs(i) + " ";
										}
										if (args.getArgs(1).matches("\\d+")) {
											int num = Integer.parseInt(args.getArgs(1));
											switch (args.getArgs(2)) {
											case "minute":
												muteLength = (long) ((num * 1000) * 60);
												break;
											case "minutes":
												muteLength = (long) ((num * 1000) * 60);
												break;
											case "hour":
												muteLength = (long) (((num * 1000) * 60) * 60);
												break;
											case "hours":
												muteLength = (long) (((num * 1000) * 60) * 60);
												break;
											case "day":
												muteLength = (long) ((((num * 1000) * 60) * 60) * 24);
												break;
											case "days":
												muteLength = (long) ((((num * 1000) * 60) * 60) * 24);
												break;
											default:
												args.getPlayer()
														.sendMessage("You must supply minute(s), hour(s), or day(s)");
												break;
											}
										} else {
											args.getPlayer()
													.sendMessage("You must supply either \"permanent\" or a number.");
										}
									}

									ListEntry row = null;
									try {
										row = NetworkCore.getInstance().playersDB.getRow("uuid", oP.getUniqueId());
									} catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									Map<String, Object> data = NetworkCore.getInstance().playersDB.getRowData(row);
									data.put("muteexpiredate", muteLength + System.currentTimeMillis());
									JSONArray mutes = new JSONArray((String) data.get("mutes"));

									JSONObject mute = new JSONObject();
									mute.put("muter", args.getPlayer().getUniqueId().toString());
									mute.put("reason", reason);
									mute.put("time", System.currentTimeMillis());
									mute.put("amount", muteLength);
									mute.put("name", args.getPlayer().getName());

									mutes.put(mute);
									Date date = new Date(System.currentTimeMillis() + muteLength);
									String myDateStr = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(date);

									data.put("mutes", mutes);

									try {
										NetworkCore.getInstance().playersDB.updateRow(row, data);
										row.update();

										muter.getPlayer().sendMessage(
												NetworkCore.prefixStandard + "Player " + data.get("username") + " for "
														+ reason + " until " + myDateStr + ". ");
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});

						} catch (Exception e) {

							Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
								@Override
								public void run() {

									UUID uuid = UUIDFetcher.getUUID(args.getArgs(0));
									if (uuid == null) {
										muter.getPlayer().sendMessage(
												NetworkCore.prefixStandard + "That is not a player. Try again.");
										return;
									}

									Map<String, Object> data = ArchrPlayer.generateBaseDBData(uuid.toString(),
											args.getArgs(0), PlayerRank.Player.toString(), "0", -1, -1, 0L);
									NetworkCore.getInstance().playersDB.addData(data);

									String reason = "";
									Long muteLength = 0L;
									if (args.getArgs(1).equalsIgnoreCase("permanent")) {
										for (int i = 2; i < args.getArgs().length; i++) {
											reason = reason + args.getArgs(i) + " ";
										}
										muteLength = 9999999999999L;
									} else {
										for (int i = 3; i < args.getArgs().length; i++) {
											reason = reason + args.getArgs(i) + " ";
										}
										if (args.getArgs(1).matches("\\d+")) {
											int num = Integer.parseInt(args.getArgs(1));
											switch (args.getArgs(2)) {
											case "minute":
												muteLength = (long) ((num * 1000) * 60);
												break;
											case "minutes":
												muteLength = (long) ((num * 1000) * 60);
												break;
											case "hour":
												muteLength = (long) (((num * 1000) * 60) * 60);
												break;
											case "hours":
												muteLength = (long) (((num * 1000) * 60) * 60);
												break;
											case "day":
												muteLength = (long) ((((num * 1000) * 60) * 60) * 24);
												break;
											case "days":
												muteLength = (long) ((((num * 1000) * 60) * 60) * 24);
												break;
											default:
												args.getPlayer()
														.sendMessage("You must supply minute(s), hour(s), or day(s)");
												break;
											}
										} else {
											args.getPlayer()
													.sendMessage("You must supply either \"permanent\" or a number.");
										}
									}

									data.put("muteexpiredate", muteLength + System.currentTimeMillis());
									JSONArray mutes = new JSONArray((String) data.get("mutes"));

									JSONObject mute = new JSONObject();
									mute.put("muter", args.getPlayer().getUniqueId().toString());
									mute.put("reason", reason);
									mute.put("time", System.currentTimeMillis());
									mute.put("amount", muteLength);
									mute.put("name", args.getPlayer().getName());

									mutes.put(mute);
									Date date = new Date(System.currentTimeMillis() + muteLength);
									String myDateStr = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(date);

									data.put("mutes", mutes);

									try {
										ListEntry row = NetworkCore.getInstance().playersDB.getRow("uuid", uuid);
										NetworkCore.getInstance().playersDB.updateRow(row, data);
										row.update();

										muter.getPlayer().sendMessage(
												NetworkCore.prefixStandard + "Player " + data.get("username") + " for "
														+ reason + " until " + myDateStr + ". ");
									} catch (Exception ex) {
										// TODO Auto-generated catch block
										ex.printStackTrace();
									}
								}
							});
						}

					} else {
						Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
							@Override
							public void run() {

								UUID uuid = UUIDFetcher.getUUID(args.getArgs(0));
								if (uuid == null) {
									muter.getPlayer().sendMessage(
											NetworkCore.prefixStandard + "That is not a player. Try again.");
									return;
								}

								Map<String, Object> data;
								try {
									ListEntry row = NetworkCore.getInstance().playersDB.getRow("uuid", uuid);
									if (row == null) {
										data = ArchrPlayer.generateBaseDBData(uuid.toString(), args.getArgs(0),
												PlayerRank.Player.toString(), "0", -1, -1, 0L);
										NetworkCore.getInstance().playersDB.addData(data);
									} else {
										data = NetworkCore.getInstance().playersDB.getRowData(row);
									}
								} catch (Exception e) {
									data = ArchrPlayer.generateBaseDBData(uuid.toString(), args.getArgs(0),
											PlayerRank.Player.toString(), "0", -1, -1, 0L);
									NetworkCore.getInstance().playersDB.addData(data);
									e.printStackTrace();
								}

								String reason = "";
								Long muteLength = 0L;
								if (args.getArgs(1).equalsIgnoreCase("permanent")) {
									for (int i = 2; i < args.getArgs().length; i++) {
										reason = reason + args.getArgs(i) + " ";
									}
									muteLength = 9999999999999L;
								} else {
									for (int i = 3; i < args.getArgs().length; i++) {
										reason = reason + args.getArgs(i) + " ";
									}
									if (args.getArgs(1).matches("\\d+")) {
										int num = Integer.parseInt(args.getArgs(1));
										switch (args.getArgs(2)) {
										case "minute":
											muteLength = (long) ((num * 1000) * 60);
											break;
										case "minutes":
											muteLength = (long) ((num * 1000) * 60);
											break;
										case "hour":
											muteLength = (long) (((num * 1000) * 60) * 60);
											break;
										case "hours":
											muteLength = (long) (((num * 1000) * 60) * 60);
											break;
										case "day":
											muteLength = (long) ((((num * 1000) * 60) * 60) * 24);
											break;
										case "days":
											muteLength = (long) ((((num * 1000) * 60) * 60) * 24);
											break;
										default:
											args.getPlayer()
													.sendMessage("You must supply minute(s), hour(s), or day(s)");
											break;
										}
									} else {
										args.getPlayer()
												.sendMessage("You must supply either \"permanent\" or a number.");
									}
								}

								data.put("muteexpiredate", muteLength + System.currentTimeMillis());
								JSONArray mutes = new JSONArray((String) data.get("mutes"));

								JSONObject mute = new JSONObject();
								mute.put("muter", args.getPlayer().getUniqueId().toString());
								mute.put("reason", reason);
								mute.put("time", System.currentTimeMillis());
								mute.put("amount", muteLength);
								mute.put("name", args.getPlayer().getName());

								mutes.put(mute);
								Date date = new Date(System.currentTimeMillis() + muteLength);
								String myDateStr = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(date);

								data.put("mutes", mutes);

								try {
									ListEntry row = NetworkCore.getInstance().playersDB.getRow("uuid", uuid);
									NetworkCore.getInstance().playersDB.updateRow(row, data);
									row.update();

									muter.getPlayer().sendMessage(NetworkCore.prefixStandard + "Muted Player "
											+ args.getArgs(0) + " for " + reason + " until " + myDateStr + ". ");

								} catch (Exception ex) {
									// TODO Auto-generated catch block
									ex.printStackTrace();
								}
							}
						});
					}
				}

			} else {
				args.getPlayer().sendMessage(args.getCommand().getUsage());
			}
		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + "You do not have permission to use this command.");
		}
	}

	@Command(name = "ban", aliases = {
			"banhammer" }, usage = "/ban {playername} {integer/permanent} {units for integer} {reason}")
	public void banPlayer(CommandArgs args) throws Exception {
		if (PlayerRank.canUseCommand(args.getArchrPlayer().getPlayerRank(), "ban")) {
			if (args.getArgs().length > 3) {
				ArchrPlayer banner = args.getArchrPlayer();
				// if they are at least a helper, and the person they are kicking has a lower
				// rank than them.
				if (Bukkit.getPlayer(args.getArgs(0)) != null) {
					ArchrPlayer banned = ArchrPlayer
							.getArchrPlayerByUUID(Bukkit.getPlayer(args.getArgs(0)).getUniqueId().toString());
					if (PlayerRank.getValueByRank(banner.getPlayerRank()) > PlayerRank
							.getValueByRank(banned.getPlayerRank())) {
						Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
							@Override
							public void run() {
								String reason = "";
								Long banLength = 0L;
								if (args.getArgs(1).equalsIgnoreCase("permanent")) {
									for (int i = 2; i < args.getArgs().length; i++) {
										reason = reason + args.getArgs(i) + " ";
									}
									banLength = 9999999999999L;
								} else {
									for (int i = 3; i < args.getArgs().length; i++) {
										reason = reason + args.getArgs(i) + " ";
									}
									if (args.getArgs(1).matches("\\d+")) {
										int num = Integer.parseInt(args.getArgs(1));
										switch (args.getArgs(2)) {
										case "minute":
											banLength = (long) ((num * 1000) * 60);
											break;
										case "minutes":
											banLength = (long) ((num * 1000) * 60);
											break;
										case "hour":
											banLength = (long) (((num * 1000) * 60) * 60);
											break;
										case "hours":
											banLength = (long) (((num * 1000) * 60) * 60);
											break;
										case "day":
											banLength = (long) ((((num * 1000) * 60) * 60) * 24);
											break;
										case "days":
											banLength = (long) ((((num * 1000) * 60) * 60) * 24);
											break;
										default:
											args.getPlayer()
													.sendMessage("You must supply minute(s), hour(s), or day(s)");
											break;
										}
									} else {
										args.getPlayer()
												.sendMessage("You must supply either \"permanent\" or a number.");
									}
								}

								banned.getData().put("banexpiredate", banLength + System.currentTimeMillis());

								JSONArray bans = new JSONArray((String) banned.getData().get("bans"));

								JSONObject ban = new JSONObject();
								ban.put("banner", args.getPlayer().getUniqueId().toString());
								ban.put("reason", reason);
								ban.put("time", System.currentTimeMillis());
								ban.put("amount", banLength);
								ban.put("name", args.getPlayer().getName());

								bans.put(ban);
								Date date = new Date(System.currentTimeMillis() + banLength);
								String myDateStr = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(date);

								banned.getData().put("bans", bans);

								try {
									ListEntry row = NetworkCore.getInstance().playersDB.getRow("uuid",
											banned.getPlayer().getUniqueId().toString());
									NetworkCore.getInstance().playersDB.updateRow(row, banned.getData());
									row.update();

									banner.getPlayer()
											.sendMessage(NetworkCore.prefixStandard + "Player "
													+ banned.getPlayer().getName() + " for " + reason + " until "
													+ myDateStr + ". ");
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						});

					}
				} else {
					OfflinePlayer oP = Bukkit.getOfflinePlayer(args.getArgs(0));
					if (oP.hasPlayedBefore()) {
						try {
							Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
								@Override
								public void run() {

									String reason = "";
									Long banLength = 0L;
									if (args.getArgs(1).equalsIgnoreCase("permanent")) {
										for (int i = 2; i < args.getArgs().length; i++) {
											reason = reason + args.getArgs(i) + " ";
										}
										banLength = 9999999999999L;
									} else {
										for (int i = 3; i < args.getArgs().length; i++) {
											reason = reason + args.getArgs(i) + " ";
										}
										if (args.getArgs(1).matches("\\d+")) {
											int num = Integer.parseInt(args.getArgs(1));
											switch (args.getArgs(2)) {
											case "minute":
												banLength = (long) ((num * 1000) * 60);
												break;
											case "minutes":
												banLength = (long) ((num * 1000) * 60);
												break;
											case "hour":
												banLength = (long) (((num * 1000) * 60) * 60);
												break;
											case "hours":
												banLength = (long) (((num * 1000) * 60) * 60);
												break;
											case "day":
												banLength = (long) ((((num * 1000) * 60) * 60) * 24);
												break;
											case "days":
												banLength = (long) ((((num * 1000) * 60) * 60) * 24);
												break;
											default:
												args.getPlayer()
														.sendMessage("You must supply minute(s), hour(s), or day(s)");
												break;
											}
										} else {
											args.getPlayer()
													.sendMessage("You must supply either \"permanent\" or a number.");
										}
									}

									ListEntry row = null;
									try {
										row = NetworkCore.getInstance().playersDB.getRow("uuid", oP.getUniqueId());
									} catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									Map<String, Object> data = NetworkCore.getInstance().playersDB.getRowData(row);
									data.put("banexpiredate", banLength + System.currentTimeMillis());
									JSONArray bans = new JSONArray((String) data.get("bans"));

									JSONObject ban = new JSONObject();
									ban.put("banner", args.getPlayer().getUniqueId().toString());
									ban.put("reason", reason);
									ban.put("time", System.currentTimeMillis());
									ban.put("amount", banLength);
									ban.put("name", args.getPlayer().getName());

									bans.put(ban);
									Date date = new Date(System.currentTimeMillis() + banLength);
									String myDateStr = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(date);

									data.put("bans", bans);

									try {
										NetworkCore.getInstance().playersDB.updateRow(row, data);
										row.update();

										banner.getPlayer().sendMessage(
												NetworkCore.prefixStandard + "Player " + data.get("username") + " for "
														+ reason + " until " + myDateStr + ". ");
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});

						} catch (Exception e) {

							Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
								@Override
								public void run() {

									UUID uuid = UUIDFetcher.getUUID(args.getArgs(0));
									if (uuid == null) {
										banner.getPlayer().sendMessage(
												NetworkCore.prefixStandard + "That is not a player. Try again.");
										return;
									}

									Map<String, Object> data = ArchrPlayer.generateBaseDBData(uuid.toString(),
											args.getArgs(0), PlayerRank.Player.toString(), "0", -1, -1, 0L);
									NetworkCore.getInstance().playersDB.addData(data);

									String reason = "";
									Long banLength = 0L;
									if (args.getArgs(1).equalsIgnoreCase("permanent")) {
										for (int i = 2; i < args.getArgs().length; i++) {
											reason = reason + args.getArgs(i) + " ";
										}
										banLength = 9999999999999L;
									} else {
										for (int i = 3; i < args.getArgs().length; i++) {
											reason = reason + args.getArgs(i) + " ";
										}
										if (args.getArgs(1).matches("\\d+")) {
											int num = Integer.parseInt(args.getArgs(1));
											switch (args.getArgs(2)) {
											case "minute":
												banLength = (long) ((num * 1000) * 60);
												break;
											case "minutes":
												banLength = (long) ((num * 1000) * 60);
												break;
											case "hour":
												banLength = (long) (((num * 1000) * 60) * 60);
												break;
											case "hours":
												banLength = (long) (((num * 1000) * 60) * 60);
												break;
											case "day":
												banLength = (long) ((((num * 1000) * 60) * 60) * 24);
												break;
											case "days":
												banLength = (long) ((((num * 1000) * 60) * 60) * 24);
												break;
											default:
												args.getPlayer()
														.sendMessage("You must supply minute(s), hour(s), or day(s)");
												break;
											}
										} else {
											args.getPlayer()
													.sendMessage("You must supply either \"permanent\" or a number.");
										}
									}

									data.put("banexpiredate", banLength + System.currentTimeMillis());
									JSONArray bans = new JSONArray((String) data.get("bans"));

									JSONObject ban = new JSONObject();
									ban.put("banner", args.getPlayer().getUniqueId().toString());
									ban.put("reason", reason);
									ban.put("time", System.currentTimeMillis());
									ban.put("amount", banLength);
									ban.put("name", args.getPlayer().getName());

									bans.put(ban);
									Date date = new Date(System.currentTimeMillis() + banLength);
									String myDateStr = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(date);

									data.put("bans", bans);

									try {
										ListEntry row = NetworkCore.getInstance().playersDB.getRow("uuid", uuid);
										NetworkCore.getInstance().playersDB.updateRow(row, data);
										row.update();

										banner.getPlayer().sendMessage(
												NetworkCore.prefixStandard + "Player " + data.get("username") + " for "
														+ reason + " until " + myDateStr + ". ");
									} catch (Exception ex) {
										// TODO Auto-generated catch block
										ex.printStackTrace();
									}
								}
							});
						}

					} else {
						Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
							@Override
							public void run() {

								UUID uuid = UUIDFetcher.getUUID(args.getArgs(0));
								if (uuid == null) {
									banner.getPlayer().sendMessage(
											NetworkCore.prefixStandard + "That is not a player. Try again.");
									return;
								}

								Map<String, Object> data;
								try {
									ListEntry row = NetworkCore.getInstance().playersDB.getRow("uuid", uuid);
									if (row == null) {
										data = ArchrPlayer.generateBaseDBData(uuid.toString(), args.getArgs(0),
												PlayerRank.Player.toString(), "0", -1, -1, 0L);
										NetworkCore.getInstance().playersDB.addData(data);
									} else {
										data = NetworkCore.getInstance().playersDB.getRowData(row);
									}
								} catch (Exception e) {
									data = ArchrPlayer.generateBaseDBData(uuid.toString(), args.getArgs(0),
											PlayerRank.Player.toString(), "0", -1, -1, 0L);
									NetworkCore.getInstance().playersDB.addData(data);
									e.printStackTrace();
								}

								String reason = "";
								Long banLength = 0L;
								if (args.getArgs(1).equalsIgnoreCase("permanent")) {
									for (int i = 2; i < args.getArgs().length; i++) {
										reason = reason + args.getArgs(i) + " ";
									}
									banLength = 9999999999999L;
								} else {
									for (int i = 3; i < args.getArgs().length; i++) {
										reason = reason + args.getArgs(i) + " ";
									}
									if (args.getArgs(1).matches("\\d+")) {
										int num = Integer.parseInt(args.getArgs(1));
										switch (args.getArgs(2)) {
										case "minute":
											banLength = (long) ((num * 1000) * 60);
											break;
										case "minutes":
											banLength = (long) ((num * 1000) * 60);
											break;
										case "hour":
											banLength = (long) (((num * 1000) * 60) * 60);
											break;
										case "hours":
											banLength = (long) (((num * 1000) * 60) * 60);
											break;
										case "day":
											banLength = (long) ((((num * 1000) * 60) * 60) * 24);
											break;
										case "days":
											banLength = (long) ((((num * 1000) * 60) * 60) * 24);
											break;
										default:
											args.getPlayer()
													.sendMessage("You must supply minute(s), hour(s), or day(s)");
											break;
										}
									} else {
										args.getPlayer()
												.sendMessage("You must supply either \"permanent\" or a number.");
									}
								}

								data.put("banexpiredate", banLength + System.currentTimeMillis());
								JSONArray bans = new JSONArray((String) data.get("bans"));

								JSONObject ban = new JSONObject();
								ban.put("banner", args.getPlayer().getUniqueId().toString());
								ban.put("reason", reason);
								ban.put("time", System.currentTimeMillis());
								ban.put("amount", banLength);
								ban.put("name", args.getPlayer().getName());

								bans.put(ban);
								Date date = new Date(System.currentTimeMillis() + banLength);
								String myDateStr = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(date);

								data.put("bans", bans);

								try {
									ListEntry row = NetworkCore.getInstance().playersDB.getRow("uuid", uuid);
									NetworkCore.getInstance().playersDB.updateRow(row, data);
									row.update();

									banner.getPlayer().sendMessage(NetworkCore.prefixStandard + "Banned Player "
											+ args.getArgs(0) + " for " + reason + " until " + myDateStr + ". ");

								} catch (Exception ex) {
									// TODO Auto-generated catch block
									ex.printStackTrace();
								}
							}
						});
					}
				}
			} else {
				args.getPlayer().sendMessage(args.getCommand().getUsage());
			}

		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + "You do not have permission to use this command.");
		}

	}

	@Command(name = "lookup", usage = "/lookup {playername}")
	public void lookupPlayer(CommandArgs args) throws Exception {
		if (PlayerRank.canUseCommand(args.getArchrPlayer().getPlayerRank(), "lookup")) {
			if (Bukkit.getOnlinePlayers().contains(args.getArgs(0))) {

				ArchrPlayer lookedup = ArchrPlayer
						.getArchrPlayerByUUID(Bukkit.getPlayer(args.getArgs(0)).getUniqueId().toString());
				if (PlayerRank
						.getValueByRank(ArchrPlayer.getArchrPlayerByUUID(lookedup.getPlayer().getUniqueId().toString())
								.getPlayerRank()) < PlayerRank.getValueByRank(args.getArchrPlayer().getPlayerRank())) {

					JSONArray jsonArrayKicks = new JSONArray(lookedup.getData().get("kicks").toString());
					JSONArray jsonArrayMutes = new JSONArray(lookedup.getData().get("mutes").toString());
					JSONArray jsonArrayBans = new JSONArray(lookedup.getData().get("bans").toString());

					args.getPlayer().sendMessage(NetworkCore.prefixStandard + "");
					args.getPlayer().sendMessage(NetworkCore.prefixStandard + "");
					args.getPlayer().sendMessage(NetworkCore.prefixStandard + "-=Kicks=-");
					if (jsonArrayKicks.length() > 0) {
						for (int i = 0; i < jsonArrayKicks.length(); i++) {
							JSONObject entry = jsonArrayKicks.getJSONObject(i);
							args.getPlayer().sendMessage(NetworkCore.prefixStandard + "--- Entry: " + (i + 1) + " ---");
							args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Kicker: " + entry.get("name")
									+ " with uuid: " + entry.get("kicker"));
							args.getPlayer().sendMessage(
									NetworkCore.prefixStandard + "Time: " + new SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
											.format(new Date(Long.parseLong(entry.get("time").toString()))));

							args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Reason: " + entry.get("reason"));

						}
					} else {
						args.getPlayer().sendMessage(NetworkCore.prefixError + "No Data Found.");
					}
					args.getPlayer().sendMessage(NetworkCore.prefixStandard + "");
					args.getPlayer().sendMessage(NetworkCore.prefixStandard + "");
					args.getPlayer().sendMessage(NetworkCore.prefixStandard + "-=Mutes=-");
					if (jsonArrayMutes.length() > 0) {
						for (int i = 0; i < jsonArrayMutes.length(); i++) {
							JSONObject entry = jsonArrayMutes.getJSONObject(i);
							args.getPlayer().sendMessage(NetworkCore.prefixStandard + "--- Entry: " + (i + 1) + " ---");
							args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Muter: "
									+ entry.getString("name") + " with uuid: " + entry.getString("muter"));
							args.getPlayer().sendMessage(
									NetworkCore.prefixStandard + "Time: " + new SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
											.format(new Date(Long.parseLong(entry.get("time").toString()))));

							long time = Long.parseLong(entry.get("time").toString());
							long amount = Long.parseLong(entry.get("amount").toString());

							args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Lifted Time: "
									+ new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date(time + amount)));
							args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Reason: " + entry.get("reason"));
						}
					} else {
						args.getPlayer().sendMessage(NetworkCore.prefixError + "No Data Found.");
					}

					args.getPlayer().sendMessage(NetworkCore.prefixStandard + "");
					args.getPlayer().sendMessage(NetworkCore.prefixStandard + "");
					args.getPlayer().sendMessage(NetworkCore.prefixStandard + "-=Bans=-");
					if (jsonArrayBans.length() > 0) {
						for (int i = 0; i < jsonArrayBans.length(); i++) {
							JSONObject entry = jsonArrayBans.getJSONObject(i);
							args.getPlayer().sendMessage(NetworkCore.prefixStandard + "--- Entry: " + (i + 1) + " ---");
							args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Banner: " + entry.get("name")
									+ " with uuid: " + entry.get("banner"));
							args.getPlayer().sendMessage(
									NetworkCore.prefixStandard + "Time: " + new SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
											.format(new Date(Long.parseLong(entry.get("time").toString()))));

							long time = Long.parseLong(entry.get("time").toString());
							long amount = Long.parseLong(entry.get("amount").toString());

							args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Lifted Time: "
									+ new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date(time + amount)));
							args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Reason: " + entry.get("reason"));
						}
					} else {
						args.getPlayer().sendMessage(NetworkCore.prefixError + "No Data Found.");
					}
				}

			} else {
				OfflinePlayer op = Bukkit.getOfflinePlayer(args.getArgs(0));
				if (op.hasPlayedBefore()) {
					Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
						@Override
						public void run() {
							try {
								ListEntry row = NetworkCore.getInstance().playersDB.getRow("uuid",
										op.getUniqueId().toString());
								Map<String, Object> data = NetworkCore.getInstance().playersDB.getRowData(row);
								JSONArray jsonArrayKicks = new JSONArray(data.get("kicks").toString());
								JSONArray jsonArrayMutes = new JSONArray(data.get("mutes").toString());
								JSONArray jsonArrayBans = new JSONArray(data.get("bans").toString());

								args.getPlayer().sendMessage(NetworkCore.prefixStandard + "");
								args.getPlayer().sendMessage(NetworkCore.prefixStandard + "");
								args.getPlayer().sendMessage(NetworkCore.prefixStandard + "-=Kicks=-");
								if (jsonArrayKicks.length() > 0) {
									for (int i = 0; i < jsonArrayKicks.length(); i++) {
										JSONObject entry = jsonArrayKicks.getJSONObject(i);
										args.getPlayer().sendMessage(
												NetworkCore.prefixStandard + "--- Entry: " + (i + 1) + " ---");
										args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Kicker: "
												+ entry.get("name") + " with uuid: " + entry.get("kicker"));
										args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Time: "
												+ new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(
														new Date(Long.parseLong(entry.get("time").toString()))));

										args.getPlayer().sendMessage(
												NetworkCore.prefixStandard + "Reason: " + entry.get("reason"));

									}
								} else {
									args.getPlayer().sendMessage(NetworkCore.prefixError + "No Data Found.");
								}
								args.getPlayer().sendMessage(NetworkCore.prefixStandard + "");
								args.getPlayer().sendMessage(NetworkCore.prefixStandard + "");
								args.getPlayer().sendMessage(NetworkCore.prefixStandard + "-=Mutes=-");
								if (jsonArrayMutes.length() > 0) {
									for (int i = 0; i < jsonArrayMutes.length(); i++) {
										JSONObject entry = jsonArrayMutes.getJSONObject(i);
										args.getPlayer().sendMessage(
												NetworkCore.prefixStandard + "--- Entry: " + (i + 1) + " ---");
										args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Muter: "
												+ entry.getString("name") + " with uuid: " + entry.getString("muter"));
										args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Time: "
												+ new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(
														new Date(Long.parseLong(entry.get("time").toString()))));

										long time = Long.parseLong(entry.get("time").toString());
										long amount = Long.parseLong(entry.get("amount").toString());

										args.getPlayer()
												.sendMessage(NetworkCore.prefixStandard + "Lifted Time: "
														+ new SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
																.format(new Date(time + amount)));
										args.getPlayer().sendMessage(
												NetworkCore.prefixStandard + "Reason: " + entry.get("reason"));
									}
								} else {
									args.getPlayer().sendMessage(NetworkCore.prefixError + "No Data Found.");
								}

								args.getPlayer().sendMessage(NetworkCore.prefixStandard + "");
								args.getPlayer().sendMessage(NetworkCore.prefixStandard + "");
								args.getPlayer().sendMessage(NetworkCore.prefixStandard + "-=Bans=-");
								if (jsonArrayBans.length() > 0) {
									for (int i = 0; i < jsonArrayBans.length(); i++) {
										JSONObject entry = jsonArrayBans.getJSONObject(i);
										args.getPlayer().sendMessage(
												NetworkCore.prefixStandard + "--- Entry: " + (i + 1) + " ---");
										args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Banner: "
												+ entry.get("name") + " with uuid: " + entry.get("banner"));
										args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Time: "
												+ new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(
														new Date(Long.parseLong(entry.get("time").toString()))));

										long time = Long.parseLong(entry.get("time").toString());
										long amount = Long.parseLong(entry.get("amount").toString());

										args.getPlayer()
												.sendMessage(NetworkCore.prefixStandard + "Lifted Time: "
														+ new SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
																.format(new Date(time + amount)));
										args.getPlayer().sendMessage(
												NetworkCore.prefixStandard + "Reason: " + entry.get("reason"));
									}
								} else {
									args.getPlayer().sendMessage(NetworkCore.prefixError + "No Data Found.");
								}

							} catch (Exception e) {
								e.printStackTrace();
								args.getPlayer()
										.sendMessage(NetworkCore.prefixError + "Player has no data in the database.");
							}

						}
					});
				} else {
					Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
						@Override
						public void run() {
							UUID uuid = UUIDFetcher.getUUID(args.getArgs(0));
							if (uuid != null) {
								ListEntry row;
								try {
									row = NetworkCore.getInstance().playersDB.getRow("uuid", uuid.toString());
									Map<String, Object> data = NetworkCore.getInstance().playersDB.getRowData(row);
									JSONArray jsonArrayKicks = new JSONArray(data.get("kicks").toString());
									JSONArray jsonArrayMutes = new JSONArray(data.get("mutes").toString());
									JSONArray jsonArrayBans = new JSONArray(data.get("bans").toString());

									args.getPlayer().sendMessage(NetworkCore.prefixStandard + "");
									args.getPlayer().sendMessage(NetworkCore.prefixStandard + "");
									args.getPlayer().sendMessage(NetworkCore.prefixStandard + "-=Kicks=-");
									if (jsonArrayKicks.length() > 0) {
										for (int i = 0; i < jsonArrayKicks.length(); i++) {
											JSONObject entry = jsonArrayKicks.getJSONObject(i);
											args.getPlayer().sendMessage(
													NetworkCore.prefixStandard + "--- Entry: " + (i + 1) + " ---");
											args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Kicker: "
													+ entry.get("name") + " with uuid: " + entry.get("kicker"));
											args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Time: "
													+ new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(
															new Date(Long.parseLong(entry.get("time").toString()))));

											args.getPlayer().sendMessage(
													NetworkCore.prefixStandard + "Reason: " + entry.get("reason"));

										}
									} else {
										args.getPlayer().sendMessage(NetworkCore.prefixError + "No Data Found.");
									}
									args.getPlayer().sendMessage(NetworkCore.prefixStandard + "");
									args.getPlayer().sendMessage(NetworkCore.prefixStandard + "");
									args.getPlayer().sendMessage(NetworkCore.prefixStandard + "-=Mutes=-");
									if (jsonArrayMutes.length() > 0) {
										for (int i = 0; i < jsonArrayMutes.length(); i++) {
											JSONObject entry = jsonArrayMutes.getJSONObject(i);
											args.getPlayer().sendMessage(
													NetworkCore.prefixStandard + "--- Entry: " + (i + 1) + " ---");
											args.getPlayer()
													.sendMessage(NetworkCore.prefixStandard + "Muter: "
															+ entry.getString("name") + " with uuid: "
															+ entry.getString("muter"));
											args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Time: "
													+ new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(
															new Date(Long.parseLong(entry.get("time").toString()))));

											long time = Long.parseLong(entry.get("time").toString());
											long amount = Long.parseLong(entry.get("amount").toString());

											args.getPlayer()
													.sendMessage(NetworkCore.prefixStandard + "Lifted Time: "
															+ new SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
																	.format(new Date(time + amount)));
											args.getPlayer().sendMessage(
													NetworkCore.prefixStandard + "Reason: " + entry.get("reason"));
										}
									} else {
										args.getPlayer().sendMessage(NetworkCore.prefixError + "No Data Found.");
									}

									args.getPlayer().sendMessage(NetworkCore.prefixStandard + "");
									args.getPlayer().sendMessage(NetworkCore.prefixStandard + "");
									args.getPlayer().sendMessage(NetworkCore.prefixStandard + "-=Bans=-");
									if (jsonArrayBans.length() > 0) {
										for (int i = 0; i < jsonArrayBans.length(); i++) {
											JSONObject entry = jsonArrayBans.getJSONObject(i);
											args.getPlayer().sendMessage(
													NetworkCore.prefixStandard + "--- Entry: " + (i + 1) + " ---");
											args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Banner: "
													+ entry.get("name") + " with uuid: " + entry.get("banner"));
											args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Time: "
													+ new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(
															new Date(Long.parseLong(entry.get("time").toString()))));

											long time = Long.parseLong(entry.get("time").toString());
											long amount = Long.parseLong(entry.get("amount").toString());

											args.getPlayer()
													.sendMessage(NetworkCore.prefixStandard + "Lifted Time: "
															+ new SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
																	.format(new Date(time + amount)));
											args.getPlayer().sendMessage(
													NetworkCore.prefixStandard + "Reason: " + entry.get("reason"));
										}
									} else {
										args.getPlayer().sendMessage(NetworkCore.prefixError + "No Data Found.");
									}

								} catch (Exception e) {
									args.getPlayer().sendMessage(
											NetworkCore.prefixError + "Player has no data in the database.");
								}
							} else {
								args.getPlayer().sendMessage(
										NetworkCore.prefixError + "User entered is not a player... Try again later.");
							}
						}
					});
				}
			}
		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + "You do not have permission to use this command.");
		}
	}

	@Command(name = "setstate", aliases = { "changestate" }, usage = "/setstate {PLAYER/SPECTATOR/GHOST}")
	public void setState(CommandArgs args) {
		if (PlayerRank.canUseCommand(args.getArchrPlayer().getPlayerRank(), "setstate")) {
			ArchrPlayer player = ArchrPlayer.getArchrPlayerByUUID(args.getPlayer().getUniqueId().toString());
			PlayerState state = PlayerState.valueOf(args.getArgs(0));
			if (state != null) {
				player.setPlayerstate(state);
			}
		}
	}

	@Command(name = "checkdata", usage = "/checkdata")
	public void checkData(CommandArgs args) {
		if (PlayerRank.canUseCommand(args.getArchrPlayer().getPlayerRank(), "checkdata")) {
			args.getPlayer().sendMessage(args.getArchrPlayer().getData().toString());
		}
	}
	
	@Command(name = "goto", usage = "/goto playername") 
	public void gotoPlayer(CommandArgs args) {
		if (PlayerRank.getValueByRank(args.getArchrPlayer().getPlayerRank()) >= PlayerRank.getValueByRank(PlayerRank.Helper)) {
			if (args.getArgs().length == 1) {
				ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(args.getArgs(0));
				if (pp != null) {
					NetworkCommands.sendPlayerToServer(ProxyServer.getInstance().getPlayer(args.getPlayer().getName()), pp.getServer().getInfo().getName());
				} else {
					args.getPlayer().sendMessage(NetworkCore.prefixError + "Player is invalid or not online. Try again later.");
				}
			} else {
				args.getPlayer().sendMessage(NetworkCore.prefixError + args.getCommand().getUsage());
			}
		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + "No.");
		}
	}
}
