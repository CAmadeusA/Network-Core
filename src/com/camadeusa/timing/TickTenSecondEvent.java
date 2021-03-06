package com.camadeusa.timing;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TickTenSecondEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private String message;
	
	public TickTenSecondEvent(String ex) {
		message = ex;
	}
	
	public String getMessage() {
		return message;
	}
	
	
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
