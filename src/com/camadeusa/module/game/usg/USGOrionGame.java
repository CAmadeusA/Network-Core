package com.camadeusa.module.game.usg;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.potion.PotionEffectType;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.game.Gamemode;
import com.camadeusa.module.game.LeaderboardToken;
import com.camadeusa.module.game.OrionGame;
import com.camadeusa.module.game.OrionSegment;
import com.camadeusa.module.game.usg.segments.Deathmatch;
import com.camadeusa.module.game.usg.segments.Endgame;
import com.camadeusa.module.game.usg.segments.Livegame;
import com.camadeusa.module.game.usg.segments.Lobby;
import com.camadeusa.module.game.usg.segments.Predeathmatch;
import com.camadeusa.module.game.usg.segments.Pregame;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerState;
import com.camadeusa.timing.TickSecondEvent;
import com.camadeusa.utility.ItemStackBuilderUtil;
import com.camadeusa.utility.PlayerUtils;
import com.camadeusa.utility.Random;
import com.camadeusa.world.OrionMap;
import com.camadeusa.world.OrionMap.SoftLocation;
import com.camadeusa.world.WorldManager;

public class USGOrionGame extends OrionGame {
	ArrayList<OrionMap> availableMaps = new ArrayList<>();
	ArrayList<SoftLocation> placedBlocks = new ArrayList<>();
	public Map<SoftLocation, Boolean> chests = new LinkedHashMap<>();
	private static LeaderboardToken lb;
		
	int LOBBYTIME = 120;
	int PREGAMETIME = 10;
	int LIVEGAMETIME = 1200;
	int PREDMTIME = 10;
	int DMTIME = 240;
	int ENDGAMETIME = 15;
	
	Lobby lobby;
	Pregame pregame;
	Livegame livegame;
	Predeathmatch predm;
	Deathmatch dm;
	Endgame endgame;

	public OrionMap lobbyMap = new OrionMap();
	
	private static USGOrionGame instance;
	private OrionSegment currentSegment;
	
