package com.camadeusa.utility;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigUtil {
	public FileConfiguration createConfig(String name, String subdirectory, Plugin plugin) throws IOException {
		File dir = new File(plugin.getDataFolder() + File.separator + subdirectory);
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		File file = new File(dir, name + ".yml");
		if (!file.exists()) {
			file.createNewFile();
		}
		return YamlConfiguration.loadConfiguration(file);
	}
	
	public FileConfiguration createConfig(String name, Plugin plugin) throws IOException {
		File dir = plugin.getDataFolder();
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		File file = new File(plugin.getDataFolder(), name + ".yml");
		if (!file.exists()) {
			file.createNewFile();
		}
		return YamlConfiguration.loadConfiguration(file);
	}
	
	public FileConfiguration createConfig(String name, String directory) throws IOException {
		File dir = new File(directory);
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		File file = new File(dir, name + ".yml");
		if (!file.exists()) {
			file.createNewFile();
		}
		return YamlConfiguration.loadConfiguration(file);
	}
	
	public FileConfiguration createConfig(String name, File directory) throws IOException {
		if (!directory.isDirectory()) {
			directory.mkdirs();
		}
		File file = new File(directory, name + ".yml");
		if (!file.exists()) {
			file.createNewFile();
		}
		return YamlConfiguration.loadConfiguration(file);
	}

	public void saveConfig(FileConfiguration config, String name, String subdirectory, Plugin plugin) throws IOException {
		File dir = new File(plugin.getDataFolder() + File.separator + subdirectory);
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		File file = new File(dir, name + ".yml");
		config.save(file);
	}

	public void saveConfig(FileConfiguration config, String name, Plugin plugin) throws IOException {
		File dir = plugin.getDataFolder();
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		File file = new File(plugin.getDataFolder(), name + ".yml");
		config.save(file);
	}

	public void saveConfig(FileConfiguration config, String name, String directory) throws IOException {
		File dir = new File(directory);
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		File file = new File(dir, name + ".yml");
		config.save(file);
	}
	
	public void saveConfig(FileConfiguration config, String name, File directory) throws IOException {
		if (!directory.isDirectory()) {
			directory.mkdirs();
		}
		File file = new File(directory, name + ".yml");
		config.save(file);
	}

	public FileConfiguration getConfig(String name, String subdirectory, Plugin plugin) {
		File dir = new File(plugin.getDataFolder() + File.separator + subdirectory);
		if (!dir.isDirectory()) {
			return null; 
		}
		File file = new File(dir, name + ".yml");
		if (!file.exists()) {
			return null; 
		}
		return YamlConfiguration.loadConfiguration(file); 
	}

	public FileConfiguration getConfig(String name, Plugin plugin) {
		File dir = plugin.getDataFolder();
		if (!dir.isDirectory()) {
			return null; 
		}
		File file = new File(dir, name + ".yml");
		if (!file.exists()) {
			return null;
		}
		return YamlConfiguration.loadConfiguration(file);
	}

	public FileConfiguration getConfig(String name, String directory) {
		File dir = new File(directory);
		if (!dir.isDirectory()) {
			return null; 
		}
		File file = new File(dir, name + ".yml");
		if (!file.exists()) {
			return null; 
		}
		return YamlConfiguration.loadConfiguration(file);
	}
	
	public FileConfiguration getConfig(String name, File directory) {
		if (!directory.isDirectory()) {
			return null; 
		}
		File file = new File(directory, name + ".yml");
		if (!file.exists()) {
			return null; 
		}
		return YamlConfiguration.loadConfiguration(file);
	}
	
	public FileConfiguration reloadConfig(String name, String subdirectory, Plugin plugin) {
		File dir = new File(plugin.getDataFolder() + File.separator + subdirectory);
		if (!dir.isDirectory()) {
			return null; 
		}
		FileConfiguration config = getConfig(name, subdirectory, plugin);
		try {
			config.load(new File(dir, name + ".yml"));
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return config;
	}

	public FileConfiguration reloadConfig(String name, Plugin plugin) {
		File dir = plugin.getDataFolder();
		if (!dir.isDirectory()) {
			return null; 
		}
		FileConfiguration config = getConfig(name, plugin);
		try {
			config.load(new File(dir, name + ".yml"));
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return config;
	}
	
	public FileConfiguration reloadConfig(String name, String directory) {
		File dir = new File(directory);
		if (!dir.isDirectory()) {
			return null; 
		}
		FileConfiguration config = getConfig(name, directory);
		try {
			config.load(new File(dir, name + ".yml"));
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return config;
	}
	
	public FileConfiguration reloadConfig(String name, File directory) {
		if (!directory.isDirectory()) {
			return null; 
		}
		FileConfiguration config = getConfig(name, directory);
		try {
			config.load(new File(directory, name + ".yml"));
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return config;
	}
}
