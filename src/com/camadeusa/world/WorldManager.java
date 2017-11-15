package com.camadeusa.world;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.camadeusa.module.game.Gamemode;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.utility.FileUtil;

public class WorldManager {

	public static void loadWorld(String name) {
		File root = new File("");
		File worldsFolder = new File(new File("").getAbsolutePath() + "/maps");
		if (!worldsFolder.exists()) {
			worldsFolder.mkdirs();
		}
		File map = new File(worldsFolder.getAbsolutePath() + "/" + name);
		
		if (map.exists()) {
			FileUtil.recursiveCopy(map, new File(root.getAbsolutePath() + "/" + name));
			Bukkit.createWorld(new WorldCreator(name));
		}	
	}
	
	public static OrionMap loadWorldByConfig(String name, Gamemode gm) {
		File root = new File("");
		File worldsFolder = new File(new File("").getAbsolutePath() + "/maps");
		if (!worldsFolder.exists()) {
			worldsFolder.mkdirs();
		}
		
		for (File map : worldsFolder.listFiles()) {
			for (File submap : map.listFiles()) {
				if (submap.isFile()) {
					boolean found = false;
					if (submap.getName().equalsIgnoreCase("OrionMap.yml")) {
						try {
							URI uri = new URI(submap.getAbsolutePath());
							JSONTokener tokener = new JSONTokener(uri.toURL().openStream());
							JSONObject orionconfig = new JSONObject(tokener);
							
							OrionMap om = new OrionMap(orionconfig.toString());
							if (om.getMapName().equalsIgnoreCase(name) && om.getGamemode() == gm) {
								FileUtil.recursiveCopy(map, new File(root.getAbsolutePath() + "/" + name));
								Bukkit.createWorld(new WorldCreator(name));
								found = true;
								return om;
							}
						} catch (URISyntaxException | IOException e) {
						}
					}
					if (!found) {
						Bukkit.getLogger().info("Could not find configured map with name " + name + " please try loading this world by the folder name.");
					}
				}
			}
		}
		return null;
	}

	public static void saveWorld(String name) {
		File root = new File(new File("").getAbsolutePath() + "/");
		for (File file : root.listFiles()) {
			if (file.getName().equalsIgnoreCase(name)) {
				FileUtil.recursiveCopy(file, new File(root.getAbsolutePath() + "/maps/" + name));
			}
		}
	}


	@SuppressWarnings("deprecation")
	public static void unloadWorld(String name) {
		NetworkPlayer.getOnlinePlayers().forEach(np -> {
			if (np.getPlayer().getWorld().getName().equalsIgnoreCase(name)) {
				np.getPlayer().chat("/hub");
			}
		});
		FileUtil.recursiveDelete(new File(new File("").getAbsolutePath() + "/" + name));

	}

	@SuppressWarnings("deprecation")
	public static void switchWorld(String name, Boolean removePlayer, String destinationWorld) {
		if (removePlayer) {
			NetworkPlayer.getOnlinePlayers().forEach(np -> {
				if (np.getPlayer().getWorld().getName().equalsIgnoreCase(name)) {
					np.getPlayer().chat("/hub");
				}
			});
		}
		NetworkPlayer.getOnlinePlayers().forEach(np -> {
			if (np.getPlayer().getWorld().getName().equalsIgnoreCase(name)) {
				np.getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());
			}
		});
		if (!name.equalsIgnoreCase("world")) {
			Bukkit.unloadWorld(name, false);
			FileUtil.recursiveDelete(new File(new File("").getAbsolutePath() + "/" + name));			
		}
		loadWorld(destinationWorld);
		
		NetworkPlayer.getOnlinePlayers().forEach(np -> {
			np.getPlayer().teleport(Bukkit.getWorld(destinationWorld).getSpawnLocation());
			
		});
	}
}
