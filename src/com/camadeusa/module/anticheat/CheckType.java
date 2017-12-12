package com.camadeusa.module.anticheat;

public enum CheckType {
	SPEED(true),
	FORCEFIELD(true);
	
	
	private boolean enabled;
	
	private CheckType(boolean enabled) {
		this.enabled = enabled;
	}
	
	public Boolean isEnabled() {
		return enabled;
	}
	
}
