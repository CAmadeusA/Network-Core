package com.camadeusa.module.game;

public enum Gamemode {
	ArenaPVP("ARENAPVP"),
	Hub("HUB");
	
	private final String value;
	
	private Gamemode(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
