package com.camadeusa.module.game.mcow;

import org.bukkit.event.Listener;

import com.camadeusa.module.Module;
import com.camadeusa.module.game.Gamemode;

public class MCOWModule extends Module implements Listener {
	
	public MCOWModule() {
		this.setTag(Gamemode.Hub.getValue());
	}

}
