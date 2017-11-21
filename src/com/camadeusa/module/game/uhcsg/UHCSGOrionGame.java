package com.camadeusa.module.game.uhcsg;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.game.Gamemode;
import com.camadeusa.module.game.GoldenHead;
import com.camadeusa.module.game.OrionGame;
import com.camadeusa.module.game.OrionSegment;
import com.camadeusa.module.game.uhcsg.segments.Deathmatch;
import com.camadeusa.module.game.uhcsg.segments.Endgame;
import com.camadeusa.module.game.uhcsg.segments.Livegame;
import com.camadeusa.module.game.uhcsg.segments.Lobby;
import com.camadeusa.module.game.uhcsg.segments.Predeathmatch;
import com.camadeusa.module.game.uhcsg.segments.Pregame;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.player.PlayerState;
import com.camadeusa.timing.TickSecondEvent;
import com.camadeusa.utility.Random;
import com.camadeusa.utility.xoreboard.XoreBoard;
import com.camadeusa.utility.xoreboard.XoreBoardPlayerSidebar;
import com.camadeusa.utility.xoreboard.XoreBoardUtil;
import com.camadeusa.world.OrionMap;
import com.camadeusa.world.OrionMap.SoftLocation;
import com.camadeusa.world.WorldManager;

public class UHCSGOrionGame extends OrionGame {
	XoreBoard xb;
	String scoreboardTitle = ChatColor.GRAY + "" + ChatColor.BOLD + "--- " + ChatColor.LIGHT_PURPLE + "Orion" + ChatColor.GRAY + " ---";
	ArrayList<OrionMap> availableMaps = new ArrayList<>();
	ArrayList<NetworkPlayer> players = new ArrayList<>();
	ArrayList<SoftLocation> placedBlocks = new ArrayList<>();
	Map<SoftLocation, Boolean> chests = new LinkedHashMap<>();
		
	int LOBBYTIME = 120;
	int PREGAMETIME = 30;
	int LIVEGAMETIME = 1200;
	int PREDMTIME = 30;
	int DMTIME = 240;
	int ENDGAMETIME = 30;
	
	Lobby lobby;
	Pregame pregame;
	Livegame livegame;
	Predeathmatch predm;
	Deathmatch dm;
	Endgame endgame;

	OrionMap lobbyMap = new OrionMap();
	
	private static UHCSGOrionGame instance;
	private OrionSegment currentSegment;
	
	public void initializeGame() {
		this.activateModule();
		NetworkCore.getInstance().getServer().addRecipe(GoldenHead.getRecipe());
		loadMaps();
		
		xb = XoreBoardUtil.getNextXoreBoard();


		lobby = new Lobby();
		lobby.setTime(LOBBYTIME);
		lobby.setTimeConst(LOBBYTIME);
		lobby.setTag("Lobby");
		
		pregame = new Pregame();
		pregame.setTime(PREGAMETIME);
		pregame.setTimeConst(PREGAMETIME);
		pregame.setTag("Pre-game");
		
		livegame = new Livegame();
		livegame.setTime(LIVEGAMETIME);
		livegame.setTimeConst(LIVEGAMETIME);
		livegame.setTag("Live-game");
		
		predm = new Predeathmatch();
		predm.setTime(PREDMTIME);
		predm.setTimeConst(PREDMTIME);
		predm.setTag("Pre-Deathmatch");
		
		dm = new Deathmatch();
		dm.setTime(DMTIME);
		dm.setTimeConst(DMTIME);
		dm.setTag("Deathmatch");
		
		endgame= new Endgame();
		endgame.setTime(ENDGAMETIME);
		endgame.setTimeConst(ENDGAMETIME);
		endgame.setTag("Endgame");
		
		lobby.setNextSegment(pregame);
		pregame.setNextSegment(livegame);
		livegame.setNextSegment(predm);
		predm.setNextSegment(dm);
		dm.setNextSegment(endgame);
		endgame.setNextSegment(lobby);
		
		this.addSegment(lobby, pregame, livegame, predm, dm, endgame);
		instance = this;
		
		currentSegment = lobby;
		currentSegment.setOrionMap(lobbyMap);
		currentSegment.activate();
	
	}
	
