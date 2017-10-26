package com.camadeusa.player;

import java.util.HashMap;

import org.json.JSONObject;

public class PlayerSettings {
	private HashMap<String, String> settings = new HashMap<>();
	
	public PlayerSettings(JSONObject resp) {		
		for (String key : resp.keySet()) {
			settings.put(key, String.valueOf(resp.get(key)));
		}
	}
	
	public String get(String key) {
		return settings.get(key);
	}

	public String getString(String key) {
		return settings.get(key);
	}
	
	public int getInt(String key) {
		return Integer.parseInt(settings.get(key));
	}
	
	public double getDouble(String key) {
		return Double.parseDouble(settings.get(key));
	}
	
	public float getFloat(String key) {
		return Float.parseFloat(settings.get(key));
	}
	
	public char getChar(String key) {
		return settings.get(key).charAt(0);
	}
	
	public boolean has(String key) {
		return settings.containsKey(key);
	}
	
	
}
