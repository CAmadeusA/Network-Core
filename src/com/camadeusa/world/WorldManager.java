package com.camadeusa.world;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.Module;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.utility.FileUtil;

public class WorldManager extends Module {
	public static File worldFolder;
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void worldInit(org.bukkit.event.world.WorldInitEvent e) {
	     e.getWorld().setKeepSpawnInMemory(false);
	}
	
	public WorldManager() {
		String[] path = new File("").getAbsolutePath().split(File.separatorChar + "");
		String parentPath = File.separatorChar + "";
		for (int i = 0; i < path.length - 1; i++) {
			parentPath += path[i] + File.separatorChar;
		}
		worldFolder = new File(parentPath + "maps");
		if (!worldFolder.exists()) {
			worldFolder.mkdirs();
		}		
	}

	public static OrionMap loadWorld(String name) {
		try {
			File map = new File(worldFolder.getAbsolutePath() + "/" + name);
			File root = new File("");			
			if (map.exists()) {
				FileUtil.recursiveCopy(map, new File(root.getAbsolutePath() + "/" + name));
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
				Bukkit.getWorld(name).save();
				FileUtil.recursiveDelete(new File(worldFolder.getAbsolutePath() + "/" + name));
				Bukkit.getScheduler().scheduleAsyncDelayedTask(NetworkCore.getInstance(), new Runnable() {
					@Override
					public void run() {
						FileUtil.recursiveCopy(file, new File(worldFolder.getAbsolutePath() + "/" + name));						
					}
				}, 20);
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
		Bukkit.unloadWorld(Bukkit.getWorld(name), false);
		FileUtil.recursiveDelete(new File(new File("").getAbsolutePath() + "/" + name));
	}
	
	@SuppressWarnings("deprecation")
	public static void unloadWorld(String name, boolean removePlayers) {
		if (removePlayers) {
			NetworkPlayer.getOnlinePlayers().forEach(np -> {
				if (np.getPlayer().getWorld().getName().equalsIgnoreCase(name)) {
					np.getPlayer().chat("/hub");
				}
			});			
			
		}
		Bukkit.unloadWorld(Bukkit.getWorld(name), false);
		FileUtil.recursiveDelete(new File(new File("").getAbsolutePath() + "/" + name));
	}
}
