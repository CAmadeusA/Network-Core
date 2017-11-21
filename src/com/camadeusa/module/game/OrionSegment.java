package com.camadeusa.module.game;

import com.camadeusa.module.Module;
import com.camadeusa.world.OrionMap;

public class OrionSegment extends Module {
	
	int time = 0;
	int timeLog = 0;
	OrionSegment nextSegment;
	OrionMap om;
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public void setTimeConst(int time) {
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

	public OrionMap getOrionMap() {
		return om;
	}

	public void setOrionMap(OrionMap om) {
		this.om = om;
	}
	
	public void nextSegment() {
		resetTimer();
		deactivate();
		getNextSegment().activate();	
	}
	
}
