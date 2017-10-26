package com.camadeusa.utility;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.camadeusa.NetworkCore;

public class WorldDeleter {

	//Author: Lazertx && CAmadeusA
	//Usage: ->
	
	/* WorldDeleter.deleteWorld(raidWorld, Bukkit.getWorld("world"), new Callback() {
                            @Override
                            public void onDelete() {
                                //Do Stuff
                            }
                        });
    */
	
    public static void deleteWorld(World world, Location exitLocation, Callback callback) throws IllegalArgumentException {
        if (exitLocation.getWorld() == world) {
            throw new IllegalArgumentException("The exit location should be in a location other than the world being deleted because the players must be teleported out of the world for it to be unloaded.");
        }

        for (Player player : world.getPlayers()) {
            if (player.isDead()) {
                //When attempting to teleport a dead player it will not work, so we force the player to respawn then teleport them.
                player.spigot().respawn();

            }
            player.teleport(exitLocation);
        }

        //Tells Bukkit to unload the world, but this does not guarantee that the world will be unloaded.
        Bukkit.unloadWorld(world, false);

        new BukkitRunnable() {
            @Override
            public void run() {
                boolean loaded = false;
                for (World currentWorld : Bukkit.getWorlds()) {
                    if (currentWorld.getName().equals(world.getName())) {
                        loaded = true;
                    }
                }

                //If the world is not in Bukkit.getWorlds() then we can assume that the world is unloaded and begin the delete the world.
                if (!loaded) {
                    deleteWorld(new File(world.getName()));
                    callback.onDelete();
                    this.cancel();
                }
            }
        }.runTaskTimer(NetworkCore.getInstance(), 20, 0);
    }

    public static void deleteWorld(World world, Callback callback) throws IllegalArgumentException {
    	
    	for (Player player : world.getPlayers()) {
    		if (player.isDead()) {
    			//When attempting to teleport a dead player it will not work, so we force the player to respawn then teleport them.
    			player.spigot().respawn();
    			
    		}
    		player.chat("/hub");
    	}
    	
    	//Tells Bukkit to unload the world, but this does not guarantee that the world will be unloaded.
    	Bukkit.unloadWorld(world, false);
    	
    	new BukkitRunnable() {
    		@Override
    		public void run() {
    			boolean loaded = false;
    			for (World currentWorld : Bukkit.getWorlds()) {
    				if (currentWorld.getName().equals(world.getName())) {
    					loaded = true;
    				}
    			}
    			
    			//If the world is not in Bukkit.getWorlds() then we can assume that the world is unloaded and begin the delete the world.
    			if (!loaded) {
    				deleteWorld(new File(world.getName()));
    				callback.onDelete();
    				this.cancel();
    			}
    		}
    	}.runTaskTimer(NetworkCore.getInstance(), 30, 0);
    }

    private static void deleteWorld(File file) {
        for (File currentFile : file.listFiles()) {
            if (currentFile.isDirectory()) {
                deleteWorld(currentFile);
            }

            currentFile.delete();
        }
        file.delete();
    }
    public static void deleteWorld(World world, World exitWorld, Callback callback) throws IllegalArgumentException {
        deleteWorld(world, exitWorld.getSpawnLocation(), callback);
    }
    
    public interface Callback {

        void onDelete();
    }
}