package com.camadeusa.module.game.uhcsg.segments;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.camadeusa.NetworkCore;
import com.camadeusa.chat.ChatManager;
import com.camadeusa.module.game.OrionSegment;
import com.camadeusa.module.game.VotingMode;
import com.camadeusa.module.game.uhcsg.UHCSGCommands;
import com.camadeusa.module.game.uhcsg.UHCSGOrionGame;
import com.camadeusa.network.ServerMode;
import com.camadeusa.network.ServerMode.ServerJoinMode;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerState;
import com.camadeusa.timing.TickSecondEvent;
import com.camadeusa.utility.MathUtil;
import com.camadeusa.utility.menu.Inventory;
import com.camadeusa.utility.menu.InventoryRunnable;
import com.camadeusa.utility.menu.InventoryS;
import com.camadeusa.utility.menu.SlotItem;
import com.camadeusa.utility.menu.hotbar.HotbarItem;
import com.camadeusa.utility.menu.hotbar.HotbarRunnable;
import com.camadeusa.world.OrionMap;

import io.github.theluca98.textapi.ActionBar;
import io.github.theluca98.textapi.Title;
import mkremins.fanciful.FancyMessage;

public class Lobby extends OrionSegment {

	HotbarItem votingItem = new HotbarItem("Vote for a Map!", "Vote for a Map!", 0, Material.REDSTONE_COMPARATOR);
	Inventory votingMenu = new Inventory("Voting-Menu", 1);
	public static Lobby instance;
	public LinkedHashMap<OrionMap, HashMap<UUID, Integer>> votes = new LinkedHashMap<>();
	public Map<OrionMap, Integer> top = new LinkedHashMap<>();
	
	VotingMode mode = VotingMode.DIRECT;
	
	@Override
	public void activate() {
		UHCSGOrionGame.getInstance().setCurrentSegment(this);
		this.activateModule();
		instance = this;
		
		votes = UHCSGOrionGame.getInstance().gatherMaps();
		
		getOrionMap().getWorldSpawn().toLocation().getWorld().setDifficulty(Difficulty.PEACEFUL);
		
		ServerMode.setMode(ServerJoinMode.PUBLIC);
		
		InventoryS.registerHotbarItem(NetworkCore.getInstance(), votingItem);
		InventoryS.registerInventory(NetworkCore.getInstance(), votingMenu);
		
		votingItem.setOnClick(new HotbarRunnable(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK) {
			@Override
			public void onHotbarItemUsed(PlayerInteractEvent event) {
				InventoryS.openInventory(event.getPlayer(), votingMenu.getTitle());
			}
		});	
		
		for (int i = 0; i < votingMenu.getSize(); i++) {
			if (votingMenu.getSlotItemAt(i) != null) {
				votingMenu.removeSlotItem(i);
			}
		}
		
		for (int i = 0; i < 5; i++) {
			SlotItem item = new SlotItem(((OrionMap) votes.keySet().toArray()[i]).getMapName(), "Vote for: " + ((OrionMap) votes.keySet().toArray()[i]).getMapName(), 0, Material.MAP);
			votingMenu.addSlotItem(i + 2, item);
			
			item.setOnClick(new InventoryRunnable() {
				@Override
				public void runOnClick(InventoryClickEvent event) {
					event.getWhoClicked().closeInventory();
					voteMap(NetworkPlayer.getNetworkPlayerByUUID(event.getWhoClicked().getUniqueId().toString()), event.getCurrentItem().getItemMeta().getDisplayName());
				}
			});
		}
		
		ServerMode.setMode(ServerJoinMode.PUBLIC);
		
	}
	
	@Override
	public void deactivate() {
		this.deactivateModule();
	}
	
	public void voteMap(NetworkPlayer np, String mapName) {
		votes.keySet().forEach(mm -> {
			UUID found = null;
			boolean foundUUID = false;
			for (UUID uuid : votes.get(mm).keySet()) {
				if (uuid.compareTo(np.getPlayer().getUniqueId()) == 0) {
					foundUUID = true;
					found = uuid;
				}
			}
			if (foundUUID) {
				votes.get(mm).remove(found);
			}
			if (mm.getMapName().equalsIgnoreCase(mapName)) {
				votes.get(mm).put(np.getPlayer().getUniqueId(), mode.getVotesByRank(np.getPlayerRank()));
			}
		});
		
		sortTopMaps();
	}
	
