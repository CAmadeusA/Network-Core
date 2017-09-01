package com.camadeusa.chat;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.camadeusa.NetworkCore;
import com.camadeusa.player.ArchrPlayer;
import com.camadeusa.player.PlayerState;
import com.camadeusa.utility.language.Translator;

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
				reciever.getPlayer().sendMessage(sender.getPlayer().getDisplayName() + "> " + message);
			}
		} else {
			reciever.getPlayer().sendMessage(NetworkCore.prefixError + "" + ChatColor.DARK_RED + "BROADCAST: CONSOLE" + ChatColor.RESET + "> " + message);
		}
	}

}
