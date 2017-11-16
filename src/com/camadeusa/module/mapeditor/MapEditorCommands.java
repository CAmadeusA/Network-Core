package com.camadeusa.module.mapeditor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;

import com.camadeusa.NetworkCore;
import com.camadeusa.chat.ChatManager;
import com.camadeusa.module.game.Gamemode;
import com.camadeusa.module.game.GamemodeManager;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.utility.command.Command;
import com.camadeusa.utility.command.CommandArgs;
import com.camadeusa.world.OrionMap;
import com.camadeusa.world.WorldManager;
import com.google.common.io.Files;


public class MapEditorCommands {

	HashMap<String, String> loadedMaps = new HashMap<>();
	File worldsFolder = new File(new File("").getAbsolutePath() + "/maps");
	String activeWorldName = "world";
	Gamemode gamemode;
	
	OrionMap om = new OrionMap();
	
	@Command(name = "loadMap", usage = "/loadMap", description = "Loads the map by the world folder name...Generally use on first time setup.")
	public void loadMap(CommandArgs args) {
		if (GamemodeManager.getInstance().getGamemode() == Gamemode.MAPEDITOR) {
			if (args.getNetworkPlayer().getPlayerRank().getValue() >= PlayerRank.Admin.getValue()) {
				if (args.getArgs().length == 0) {
					args.getPlayer().chat("/loadmap <What is the world name (Case sensitive)> <What is the gamemode? (Case Sensitive)>");
				} else {
					activeWorldName = args.getArgs(0);
					gamemode = Gamemode.valueof(args.getArgs(1).toUpperCase());

					om = WorldManager.loadWorld(activeWorldName);
					if (om == null) {
						om = new OrionMap();						
					}
					om.setGamemode(gamemode);
					Bukkit.getOnlinePlayers().forEach(p -> {
						p.teleport(Bukkit.getWorld(activeWorldName).getSpawnLocation());
					});					
					
					args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Loaded world: " + activeWorldName + " with gamemode: " + gamemode.getValue());
				}
			} else {
				args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "You do not have permission to run this command. How did you get here?"));
			}
		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "This server is not configured to edit maps. Please contact a developer if you think this is an error."));
		}
	}
	
	@Command(name = "setMapLink", usage = "/setMapLink {Map Link}", description = "Sets the map link to be used in game.")
	public void setMapLink(CommandArgs args) {
		if (GamemodeManager.getInstance().getGamemode() == Gamemode.MAPEDITOR) {
			if (args.getNetworkPlayer().getPlayerRank().getValue() >= PlayerRank.Admin.getValue()) {
				if (args.getArgs().length == 0) {
					args.getPlayer().chat("/setMapLink <What is the Map Link?: >");
				} else {
					String link = "";
					for (int i = 0; i < args.getArgs().length; i++) {
						link += args.getArgs(i) + " ";
					}
					om.setMapLink(link);
					args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Link set!");
				}
			} else {
				args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "You do not have permission to run this command. How did you get here?"));
			}
		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "This server is not configured to edit maps. Please contact a developer if you think this is an error."));
		}				
	}

	@Command(name = "setMapAuthor", usage = "/setMapAuthor {Map Author}", description = "Sets the map author to be used in game.")
	public void setMapAuthor(CommandArgs args) {
		if (GamemodeManager.getInstance().getGamemode() == Gamemode.MAPEDITOR) {
			if (args.getNetworkPlayer().getPlayerRank().getValue() >= PlayerRank.Admin.getValue()) {
				if (args.getArgs().length == 0) {
					args.getPlayer().chat("/setMapAuthor <What is the Map Author?:>");
				} else {
					String author = "";
					for (int i = 0; i < args.getArgs().length; i++) {
						author += args.getArgs(i) + " ";
					}
					om.setMapAuthor(author);
					args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Author set!");
				}
			} else {
				args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "You do not have permission to run this command. How did you get here?"));
			}
		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "This server is not configured to edit maps. Please contact a developer if you think this is an error."));
		}				
	}
	
	@Command(name = "setMapName", usage = "/setMapName {Map Name}", description = "Sets the map name to be used in game, and parsed when looking up.")
	public void setMapName(CommandArgs args) {
		if (GamemodeManager.getInstance().getGamemode() == Gamemode.MAPEDITOR) {
			if (args.getNetworkPlayer().getPlayerRank().getValue() >= PlayerRank.Admin.getValue()) {
				if (args.getArgs().length == 0) {
					args.getPlayer().chat("/setMapName <What is the Map Name?: >");
				} else {
					String name = "";
					for (int i = 0; i < args.getArgs().length; i++) {
						name += args.getArgs(i) + " ";
					}
					om.setMapName(name);
					args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Name set!");
				}
			} else {
				args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "You do not have permission to run this command. How did you get here?"));
			}
		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "This server is not configured to edit maps. Please contact a developer if you think this is an error."));
		}				
	}
	
	@Command(name = "setRadius", usage = "/setRadius {Radius}", description = "Sets the world radius that players cannot travel farther than.")
	public void setRadius(CommandArgs args) {
		if (GamemodeManager.getInstance().getGamemode() == Gamemode.MAPEDITOR) {
			if (args.getNetworkPlayer().getPlayerRank().getValue() >= PlayerRank.Admin.getValue()) {
				if (args.getArgs().length != 1) {
					args.getPlayer().chat("/setRadius <What is the radius?: (Number in blocks)>");
				} else {
					if (!StringUtils.isNumeric(args.getArgs(0))) {
						args.getPlayer().chat("/setRadius <Try again... What is the radius?: (Integer in blocks)>");						
					} else {
						om.setRadius(Integer.parseInt(args.getArgs(0)));
						args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Radius set!");
					}
				}
			} else {
				args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "You do not have permission to run this command. How did you get here?"));
			}
		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "This server is not configured to edit maps. Please contact a developer if you think this is an error."));
		}		
	}
	
	@Command(name = "addDeathmatchSpawn", usage = "/addDeathmatchSpawn", description = "Add a Deathmatch spawn location to the list of spawns.")
	public void addDeathmatchSpawn(CommandArgs args) {
		if (GamemodeManager.getInstance().getGamemode() == Gamemode.MAPEDITOR) {
			if (args.getNetworkPlayer().getPlayerRank().getValue() >= PlayerRank.Admin.getValue()) {
				om.addDMSpawn(om.new SoftLocation(args.getPlayer().getLocation().getWorld().getName(), args.getPlayer().getLocation().getX(), args.getPlayer().getLocation().getY(), args.getPlayer().getLocation().getZ(), args.getPlayer().getLocation().getYaw()));
				args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Deathmatch Spawn" + (om.getDeathmatchSpawns().size()) + " set!");

			} else {
				args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "You do not have permission to run this command. How did you get here?"));
			}
		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "This server is not configured to edit maps. Please contact a developer if you think this is an error."));
		}
	}

	@Command(name = "addWorldSpawn", usage = "/addWorldSpawn", description = "Add a spawn location to the list of spawns.")
	public void addWorldSpawn(CommandArgs args) {
		if (GamemodeManager.getInstance().getGamemode() == Gamemode.MAPEDITOR) {
			if (args.getNetworkPlayer().getPlayerRank().getValue() >= PlayerRank.Admin.getValue()) {
				om.addSpawn(om.new SoftLocation(args.getPlayer().getLocation().getWorld().getName(), args.getPlayer().getLocation().getX(), args.getPlayer().getLocation().getY(), args.getPlayer().getLocation().getZ(), args.getPlayer().getLocation().getYaw()));
				args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Spawn" + (om.getSpawns().size()) + " set!");

			} else {
				args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "You do not have permission to run this command. How did you get here?"));
			}
		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "This server is not configured to edit maps. Please contact a developer if you think this is an error."));
		}
	}
	
	@Command(name = "setOWorldSpawn", usage = "/setOWorldSpawn", description = "Sets the world spawn to your current location")
	public void setOWorldSpawn(CommandArgs args) {
		if (GamemodeManager.getInstance().getGamemode() == Gamemode.MAPEDITOR) {
			if (args.getNetworkPlayer().getPlayerRank().getValue() >= PlayerRank.Admin.getValue()) {
				om.setWorldSpawn(om.new SoftLocation(args.getPlayer().getLocation().getWorld().getName(), args.getPlayer().getLocation().getX(), args.getPlayer().getLocation().getY(), args.getPlayer().getLocation().getZ(), args.getPlayer().getLocation().getYaw()));
				args.getPlayer().sendMessage(NetworkCore.prefixStandard + "World spawn set!");

			} else {
				args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "You do not have permission to run this command. How did you get here?"));
			}
		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "This server is not configured to edit maps. Please contact a developer if you think this is an error."));
		}
	}

	@Command(name = "setODeathmatchSpawn", usage = "/setODeathmatchSpawn", description = "Sets the Deathmatch spawn to your current location")
	public void setODeathMatchSpawn(CommandArgs args) {
		if (GamemodeManager.getInstance().getGamemode() == Gamemode.MAPEDITOR) {
			if (args.getNetworkPlayer().getPlayerRank().getValue() >= PlayerRank.Admin.getValue()) {
				om.setDeathmatchSpawn(om.new SoftLocation(args.getPlayer().getLocation().getWorld().getName(), args.getPlayer().getLocation().getX(), args.getPlayer().getLocation().getY(), args.getPlayer().getLocation().getZ(), args.getPlayer().getLocation().getYaw()));
				args.getPlayer().sendMessage(NetworkCore.prefixStandard + "DeathmatchSpawn set!");

			} else {
				args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "You do not have permission to run this command. How did you get here?"));
			}
		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "This server is not configured to edit maps. Please contact a developer if you think this is an error."));
		}
	}
	
	@Command(name = "saveMap", usage = "/saveMap", description = "Saves the current map. All changes are final.")
	public void saveMap(CommandArgs args) {
		if (GamemodeManager.getInstance().getGamemode() == Gamemode.MAPEDITOR) {
			if (args.getNetworkPlayer().getPlayerRank().getValue() >= PlayerRank.Admin.getValue()) {
					if (Bukkit.getWorld(activeWorldName) != null) {
						try {
							File map = new File(new File("").getAbsolutePath() + "/" + activeWorldName + "/OrionMap.yml");
							if (!map.exists()) {
								map.createNewFile();
							}
							Files.write(om.toJSONString().getBytes(), map);
						} catch (IOException e) {
							e.printStackTrace();
						}
						Bukkit.getWorld(activeWorldName).save();
						WorldManager.saveWorld(activeWorldName);
						args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Map Saved!");
					}	
				
			} else {
				args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "You do not have permission to run this command. How did you get here?"));
			}
		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "This server is not configured to edit maps. Please contact a developer if you think this is an error."));
		}
	}
}