	public void sortTopMaps() {
		top = new LinkedHashMap<>();
		votes.keySet().forEach(mm -> {
			int total = 0;
			for (Integer i : votes.get(mm).values()) {
				total += i;
			}
			top.put(mm, total);
		});			
		top = sortByValue(top);
		
		for (int i = 0; i < votingMenu.getSize(); i++) {
			if (votingMenu.getSlotItemAt(i) != null) {
				votingMenu.removeSlotItem(i);
			}
		}
		
		int totalVotes = 0;
		for (int i = 0; i < top.size(); i++) {
			totalVotes += (int) top.values().toArray()[i];
		}
		for (int i = 0; i < (top.size() >= 5 ? 5 : top.size()); i++) {
			SlotItem item = new SlotItem(((OrionMap) top.keySet().toArray()[i]).getMapName(), "(" + (int) top.values().toArray()[i] + "/" + totalVotes + ") " + "Vote for: " + ((OrionMap) top.keySet().toArray()[i]).getMapName(), 0, Material.MAP, ((int) top.values().toArray()[i]) > 64 ? 64:(int) top.values().toArray()[i]);
			votingMenu.addSlotItem(((top.size() >= 5 ? 5 : top.size()) - i - 1) + 2, item);
			
			item.setOnClick(new InventoryRunnable() {
				@Override
				public void runOnClick(InventoryClickEvent event) {
					event.getWhoClicked().closeInventory();
					voteMap(NetworkPlayer.getNetworkPlayerByUUID(event.getWhoClicked().getUniqueId().toString()), event.getCurrentItem().getItemMeta().getDisplayName());
				}
			});
		}
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.getPlayer().setHealth(20);
		event.getPlayer().setFoodLevel(20);
		event.getPlayer().getInventory().clear();
		event.getPlayer().setGameMode(GameMode.SURVIVAL);
		event.getPlayer().setExp(0);
		event.getPlayer().getActivePotionEffects().forEach(pe -> {
			event.getPlayer().removePotionEffect(pe.getType());
		});
		
		NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString()).updatePlayerstate(PlayerState.NORMAL);
		event.getPlayer().teleport(getOrionMap().getWorldSpawn().toLocation());
		
