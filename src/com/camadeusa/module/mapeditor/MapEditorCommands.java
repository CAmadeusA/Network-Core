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
import com.camadeusa.utility.Cuboid;
import com.camadeusa.utility.command.Command;
import com.camadeusa.utility.command.CommandArgs;
import com.camadeusa.world.OrionMap;
import com.camadeusa.world.OrionMap.SoftLocation;
import com.camadeusa.world.WorldManager;
import com.google.common.io.Files;


public class MapEditorCommands {
	HashMap<String, String> loadedMaps = new HashMap<>();
	String activeWorldName = "world";
	Gamemode gamemode;
	
	SoftLocation wp1, wp2, temp;
	
	OrionMap om = new OrionMap();
	
	@Command(name = "loadMap", usage = "/loadMap", description = "Loads the map by the world folder name...Generally use on first time setup.")
	public void loadMap(CommandArgs args) {
		if (GamemodeManager.getInstance().getGamemode() == Gamemode.MAPEDITOR) {
			if (args.getNetworkPlayer().getPlayerRank().getValue() >= PlayerRank.Admin.getValue()) {
				if (args.getArgs().length == 0) {
					args.getPlayer().chat("/loadmap <What is the world name (Case sensitive)> <What is the gamemode? (Case Sensitive)>");
				} else if (args.getArgs().length == 2) {
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
				} else {
					args.getPlayer().chat("/loadmap <What is the world name (Case sensitive)> <What is the gamemode? (Case Sensitive)>");					
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
				} else if (args.getArgs().length == 1) {
					String link = "";
					for (int i = 0; i < args.getArgs().length; i++) {
						if (i+1 == args.getArgs().length) {
							link += args.getArgs(i);
						} else {
							link += args.getArgs(i) + " ";							
						}					}
					om.setMapLink(link);
					args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Link set!");
				} else {
					args.getPlayer().chat("/setMapLink <What is the Map Link?: >");					
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
						if (i+1 == args.getArgs().length) {
							author += args.getArgs(i);
						} else {
							author += args.getArgs(i) + " ";							
						}
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
				} else if (args.getArgs().length == 1) {
					String name = "";
					for (int i = 0; i < args.getArgs().length; i++) {
						if (i+1 == args.getArgs().length) {
							name += args.getArgs(i);
						} else {
							name += args.getArgs(i) + " ";							
						}					}
					om.setMapName(name);
					args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Name set!");
				} else {
					args.getPlayer().chat("/setMapName <Please only use one word: What is the Map Name?: >");					
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
				om.addDMSpawn(new SoftLocation(args.getPlayer().getLocation().getWorld().getName(), args.getPlayer().getLocation().getX(), args.getPlayer().getLocation().getY(), args.getPlayer().getLocation().getZ(), args.getPlayer().getLocation().getYaw()));
				args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Deathmatch Spawn " + (om.getDeathmatchSpawns().size()) + " set!");

			} else {
				args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "You do not have permission to run this command. How did you get here?"));
			}
		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "This server is not configured to edit maps. Please contact a developer if you think this is an error."));
		}
	}
	
	@Command(name = "setWallPos1", usage = "/setWallPos1", description = "Set the deathmatch wall position 1")
	public void setWallPos1(CommandArgs args) {
		if (GamemodeManager.getInstance().getGamemode() == Gamemode.MAPEDITOR) {
			if (args.getNetworkPlayer().getPlayerRank().getValue() >= PlayerRank.Admin.getValue()) {
				wp1 = new SoftLocation(args.getPlayer().getEyeLocation().getWorld().getName(), args.getPlayer().getEyeLocation().getX(), args.getPlayer().getEyeLocation().getY(), args.getPlayer().getEyeLocation().getZ());
				args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Wall position 1 set!");
				
			} else {
				args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "You do not have permission to run this command. How did you get here?"));
			}
		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "This server is not configured to edit maps. Please contact a developer if you think this is an error."));
		}
		
	}

	@Command(name = "setWallPos2", usage = "/setWallPos2", description = "Set the deathmatch wall position 2")
	public void setWallPos2(CommandArgs args) {
		if (GamemodeManager.getInstance().getGamemode() == Gamemode.MAPEDITOR) {
			if (args.getNetworkPlayer().getPlayerRank().getValue() >= PlayerRank.Admin.getValue()) {
				wp2 = new SoftLocation(args.getPlayer().getEyeLocation().getWorld().getName(), args.getPlayer().getEyeLocation().getX(), args.getPlayer().getEyeLocation().getY(), args.getPlayer().getEyeLocation().getZ());
				args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Wall position 2 set!");
				
			} else {
				args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "You do not have permission to run this command. How did you get here?"));
			}
		} else {
			args.getPlayer().sendMessage(NetworkCore.prefixError + ChatManager.translateFor("en", args.getNetworkPlayer(), "This server is not configured to edit maps. Please contact a developer if you think this is an error."));
		}
		
	}

	@Command(name = "ToggleSelectable", usage = "/toggleSelectable", description = "Toggles wether a map is selectable or not.")
	public void toggleSelectable(CommandArgs args) {
		if (GamemodeManager.getInstance().getGamemode() == Gamemode.MAPEDITOR) {
			if (args.getNetworkPlayer().getPlayerRank().getValue() >= PlayerRank.Admin.getValue()) {
				om.toggleSelectable();
				args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Selectable is now: " + om.isSelectable());
				
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
				om.addSpawn(new SoftLocation(args.getPlayer().getLocation().getWorld().getName(), args.getPlayer().getLocation().getX(), args.getPlayer().getLocation().getY(), args.getPlayer().getLocation().getZ(), args.getPlayer().getLocation().getYaw()));
				args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Spawn " + (om.getSpawns().size()) + " set!");

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
				om.setWorldSpawn(new SoftLocation(args.getPlayer().getLocation().getWorld().getName(), args.getPlayer().getLocation().getX(), args.getPlayer().getLocation().getY(), args.getPlayer().getLocation().getZ(), args.getPlayer().getLocation().getYaw()));
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
				om.setDeathmatchSpawn(new SoftLocation(args.getPlayer().getLocation().getWorld().getName(), args.getPlayer().getLocation().getX(), args.getPlayer().getLocation().getY(), args.getPlayer().getLocation().getZ(), args.getPlayer().getLocation().getYaw()));
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
						if (wp1 != null && wp2 != null) {
							Cuboid c = new Cuboid(wp1.toLocation(), wp2.toLocation());
							c.getBlocks().forEach(b -> {
								om.addToWall(new SoftLocation(b.getLocation()));
							});							
						}
						
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
