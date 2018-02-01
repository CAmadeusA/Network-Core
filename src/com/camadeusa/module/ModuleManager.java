package com.camadeusa.module;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.event.Listener;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.game.usg.USGOrionGame;
import com.camadeusa.module.game.usg.segments.Deathmatch;
import com.camadeusa.module.game.usg.segments.Endgame;
import com.camadeusa.module.game.usg.segments.Livegame;
import com.camadeusa.module.game.usg.segments.Lobby;
import com.camadeusa.module.game.usg.segments.Predeathmatch;
import com.camadeusa.module.game.usg.segments.Pregame;
import com.camadeusa.module.hub.HubModule;

public class ModuleManager {
	public ArrayList<? super Module> modulesToRegister = new ArrayList<>();

	public ModuleManager() {
	}
	
	public void registerModules() {
		if (!modulesToRegister.isEmpty()) {
			modulesToRegister.forEach(m -> {
				((Module) m).activateModule();
			});			
		}
	}
}
