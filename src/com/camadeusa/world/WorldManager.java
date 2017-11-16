package com.camadeusa.world;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.camadeusa.module.game.Gamemode;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.utility.FileUtil;

public class WorldManager {

	public static OrionMap loadWorld(String name) {
		try {
			File root = new File("");
			File worldsFolder = new File(new File("").getAbsolutePath() + "/maps");
			if (!worldsFolder.exists()) {
				worldsFolder.mkdirs();
			}
			File map = new File(worldsFolder.getAbsolutePath() + "/" + name);
			
			if (map.exists()) {
				FileUtil.recursiveCopy(map, new File(root.getAbsolutePath() + "/" + name));
				Bukkit.createWorld(new WorldCreator(name));
				for (File f : map.listFiles()) {
					if (f.isFile() && f.getName().equalsIgnoreCase("OrionMap.yml")) {
						return new OrionMap(new String(Files.readAllBytes(Paths.get(f.getAbsolutePath()))));
						
					}
				}
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void saveWorld(String name) {
		File root = new File(new File("").getAbsolutePath() + "/");
		for (File file : root.listFiles()) {
			if (file.getName().equalsIgnoreCase(name)) {
				FileUtil.recursiveDelete(new File(root.getAbsolutePath() + "/maps/" + name));
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
}