		votingItem.give(event.getPlayer(), 0);
		
	}
	
	@EventHandler
	public void onTickSecond(TickSecondEvent event) {
		if (NetworkPlayer.getOnlinePlayersByState(PlayerState.NORMAL).size() >= 18 && getTime() > 30) {
			setTime(30);
		}
		
		if (getTime() % 30 == 0 && top.size() > 0) {
			NetworkPlayer.getNetworkPlayerList().forEach(np -> {
				int totalVotes = 0;
				for (int j = 0; j < top.size(); j++) {
					totalVotes += (int) top.values().toArray()[j];
				}
				np.getPlayer().sendMessage(NetworkCore.prefixStandard + "The following maps are available: ");
				for (int i = (top.size() >= 5 ? 5 : top.size()) -1; i >= 0; i--) {
					FancyMessage fm = new FancyMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RESET + "" + top.values().toArray()[i] + "/" + totalVotes + ChatColor.DARK_GRAY + ']' + ChatColor.RESET + ": " + ChatColor.LIGHT_PURPLE + ((OrionMap) top.keySet().toArray()[i]).getMapName());
					fm.tooltip(ChatColor.GOLD + "Click To Vote!", "", ChatColor.GOLD + "Author: " + ChatColor.RESET + ((OrionMap) top.keySet().toArray()[i]).getMapAuthor(), ChatColor.GOLD + "Link: " + ChatColor.RESET + ((OrionMap) top.keySet().toArray()[i]).getMapLink(), ChatColor.GOLD + "Size: " + ChatColor.RESET + ((OrionMap) top.keySet().toArray()[i]).getRadius());
					fm.command("/vote " + ((OrionMap) top.keySet().toArray()[i]).getMapName());
					fm.send(np.getPlayer());
				}
			});
			
		}
		
		if (getTime() > 1) {
			setTime(getTime() - 1);
			if (getTime() > 30) {
				new ActionBar(ChatColor.LIGHT_PURPLE + "Time Remaining: " + ChatColor.RESET + "" + String.format("%02d:%02d", getTime() / 60, getTime() % 60)).sendToAll();				
			} else {
				if (getTime() > 3) {
					new Title("", ChatColor.LIGHT_PURPLE + "Time Remaining: " + ChatColor.RESET + "" + getTime(), 5, 10, 5).sendToAll();
				} else {
					new Title(ChatColor.DARK_RED + "" + getTime(), "", 5, 10, 5).sendToAll();
					NetworkPlayer.getOnlinePlayers().forEach(np -> {
						np.getPlayer().playSound(np.getPlayer().getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 1f);
					});
				}
			}
		} else {
			if (NetworkPlayer.getOnlinePlayersByState(PlayerState.NORMAL).size() < 18 && !UHCSGCommands.debugList.contains("playercount")) {
				NetworkPlayer.getNetworkPlayerList().forEach(np -> {
					np.getPlayer().sendMessage(NetworkCore.prefixStandard + ChatManager.translateFor("en", np, "Not enough players to begin game. 18 required, current: " + NetworkPlayer.getOnlinePlayersByState(PlayerState.NORMAL).size() + ". "));
					
				});
				NetworkPlayer.getOnlinePlayers().forEach(np -> {
					np.getPlayer().playSound(np.getPlayer().getLocation(), Sound.ENTITY_DONKEY_HURT, 1f, 1f);
				});
				resetTimer();				
			} else {
				NetworkPlayer.getOnlinePlayers().forEach(np -> {
					np.getPlayer().playSound(np.getPlayer().getLocation(), Sound.BLOCK_NOTE_SNARE, 1f, 1f);
				});
				nextSegment();
			}
		}
		
	}
	
	public static Map<OrionMap, Integer> sortByValue(Map<OrionMap, Integer> unsorted_map){

	    Map<OrionMap, Integer> sorted_map = new LinkedHashMap<OrionMap, Integer>();

	    try{
	        // 1. Convert Map to List of Map
	        List<Map.Entry<OrionMap, Integer>> list = new LinkedList<Map.Entry<OrionMap, Integer>>(unsorted_map.entrySet());

	        // 2. Sort list with Collections.sort(), provide a custom Comparator
	        Collections.sort(list, new Comparator<Map.Entry<OrionMap, Integer>>() {
	            public int compare(Map.Entry<OrionMap, Integer> o1,
	                    Map.Entry<OrionMap, Integer> o2) {
	                return (o1.getValue()).compareTo(o2.getValue());
	            }
	        });

	        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
	        for (Map.Entry<OrionMap, Integer> entry : list) {
	            sorted_map.put(entry.getKey(), entry.getValue());
	        }

	    }
	    catch(Exception e){
	        e.printStackTrace();
	    } 

	    return sorted_map;

	}
	
	public LinkedHashMap<OrionMap, HashMap<UUID, Integer>> getVotes() {
		return votes;
	}

	public Map<OrionMap, Integer> getTop() {
		return top;
	}

	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPickupItem(EntityPickupItemEvent event) {
		event.setCancelled(true);		
	}
	
	@EventHandler
	public void onPlayerMoveOutOfBounds(PlayerMoveEvent event) {
		if ((MathUtil.distance(getOrionMap().getWorldSpawn().getX(), event.getTo().getX(), getOrionMap().getWorldSpawn().getZ(), event.getTo().getZ())) > getOrionMap().getRadius()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void tickSecondSetTime(TickSecondEvent event) {
		getOrionMap().getWorldSpawn().toLocation().getWorld().setTime(6000);
	}
	
	@EventHandler
	public void onWeatherChangge(WeatherChangeEvent event) {
		if (event.toWeatherState()) {
			event.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
    public void FrameRotate(PlayerInteractEntityEvent e) {
        if (e.getRightClicked().getType().equals(EntityType.ITEM_FRAME)) {
            if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                e.setCancelled(true);
            }
        }
    }
}
