package com.camadeusa.module.game.uhcsg.segments;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
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
import com.camadeusa.module.game.uhcsg.UHCSGOrionGame;
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

public class Lobby extends OrionSegment {

	HotbarItem votingItem = new HotbarItem("Vote for a Map!", "Vote for a Map!", 0, Material.ANVIL);
	Inventory votingMenu = new Inventory("Voting-Menu", 1);
	public static LinkedHashMap<OrionMap, HashMap<UUID, Integer>> votes = new LinkedHashMap<>();
	public static Map<OrionMap, Integer> top = new LinkedHashMap<>();
	
	VotingMode mode = VotingMode.DIRECT;
	
	@Override
	public void activate() {
		UHCSGOrionGame.getInstance().setCurrentSegment(this);
		this.activateModule();
		
		getOrionMap().getWorldSpawn().toLocation().getWorld().setDifficulty(Difficulty.PEACEFUL);
		
		InventoryS.registerHotbarItem(NetworkCore.getInstance(), votingItem);
		InventoryS.registerInventory(NetworkCore.getInstance(), votingMenu);
		
		votingItem.setOnClick(new HotbarRunnable(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK) {
			@Override
			public void onHotbarItemUsed(PlayerInteractEvent event) {
				InventoryS.openInventory(event.getPlayer(), votingMenu.getTitle());
			}
		});	
		
		for (int i = 0; i < votes.size(); i++) {
			SlotItem item = new SlotItem(((OrionMap) votes.keySet().toArray()[i]).getMapName(), "Vote for: " + ((OrionMap) votes.keySet().toArray()[i]).getMapName(), 0, Material.MAP);
			votingMenu.addSlotItem(i + 2, item);
			
			item.setOnClick(new InventoryRunnable() {
				@Override
				public void runOnClick(InventoryClickEvent event) {
					event.getWhoClicked().closeInventory();
					voteMap(NetworkPlayer.getNetworkPlayerByUUID(event.getWhoClicked().getUniqueId().toString()), votingMenu.getSlotItemAt(event.getSlot()).getTitle());
				}
			});
		}
		
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
		top = top.entrySet().stream().sorted(Collections.reverseOrder(Entry.comparingByValue())).collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	
	
		for (int i = 0; i < votingMenu.getSize(); i++) {
			if (votingMenu.getSlotItemAt(i) != null) {
				votingMenu.removeSlotItem(i);
			}
		}
		
		int totalVotes = 0;
		for (int i = 0; i < (top.size() >= 5 ? 5 : top.size()); i++) {
			totalVotes += (int) top.values().toArray()[i];
		}
		for (int i = 0; i < (top.size() >= 5 ? 5 : top.size()); i++) {
			SlotItem item = new SlotItem(((OrionMap) top.keySet().toArray()[i]).getMapName(), "(" + (int) top.values().toArray()[i] + "/" + totalVotes + ") " + "Vote for: " + ((OrionMap) top.keySet().toArray()[i]).getMapName(), 0, Material.MAP, ((int) top.values().toArray()[i]) > 64 ? 64:(int) top.values().toArray()[i]);
			votingMenu.addSlotItem(i + 2, item);
			
			item.setOnClick(new InventoryRunnable() {
				@Override
				public void runOnClick(InventoryClickEvent event) {
					event.getWhoClicked().closeInventory();
					voteMap(NetworkPlayer.getNetworkPlayerByUUID(event.getWhoClicked().getUniqueId().toString()), votingMenu.getSlotItemAt(event.getSlot()).getTitle());
				}
			});
		}
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.getPlayer().teleport(getOrionMap().getWorldSpawn().toLocation());
		event.getPlayer().setHealth(20);
		event.getPlayer().setFoodLevel(20);
		event.getPlayer().getInventory().clear();
		event.getPlayer().getActivePotionEffects().forEach(pe -> {
			event.getPlayer().removePotionEffect(pe.getType());
		});
		
		NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString()).updatePlayerstate(PlayerState.NORMAL);
		
		votingItem.give(event.getPlayer(), 0);
		
	}
	
	@EventHandler
	public void onTickSecond(TickSecondEvent event) {
		if (UHCSGOrionGame.getInstance().getPlayers().size() >= 18 && getTime() > 30) {
			setTime(30);
		}
		
		if (getTime() % 10 == 0) {
			NetworkPlayer.getNetworkPlayerList().forEach(np -> {
				// Incomplete, format this as necessary && set up voting system.
				np.getPlayer().sendMessage(NetworkCore.prefixStandard + "The following maps are available: ");
				for (int i = 0; i < (top.size() >= 5 ? 5 : top.size()); i++) {
					np.getPlayer().sendMessage(((OrionMap) top.keySet().toArray()[i]).getMapName() + " with " + top.values().toArray()[i]);
				}
			});
			
		}
		
		if (getTime() > 0) {
			setTime(getTime() - 1);
		} else {
			if (UHCSGOrionGame.getInstance().getPlayers().size() < 18) {
				NetworkPlayer.getNetworkPlayerList().forEach(np -> {
					np.getPlayer().sendMessage(NetworkCore.prefixStandard + ChatManager.translateFor("en", np, "Not enough players to begin game. 18 required, current: " + UHCSGOrionGame.getInstance().getPlayers().size() + ". "));
					
				});
				resetTimer();				
			} else {
				nextSegment();
			}
		}
		
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
