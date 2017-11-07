package com.camadeusa.module;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.event.Listener;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.game.uhcsg.UHCSGOrionGame;
import com.camadeusa.module.game.uhcsg.segments.Deathmatch;
import com.camadeusa.module.game.uhcsg.segments.Endgame;
import com.camadeusa.module.game.uhcsg.segments.Livegame;
import com.camadeusa.module.game.uhcsg.segments.Lobby;
import com.camadeusa.module.game.uhcsg.segments.Predeathmatch;
import com.camadeusa.module.game.uhcsg.segments.Pregame;
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
