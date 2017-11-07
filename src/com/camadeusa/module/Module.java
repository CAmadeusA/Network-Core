package com.camadeusa.module;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.camadeusa.NetworkCore;

public class Module implements Listener {
	boolean active;
	String tag;
	
	public void activateModule() {
		this.active = true;
		NetworkCore.getInstance().getServer().getPluginManager().registerEvents(this, NetworkCore.getInstance());
		
	}
	public void deactivateModule() {
		this.active = false;
		HandlerList.unregisterAll(this);
	}
	public boolean isActive() {
		return active;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	
}
