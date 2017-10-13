package com.camadeusa.utility.command.prompt.listener;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.camadeusa.NetworkCore;

public class Prompt implements Listener {
	private Plugin plugin;
	private Player sender;
	private List<String> prompts;
	private String message;

	public Prompt(Plugin plugin, Player sender, List<String> prompts, String message) {
		this.plugin = plugin;
		this.sender = sender;
		this.prompts = prompts;
		this.message = message;
		sendPrompt();
	}

	public void sendPrompt() {
		String prompt = ((String) this.prompts.get(0)).replaceAll("<", "");
		prompt = prompt.replaceAll(">", "");
		this.sender.sendMessage(NetworkCore.prefixStandard + ChatColor.translateAlternateColorCodes('&', prompt));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event) {
		if (!event.getPlayer().equals(this.sender))
			return;
		if (this.prompts.size() > 1) {
			this.prompts.set(0, ((String) this.prompts.get(0)).replaceAll("[^\\w\\s<>]", "\\\\$0"));
			this.message = this.message.replaceAll((String) this.prompts.get(0), event.getMessage());
			this.prompts.remove(0);
			sendPrompt();
			event.setCancelled(true);
		} else if (this.prompts.size() == 1) {
			this.prompts.set(0, ((String) this.prompts.get(0)).replaceAll("[^\\w\\s<>]", "\\\\$0"));
			this.message = this.message.replaceAll((String) this.prompts.get(0), event.getMessage());
			this.prompts.remove(0);
			dispatch(this.sender, this.message);
			event.setCancelled(true);
		}
	}

	public void dispatch(final Player sender, final String command) {
		new BukkitRunnable() {
			public void run() {
				sender.chat(command);
			}
		}

				.runTask(this.plugin);
		HandlerList.unregisterAll(this);
	}
}