	//Loads only maps for this specific gamemode
	public void loadMaps() {
		File worldsFolder = new File(new File("").getAbsolutePath() + "/maps");
		for (File map : worldsFolder.listFiles()) {
			if (map.isDirectory()) {
				OrionMap temp = WorldManager.loadWorld(map.getName());
				if (temp.getGamemode().getValue().equalsIgnoreCase(Gamemode.UHCSG.getValue()) && temp.isSelectable()) {
					if (temp.getMapName().toLowerCase().contains("lobby")) {
						lobbyMap = temp;
					} else {
						availableMaps.add(temp);						
					}
					Bukkit.getLogger().info("Loaded map: " + temp.getMapName());
				} else {
					WorldManager.unloadWorld(map.getName());
					Bukkit.getLogger().info("Unloaded map: " + temp.getMapName());
				}
			}
		}
		Collections.shuffle(availableMaps);
		getAvailableMaps().forEach(m -> {
			if (Lobby.votes.size() <= 5) {
				Lobby.votes.put(m, new HashMap<UUID, Integer>());
			}
		});
	}
	
	@EventHandler
	public void onTickSecond(TickSecondEvent event) {
		
		for (NetworkPlayer np : NetworkPlayer.getNetworkPlayerList()) {
			XoreBoardPlayerSidebar xbps = xb.getSidebar(np.getPlayer());
			xbps.setDisplayName(scoreboardTitle);
			HashMap<String, Integer> lines = new HashMap<>();
			lines.put(ChatColor.GOLD + "Name: ", 20);
			lines.put(StringUtils.abbreviate(PlayerRank.formatNameByRankWOIcon(NetworkPlayer.getNetworkPlayerByUUID(np.getPlayer().getUniqueId().toString())), 40), 19);
			lines.put(" ", 18);
			lines.put(ChatColor.GOLD + "Rank: ", 17);
			lines.put(StringUtils.abbreviate(ChatColor.BLUE + NetworkPlayer.getNetworkPlayerByUUID(np.getPlayer().getUniqueId().toString()).getPlayerRank().toString(), 40), 16);
			lines.put("  ", 15);
			lines.put(currentSegment.getTag() + ": " + currentSegment.getTime(), 14);
			lines.put(ChatColor.GOLD + "orionmc.net", -1);
			xbps.rewriteLines(lines);
			
			xbps.showSidebar();
		}
		players = NetworkPlayer.getOnlinePlayersByState(PlayerState.NORMAL);
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		xb.addPlayer(event.getPlayer());
		XoreBoardPlayerSidebar xbps = xb.getSidebar(event.getPlayer());
		xbps.setDisplayName(scoreboardTitle);
		HashMap<String, Integer> lines = new HashMap<>();
		lines.put(ChatColor.GOLD + "Name: ", 20);
		lines.put(StringUtils.abbreviate(PlayerRank.formatNameByRankWOIcon(NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString())), 40), 19);
		lines.put(" ", 18);
		lines.put(ChatColor.GOLD + "Rank: ", 17);
		lines.put(StringUtils.abbreviate(ChatColor.BLUE + NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString()).getPlayerRank().toString(), 40), 16);
		lines.put("  ", 15);
		lines.put(currentSegment.getTag() + ": " + currentSegment.getTime(), 14);
		lines.put(ChatColor.GOLD + "orionmc.net", -1);
		xbps.rewriteLines(lines);
		
		xbps.showSidebar();
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			if (getPlacableBlocks().contains(event.getBlockPlaced().getType())) {
				if (!event.isCancelled()) {
					placedBlocks.add(new SoftLocation(event.getBlockPlaced().getLocation()));
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onRegainHealth(EntityRegainHealthEvent event) {
		event.getEntity().sendMessage("Healed: " + event.getRegainReason().name());
		if (event.getRegainReason().equals(RegainReason.REGEN)  || event.getRegainReason().equals(RegainReason.SATIATED)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			if (!getBreakableBlocks().contains(event.getBlock().getType())) {
				event.setCancelled(true);
			}
		}		
	}
	
	@EventHandler
	public void onClickBlock(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			SoftLocation contains = null;
			for (SoftLocation sl : placedBlocks) {
				if (sl.equalsSoft(new SoftLocation(event.getClickedBlock().getLocation()))) {
					contains = sl;
				}
			}
			if (contains != null) {
				contains.toLocation().getBlock().breakNaturally();
				placedBlocks.remove(contains);
			}
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		for (BlockState tile : event.getChunk().getTileEntities()) {
			if (Material.ENDER_CHEST == (tile.getBlock().getType())) {
				tile.getBlock().setType(Material.CHEST);
			}
		}
	}
	
	@EventHandler
	public void onOpenChest(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock().getType() == Material.CHEST) {
				Chest ch = (Chest) event.getClickedBlock().getState();
				boolean has = false;
				for (SoftLocation sl : chests.keySet()) {
					if (sl.equalsSoft(new SoftLocation(ch.getWorld().getName(), ch.getLocation().getX(), ch.getLocation().getY(), ch.getLocation().getZ()))) {
						has = chests.get(sl);
					}
				}
				if (!has) {
					int chance = Random.instance().nextInt(100);
					ItemStack[] contents = null;
					if (chance <= UHCSGChestContents.TIER1.getPercent() * 100) {
						contents = UHCSGChestContents.TIER1.getTierContents();
					} else {
						if (chance > UHCSGChestContents.TIER1.getPercent() * 100 && chance < UHCSGChestContents.TIER3.getPercent() * 100) {
							contents = UHCSGChestContents.TIER2.getTierContents();
							
						} else if (chance >= UHCSGChestContents.TIER3.getPercent() * 100) {
							contents = UHCSGChestContents.TIER3.getTierContents();
							
						}
					}
					
					if (contents != null) {
						Inventory inv = ch.getBlockInventory();
						inv.clear();
						for (int i = 0; i < (Random.instance().nextInt(4) + 4); i++) {
							int slot = Random.instance().nextInt(inv.getSize());
							int item = Random.instance().nextInt(contents.length);
							inv.setItem(slot, contents[item]);
						}
						chests.put(new SoftLocation(ch.getWorld().getName(), ch.getLocation().getX(), ch.getLocation().getY(), ch.getLocation().getZ()), true);
					}					
				}
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		event.setQuitMessage("");
	}
	
	@EventHandler
	public void onLeave(PlayerKickEvent event) {
		event.setLeaveMessage("");
	}
	
	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			NetworkPlayer.getNetworkPlayerByUUID(event.getEntity().getUniqueId().toString()).updatePlayerstate(PlayerState.SPECTATOR);
			event.getEntity().teleport(UHCSGOrionGame.getInstance().getCurrentSegment().getOrionMap().getWorldSpawn().toLocation());
		}
	}
	
	public OrionSegment getCurrentSegment() {
		return currentSegment;
	}

	public <T extends OrionSegment> void setCurrentSegment(T currentSegment) {
		this.currentSegment = currentSegment;
	}

	public ArrayList<OrionMap> getAvailableMaps() {
		return availableMaps;
	}

	public ArrayList<NetworkPlayer> getPlayers() {
		return players;
	}

	public static UHCSGOrionGame getInstance() {
		return instance;
	}
	
	public static ArrayList<Material> getBreakableBlocks() {
		ArrayList<Material> list = new ArrayList<>();
		list.add(Material.LONG_GRASS);
		list.add(Material.LEAVES);
		list.add(Material.LEAVES_2);
		list.add(Material.VINE);
		list.add(Material.YELLOW_FLOWER);
		list.add(Material.RED_ROSE);
		return list; 
	}

	public static ArrayList<Material> getPlacableBlocks() {
		ArrayList<Material> list = new ArrayList<>();
		list.add(Material.COBBLESTONE);
		list.add(Material.WOOD);
		list.add(Material.VINE);
		list.add(Material.YELLOW_FLOWER);
		list.add(Material.RED_ROSE);
		return list; 		
	}
}
