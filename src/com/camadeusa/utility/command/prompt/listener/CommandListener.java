package com.camadeusa.utility.command.prompt.listener;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.camadeusa.NetworkCore;
import com.camadeusa.utility.command.prompt.SRegex;

public class CommandListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		SRegex simpleRegex = new SRegex();
		simpleRegex.find(event.getMessage(), "<.*?>");
		List<String> prompts = simpleRegex.getResults();
		if (prompts.size() > 0) {
			event.setCancelled(true);
			Bukkit.getPluginManager().registerEvents(
					new Prompt(NetworkCore.getInstance(), event.getPlayer(), prompts, event.getMessage()),
					NetworkCore.getInstance());
		}
	}
}
