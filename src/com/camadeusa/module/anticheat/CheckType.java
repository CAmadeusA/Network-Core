package com.camadeusa.module.anticheat;

public enum CheckType {
	SPEED(false),
	FORCEFIELD(true),
	INVENTORYMOVEMENT(true),
	VAPE(true);
	
	
	private boolean enabled;
	
	private CheckType(boolean enabled) {
		this.enabled = enabled;
	}
	
	public Boolean isEnabled() {
		return enabled;
	}
	
}
