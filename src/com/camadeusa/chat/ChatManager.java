package com.camadeusa.chat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.json.JSONArray;

import com.camadeusa.NetworkCore;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.player.PlayerState;
import com.camadeusa.utility.language.Translator;

import mkremins.fanciful.FancyMessage;
import protocolsupport.api.ProtocolSupportAPI;

public class ChatManager implements Listener {
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if (!event.isCancelled()) {
			String senderLang = event.getPlayer().spigot().getLocale().substring(0, 2);
			
			long l = 0;			
			// Sets the highest muteexpiredate to the only one that matters.
			if (NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString())
					.getData().has("mutes")) {
				for (String key : NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString())
						.getData().getJSONObject("mutes").keySet()) {
					long lk = NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString())
							.getData().getJSONObject("mutes").getJSONObject(key).getLong("muteexpiredate");
					l = l > lk ? l : lk; 
				}				
			}
			
			Date date = new Date(l);
			String myDateStr = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(date);
			if (l > System.currentTimeMillis()) {
				event.getPlayer()
						.sendMessage(NetworkCore.prefixStandard + translateFor("en",
								NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString()),
								"You are muted until:") + " " + myDateStr);
			} else {
				NetworkPlayer.getNetworkPlayerList().forEach(aP -> {
					if (PlayerState.canSee(NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString())
							.getPlayerState(), aP.getPlayerState())) {

						if (!event.getPlayer().spigot().getLocale()
								.equalsIgnoreCase(aP.getPlayer().spigot().getLocale())) {
							sendMessage(NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString()),
									aP, event.getMessage(), true);
						} else {
							sendMessage(NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString()),
									aP, event.getMessage(), false);
						}
					}
				});
			}
			event.setCancelled(true);
		}
	}

	public static String translateFor(String senderLang, NetworkPlayer aP, String message) {
		String language = aP.getPlayer().spigot().getLocale().substring(0, 2);
		try {
			return Translator.translateText(senderLang, language, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String translateFor(String senderLang, String locale, String message) {
		String language = locale.substring(0, 2);
		try {
			return Translator.translateText(senderLang, language, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void sendMessage(NetworkPlayer sender, NetworkPlayer reciever, String message, boolean translate) {
		if (sender != null) {
			if (PlayerState.canSee(sender.getPlayerState(), reciever.getPlayerState())) {
				ArrayList<FancyMessage> info = new ArrayList<>();
				FancyMessage fm = new FancyMessage().text(sender.getPlayer().getDisplayName() + "> ");
				info.add(new FancyMessage().text("Name: ").color(ChatColor.GOLD).then(sender.getPlayer().getDisplayName()));
				info.add(new FancyMessage().text("UUID: ").color(ChatColor.GOLD).then(sender.getPlayer().getUniqueId().toString()));
				info.add(new FancyMessage().text("Rank: ").color(ChatColor.GOLD).then(sender.getPlayerRank().toString()));
				info.add(new FancyMessage().text("First Login: ").color(ChatColor.GOLD).then(new SimpleDateFormat("dd/MM/yy").format(Long.parseLong(sender.getData().get("firstlogin").toString()))));
				info.add(new FancyMessage().text("Version: ").color(ChatColor.GOLD).then(ProtocolSupportAPI.getProtocolVersion(sender.getPlayer()).getName()));
				
				if (reciever.getPlayerRank().getValue() >= PlayerRank.Helper.getValue()) {
					info.add(new FancyMessage().text("You can click this name to lookup our records on this player.").color(ChatColor.GRAY));
					
					if (sender.getData().has("bans")) {
						info.add(new FancyMessage().text("# Bans: ").color(ChatColor.GOLD).then(sender.getData().getJSONObject("bans").length()-1 + ""));						
					} else {
						info.add(new FancyMessage().text("# Bans: ").color(ChatColor.GOLD).then("0"));												
					}
					
					if (sender.getData().has("kicks")) {
						info.add(new FancyMessage().text("# Kicks: ").color(ChatColor.GOLD).then(sender.getData().getJSONObject("kicks").length()-1 + ""));
					} else {						
						info.add(new FancyMessage().text("# Kicks: ").color(ChatColor.GOLD).then("0"));
					}

					if (sender.getData().has("mutes")) {
						info.add(new FancyMessage().text("# Mutes: ").color(ChatColor.GOLD).then(sender.getData().getJSONObject("mutes").length()-1 + ""));
					} else {						
						info.add(new FancyMessage().text("# Mutes: ").color(ChatColor.GOLD).then("0"));
					}
					
					fm.suggest("/lookup " + sender.getPlayer().getName());
					
					if (reciever.getPlayerRank().getValue() >= PlayerRank.SrMod.getValue()) {
						info.add(new FancyMessage().text("IP Address: ").color(ChatColor.GOLD).then(sender.getData().get("ipaddress").toString()));
					}
				}
				
				if (translate) {
					
				ArrayList<FancyMessage> translation = new ArrayList<>();
				translation.add(new FancyMessage().text(NetworkCore.prefixStandard + translateFor("en", reciever, "Attempting Translation on this text. This may not be 100% accurate.")));
				translation.add(new FancyMessage().text(ChatColor.GOLD + translateFor(sender.getPlayer().spigot().getLocale().substring(0,2), reciever, message)));
				fm.formattedTooltip(info).then(message).formattedTooltip(translation).send(reciever.getPlayer());				
				} else {
					fm.formattedTooltip(info).then(message).send(reciever.getPlayer());									
				}
			}
		} else {
			reciever.getPlayer().sendMessage(NetworkCore.prefixError + "" + ChatColor.DARK_RED + "BROADCAST: CONSOLE" + ChatColor.RESET + "> " + message);
		}
	}

}
