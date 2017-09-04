package com.camadeusa.module.game;

public enum Gamemode {
	Hub("HUB"),
	ArenaPVP("ARENAPVP");
	
	private final String value;
	
	private Gamemode(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static Gamemode valueof(String s) {
		switch (s) {
		case "HUB":
			return Hub;
		case "ARENAPVP":
			return ArenaPVP;
			default:
				return Hub;
		}
	}
}
