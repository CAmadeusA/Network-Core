package com.camadeusa.world;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;

import com.camadeusa.NetworkCore;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.utility.FileUtil;
import com.camadeusa.utility.WorldDeleter;
import com.camadeusa.utility.WorldDeleter.Callback;

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
