package com.camadeusa.module.game;

import com.camadeusa.module.Module;

public class OrionSegment extends Module {
	
	int time = 0;
	int timeLog = 0;
	OrionSegment nextSegment;
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
		this.timeLog = time;
	}

	public OrionSegment getNextSegment() {
		return nextSegment;
	}

	public void setNextSegment(OrionSegment nextSegment) {
		this.nextSegment = nextSegment;
	}
	
	public void activate() {
		
	}
	
	public void deactivate() {
		
	}
	
	public void resetTimer() {
		time = timeLog;
	}
	
	public void endSegment() {
		
	}
}
