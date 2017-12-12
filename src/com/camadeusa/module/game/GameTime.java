package com.camadeusa.module.game;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import com.camadeusa.module.Module;
import com.camadeusa.timing.TickSecondEvent;
import com.camadeusa.world.OrionMap;

public class GameTime extends Module {

	/*
	 * Gametime:
	 * • Controlls day/night cycle, and freezing minecraft time.
	 * • Not related to game timers.
	 * 
	 */
	
	int dayTime; //in seconds
	int nightTime; // in seconds
	boolean frozen = true;
	double dayIncr = 0;
	double nightIncr = 0;
	OrionMap map;
	
	private static GameTime instance;
	
	long lastTime = 1000L;
	
	long gameStartTime = 1000L;
	
	long DAYSTART = 700;
	long DAYEND = 12560;
	long NIGHTSTART = 12960;
	long NIGHTEND = 22800;
	
	public GameTime() {
		GameTime.instance = this;
	}
	
	public void setDayLength(int seconds) {
		this.dayTime = seconds;
		dayIncr = (DAYEND - DAYSTART) / seconds;
	}
	
	public void setNightLength(int seconds) {
		this.nightTime = seconds;
		nightIncr = (NIGHTEND - NIGHTSTART) / seconds;
	}
	
	public void setOrionMap(OrionMap map) {
		this.map = map;
	}
	
	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
		if (!frozen && map != null) {
			lastTime = 1000L;
			map.getWorld().setTime(lastTime);
		}
	}
	
	@EventHandler
	public void onTickSecond(TickSecondEvent event) {
		if (map != null) {
			map.getWorld().setTime(lastTime);
			if (!frozen) {
				//Bukkit.broadcastMessage("Time");
				if (lastTime < DAYSTART || lastTime > NIGHTEND) {
					lastTime = DAYSTART;
				}
				if (lastTime > DAYEND && lastTime < NIGHTSTART) {
					lastTime = NIGHTSTART;
				}
				
				if (lastTime >= DAYSTART && lastTime <= DAYEND) {
					lastTime += dayIncr;
				} else if (lastTime >= NIGHTSTART && lastTime <= NIGHTEND) {
					lastTime += nightIncr;
				}
				map.getWorld().setTime((long) lastTime);		
			}
		}
	}
	
	public static GameTime getInstance() {
		return instance;
	}
	
}
