package com.camadeusa.module;

public class Module {
	boolean active;
	String tag;
	
	public void activateModule() {
		this.active = true;
		
	}
	public void deactivateModule() {
		this.active = false;
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
