package com.camadeusa.module.mapeditor;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import com.camadeusa.module.Module;
import com.camadeusa.module.game.Gamemode;
import com.camadeusa.module.game.GamemodeManager;
import com.camadeusa.world.WorldManager;

public class MapEditorModule extends Module {

	@Override
	public void activateModule() {
		this.setTag(Gamemode.MAPEDITOR.getValue());
		Bukkit.getLogger().info("Activated");
		
		super.activateModule();
	}
	
}