	public void initializeGame() {
		Bukkit.setSpawnRadius(0);
		loadMaps();
		
		lb = new LeaderboardToken();

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
	
	// Loads only maps for this specific gamemode
	public void loadMaps() {
		availableMaps.clear();

		for (File map : WorldManager.worldFolder.listFiles()) {
			if (map.isDirectory()) {
				OrionMap temp = WorldManager.loadWorld(map.getName());
				if (temp.getGamemode().getValue().equalsIgnoreCase(Gamemode.USG.getValue()) && temp.isSelectable()) {
					if (temp.getMapName().toLowerCase().contains("lobby_usg")
							|| temp.getMapName().toLowerCase().equalsIgnoreCase("lobby_usg")) {
						lobbyMap = temp;
						lobbyMap.createWorld();
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

	}

	public LinkedHashMap<OrionMap, HashMap<UUID, Integer>> gatherMaps() {
		LinkedHashMap<OrionMap, HashMap<UUID, Integer>> votes = new LinkedHashMap<>();
		Collections.shuffle(availableMaps);
		availableMaps.forEach(m -> {
			if (votes.size() < 5) {
				votes.put(m, new HashMap<UUID, Integer>());
			}
		});
		return votes;
	}
	
	public static LeaderboardToken getLeaderboardToken() {
		return lb;
	}

	@EventHandler
	public void onTickSecond(TickSecondEvent event) {	
	//Player visability managment
		NetworkPlayer.getOnlinePlayers().forEach(p1 -> {
			NetworkPlayer.getOnlinePlayers().forEach(p2 -> {
				if (PlayerState.canSee(p1.getPlayerState(), p2.getPlayerState())) {
					p1.getPlayer().showPlayer(p2.getPlayer());
				} else {
					p1.getPlayer().hidePlayer(p2.getPlayer());
				}
			});
		});
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (currentSegment != lobby) {
			NetworkPlayer np = NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString());
			np.updatePlayerstate(PlayerState.SPECTATOR);
			event.getPlayer().teleport(currentSegment.getOrionMap().getWorldSpawn().toLocation());
			event.getPlayer().getInventory().clear();
		}
		event.getPlayer().spigot().setCollidesWithEntities(true);
		event.getPlayer().setExp(0);
		
		//Player visability managment
		NetworkPlayer.getOnlinePlayers().forEach(p1 -> {
			NetworkPlayer.getOnlinePlayers().forEach(p2 -> {
				if (PlayerState.canSee(p1.getPlayerState(), p2.getPlayerState())) {
					p1.getPlayer().showPlayer(p2.getPlayer());
				} else {
					p1.getPlayer().hidePlayer(p2.getPlayer());
				}
			});
		});
		
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString()).getPlayerState() != PlayerState.NORMAL) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			if (NetworkPlayer.getNetworkPlayerByUUID(event.getEntity().getUniqueId().toString()).getPlayerState() != PlayerState.NORMAL) {
				event.setCancelled(true);
				return;
			} else {
				// Fixes Strength values damage percent
				if (((Player) event.getDamager()).getPotionEffect(PotionEffectType.INCREASE_DAMAGE) != null) {
					if (((Player) event.getDamager()).getPotionEffect(PotionEffectType.INCREASE_DAMAGE).getAmplifier() == 1) {
						event.setDamage((event.getDamage() / 1.3D) + ((event.getDamage() / 1.3D) * 0.4)); 
					}
					if (((Player) event.getDamager()).getPotionEffect(PotionEffectType.INCREASE_DAMAGE).getAmplifier() == 2) {
						event.setDamage((event.getDamage() / 2.6D) + ((event.getDamage() / 2.6D) * 0.55)); 
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString()).getPlayerState() != PlayerState.NORMAL) {
			event.setCancelled(true);
			return;
		}
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			if (getPlacableBlocks().contains(event.getBlockPlaced().getType())) {
				if (!event.isCancelled()) {
					placedBlocks.add(new SoftLocation(event.getBlockPlaced().getLocation()));
				}
			} else {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onRegainHealth(EntityRegainHealthEvent event) {
		if (event.getRegainReason().equals(RegainReason.REGEN)  || event.getRegainReason().equals(RegainReason.SATIATED)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString()).getPlayerState() != PlayerState.NORMAL) {
			event.setCancelled(true);
			return;
		}
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			if (!getBreakableBlocks().contains(event.getBlock().getType())) {
				event.setCancelled(true);
			}
		}		
	}
	
	@EventHandler
	public void onClickBlock(PlayerInteractEvent event) {
		if (NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString()).getPlayerState() != PlayerState.NORMAL) {
			event.setCancelled(true);
			return;
		}
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
		if (NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString()).getPlayerState() != PlayerState.NORMAL) {
			event.setCancelled(true);
			return;
		}
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
					if (USGCommands.debugList.contains("tier")) {
						if (chance <= 33) {
							contents = USGChestContents.TIER1.getTierContents();
							ch.setCustomName("Tier: 1");							
						} else if (chance > 33 && chance <= 66) {
							contents = USGChestContents.TIER2.getTierContents();
							ch.setCustomName("Tier: 2");							
						} else if (chance > 66) {
							contents = USGChestContents.TIER3.getTierContents();
							ch.setCustomName("Tier: 3");
						}
					} else {
						if (chance <= USGChestContents.TIER1.getPercent() * 100) {
							contents = USGChestContents.TIER1.getTierContents();
							ch.setCustomName("Tier: 1");
						} else {
							if (chance > USGChestContents.TIER1.getPercent() * 100 && chance < (USGChestContents.TIER2.getPercent() * 100) + (USGChestContents.TIER1.getPercent() * 100)) {
								contents = USGChestContents.TIER2.getTierContents();
								ch.setCustomName("Tier: 2");
								
							} else if (chance >= (USGChestContents.TIER3.getPercent() * 100) + (USGChestContents.TIER2.getPercent() * 100) + (USGChestContents.TIER1.getPercent() * 100)) {
								contents = USGChestContents.TIER3.getTierContents();
								ch.setCustomName("Tier: 3");
								
							}
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
		if (currentSegment != lobby) {
			if (event.getPlayer().getInventory().getContents().length > 0) {
				for (ItemStack item : event.getPlayer().getInventory().getContents()) {
					if (item != null) {
						event.getPlayer().getLocation().getWorld().dropItem(event.getPlayer().getLocation(), item);
					}
				}	
			}
			Entity hitter = PlayerUtils.getLastEntityDamager(event.getPlayer());
			if (hitter instanceof Player) {
				Bukkit.broadcastMessage(NetworkCore.prefixStandard + event.getPlayer().getDisplayName() + " has been slain by " + ((Player) hitter).getDisplayName() + ". There are now " + (NetworkPlayer.getOnlinePlayersByState(PlayerState.NORMAL).size()-1) + " players left. ");				
				lb.registerKill(((Player) hitter).getUniqueId(), event.getPlayer().getUniqueId());						
			}
		}
		event.setQuitMessage("");
		USGScoreboard.getInstance().removeFromLog(event.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent event) {
		if (currentSegment != lobby) {
			for (ItemStack item : event.getPlayer().getInventory().getContents()) {
				event.getPlayer().getLocation().getWorld().dropItem(event.getPlayer().getLocation(), item);
			}	
			Entity hitter = PlayerUtils.getLastEntityDamager(event.getPlayer());
			if (hitter instanceof Player) {
				Bukkit.broadcastMessage(NetworkCore.prefixStandard + event.getPlayer().getDisplayName() + " has been slain by " + ((Player) hitter).getDisplayName() + ". There are now " + (NetworkPlayer.getOnlinePlayersByState(PlayerState.NORMAL).size()-1) + " players left. ");				
				lb.registerKill(((Player) hitter).getUniqueId(), event.getPlayer().getUniqueId());						
			}
		}
		event.setLeaveMessage("");
		USGScoreboard.getInstance().removeFromLog(event.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			NetworkPlayer.getNetworkPlayerByUUID(event.getEntity().getUniqueId().toString()).updatePlayerstate(PlayerState.SPECTATOR);
			event.getDrops().add(new ItemStackBuilderUtil().toSkullBuilder().withOwner(event.getEntity().getName()).buildSkull());
			((Player) event.getEntity()).setExp(0);
			event.getEntity().getLocation().getWorld().strikeLightningEffect(event.getEntity().getLocation());
			event.setDeathMessage("");
			if (event.getEntity().getKiller() instanceof Arrow) {
				Bukkit.broadcastMessage(NetworkCore.prefixStandard + event.getEntity().getDisplayName() + " has been slain by " + ((Player) ((Arrow) event.getEntity().getKiller()).getShooter()).getDisplayName() + ". There are now " + (NetworkPlayer.getOnlinePlayersByState(PlayerState.NORMAL).size()-1) + " players left. ");
				lb.registerKill(((Player) ((Arrow) event.getEntity().getKiller()).getShooter()).getUniqueId(), event.getEntity().getUniqueId());										
			} else if (event.getEntity().getKiller() instanceof Player) {
				Bukkit.broadcastMessage(NetworkCore.prefixStandard + event.getEntity().getDisplayName() + " has been slain by " + event.getEntity().getKiller().getDisplayName() + ". There are now " + (NetworkPlayer.getOnlinePlayersByState(PlayerState.NORMAL).size()-1) + " players left. ");
				lb.registerKill(event.getEntity().getKiller().getUniqueId(), event.getEntity().getUniqueId());						
			}
		}
	}
	
	@EventHandler
	public void onProjectileHitEvent(ProjectileHitEvent event) {
		if (getCurrentSegment() == livegame || getCurrentSegment() == dm) {
			if (event.getEntityType() == EntityType.ARROW) {
				if (event.getHitEntity() != null) {
					if (event.getHitEntity().getType() == EntityType.PLAYER) {
						if (((Arrow) event.getEntity()).getShooter() instanceof Player) {
							lb.registerBow(((Player) ((Arrow) event.getEntity()).getShooter()).getUniqueId(), true);
						}
					}
				} else if (event.getHitBlock() != null) {
					if (((Arrow) event.getEntity()).getShooter() instanceof Player) {
						lb.registerBow(((Player) ((Arrow) event.getEntity()).getShooter()).getUniqueId(), false);
					}				
				}
			}
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(USGOrionGame.getInstance().getCurrentSegment().getOrionMap().getWorldSpawn().toLocation());
		NetworkPlayer.getOnlinePlayers().forEach(p1 -> {
			NetworkPlayer.getOnlinePlayers().forEach(p2 -> {
				if (PlayerState.canSee(p1.getPlayerState(), p2.getPlayerState())) {
					p1.getPlayer().showPlayer(p2.getPlayer());
				} else {
					p1.getPlayer().hidePlayer(p2.getPlayer());
				}
			});
		});
		event.getPlayer().spigot().setCollidesWithEntities(false);
	}
	
	@EventHandler
	public void onHorseSpawn(PlayerInteractEvent event) {
		if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.MONSTER_EGG && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (((SpawnEggMeta) event.getPlayer().getInventory().getItemInMainHand().getItemMeta()).getSpawnedType() == EntityType.HORSE) {
				event.setCancelled(true);
				if (event.getPlayer().getInventory().getItemInMainHand().getAmount() > 1) {
					ItemStack is = event.getPlayer().getInventory().getItemInMainHand();
					is.setAmount(is.getAmount() - 1);
					event.getPlayer().getInventory().setItemInMainHand(is);
				} else {
					event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
				}
				Horse h = (Horse) event.getClickedBlock().getWorld().spawnEntity(event.getClickedBlock().getRelative(BlockFace.UP).getLocation(), EntityType.HORSE);
				h.setAdult();
				h.setOwner(event.getPlayer());
				h.getInventory().setSaddle(new ItemStack(Material.SADDLE));
			}
		}
	}
	
	@EventHandler
	public void onBlockSpread(BlockSpreadEvent event)  {
		event.setCancelled(true);
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


	public static USGOrionGame getInstance() {
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
