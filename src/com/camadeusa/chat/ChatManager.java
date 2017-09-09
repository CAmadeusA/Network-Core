package com.camadeusa.chat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.json.JSONArray;

import com.camadeusa.NetworkCore;
import com.camadeusa.player.ArchrPlayer;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.player.PlayerState;
import com.camadeusa.utility.language.Translator;

import mkremins.fanciful.FancyMessage;
import protocolsupport.api.ProtocolSupportAPI;

public class ChatManager implements Listener {
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		String senderLang = event.getPlayer().spigot().getLocale().substring(0, 2);
		long l = Long.parseLong(ArchrPlayer.getArchrPlayerByUUID(event.getPlayer().getUniqueId().toString()).getData().get("muteexpiredate").toString());
			Date date = new Date(l);
			String myDateStr = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(date);
		if (l > System.currentTimeMillis()) {
			event.getPlayer().sendMessage(NetworkCore.prefixStandard + translateFor("en", ArchrPlayer.getArchrPlayerByUUID(event.getPlayer().getUniqueId().toString()), "You are muted until:") + " " + myDateStr);
		} else {
			ArchrPlayer.getArchrPlayerList().forEach(aP -> {
				if (!event.getPlayer().spigot().getLocale().equalsIgnoreCase(aP.getPlayer().spigot().getLocale())) {
					sendMessage(ArchrPlayer.getArchrPlayerByUUID(event.getPlayer().getUniqueId().toString()), aP, event.getMessage()
							+ ChatColor.DARK_GRAY + " (" + translateFor(senderLang, aP, event.getMessage()) + ") ");
				} else {
					sendMessage(ArchrPlayer.getArchrPlayerByUUID(event.getPlayer().getUniqueId().toString()), aP, event.getMessage());
				}
			});
		}
		event.setCancelled(true);
	}

	public static String translateFor(String senderLang, ArchrPlayer aP, String message) {
		String language = aP.getPlayer().spigot().getLocale().substring(0, 2);
		try {
			return Translator.translateText(senderLang, language, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void sendMessage(ArchrPlayer sender, ArchrPlayer reciever, String message) {
		if (sender != null) {
			if (PlayerState.canSee(sender.getPlayerState(), reciever.getPlayerState())) {
				ArrayList<FancyMessage> info = new ArrayList<>();
				FancyMessage fm = new FancyMessage().text(sender.getPlayer().getDisplayName() + "> ");
				info.add(new FancyMessage().text("Name: ").color(ChatColor.GOLD).then(sender.getPlayer().getDisplayName()));
				info.add(new FancyMessage().text("UUID: ").color(ChatColor.GOLD).then(sender.getPlayer().getUniqueId().toString()));
				info.add(new FancyMessage().text("Rank: ").color(ChatColor.GOLD).then(sender.getPlayerRank().toString()));
				info.add(new FancyMessage().text("First Login: ").color(ChatColor.GOLD).then(new SimpleDateFormat("dd/MM/yy").format(Long.parseLong(sender.getData().get("firstlogin").toString()))));
				info.add(new FancyMessage().text("Version: ").color(ChatColor.GOLD).then(ProtocolSupportAPI.getProtocolVersion(sender.getPlayer()).getName()));
				
				if (PlayerRank.getValueByRank(reciever.getPlayerRank()) >= PlayerRank
						.getValueByRank(PlayerRank.Helper)) {
					info.add(new FancyMessage().text("You can click this name to lookup our records on this player.").color(ChatColor.GRAY));
					info.add(new FancyMessage().text("# Bans: ").color(ChatColor.GOLD).then(new JSONArray(sender.getData().get("bans").toString()).length() + ""));
					info.add(new FancyMessage().text("# Kicks: ").color(ChatColor.GOLD).then(new JSONArray(sender.getData().get("kicks").toString()).length() + ""));
					info.add(new FancyMessage().text("# Mutes: ").color(ChatColor.GOLD).then(new JSONArray(sender.getData().get("mutes").toString()).length() + ""));
					fm.suggest("/lookup " + sender.getPlayer().getName());
					
					if (PlayerRank.getValueByRank(reciever.getPlayerRank()) >= PlayerRank
							.getValueByRank(PlayerRank.SrMod)) {
						info.add(new FancyMessage().text("IP Address: ").color(ChatColor.GOLD).then(sender.getData().get("ipaddress").toString()));
					}
				}
					fm.formattedTooltip(info).then(message).send(reciever.getPlayer());
			}
		} else {
			reciever.getPlayer().sendMessage(NetworkCore.prefixError + "" + ChatColor.DARK_RED + "BROADCAST: CONSOLE" + ChatColor.RESET + "> " + message);
		}
	}

}
