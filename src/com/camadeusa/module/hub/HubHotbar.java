package com.camadeusa.module.hub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.JSONArray;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.Module;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.utility.ItemStackBuilderUtil;
import com.camadeusa.utility.menu.Inventory;
import com.camadeusa.utility.menu.InventoryRunnable;
import com.camadeusa.utility.menu.InventoryS;
import com.camadeusa.utility.menu.SlotItem;
import com.camadeusa.utility.menu.hotbar.HotbarItem;
import com.camadeusa.utility.menu.hotbar.HotbarRunnable;
import com.rethinkdb.RethinkDB;

import mkremins.fanciful.FancyMessage;

public class HubHotbar extends Module {
	HubHotbar instance;
	
	HotbarItem nav;
	HotbarItem cosmetics;
	HotbarItem stats;
	HotbarItem shop;
	HotbarItem info;
	
	Inventory navInv;
	//Inventory cosmeticsInv;
	Inventory statsInv;
	Inventory infoInv;
	
	
	public HubHotbar() {
		instance = this;
		
		nav = new HotbarItem(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Navigation", ChatColor.GOLD + "Server Transportation", 0, Material.COMPASS);
		cosmetics = new HotbarItem(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Cosmetics", ChatColor.GOLD + "Currently Unavailable...", 0, Material.NETHER_STAR);
		stats = new HotbarItem(ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Statistics", ChatColor.GOLD + "Leaderboards and your stats!", 0, Material.PAPER);
		shop = new HotbarItem(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Donate", ChatColor.GOLD + "The available shop!", 0, Material.DIAMOND);
		info = new HotbarItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Info", ChatColor.LIGHT_PURPLE + "Links and available info!", 0, Material.BOOK);
	
		navInv = new Inventory("Navigation", 1);
		//cosmeticsInv = new Inventory("Cosmetics", 6);
		statsInv = new Inventory("Stats: Select Gametype", 1);
		infoInv = new Inventory("Info", 1);
		
		registerHotbarOnClicks();
		
		setupNavigationItem();
		setupStatsItem();
		setupInfoItem();
		
		InventoryS.registerInventory(NetworkCore.getInstance(), navInv);
		InventoryS.registerInventory(NetworkCore.getInstance(), statsInv);
		InventoryS.registerInventory(NetworkCore.getInstance(), infoInv);
		
		InventoryS.registerHotbarItem(NetworkCore.getInstance(), nav);
		InventoryS.registerHotbarItem(NetworkCore.getInstance(), cosmetics);
		InventoryS.registerHotbarItem(NetworkCore.getInstance(), stats);
		InventoryS.registerHotbarItem(NetworkCore.getInstance(), shop);
		InventoryS.registerHotbarItem(NetworkCore.getInstance(), info);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent event) {
		nav.give(event.getPlayer(), 0);
		cosmetics.give(event.getPlayer(), 1);
		stats.give(event.getPlayer(), 4);
		shop.give(event.getPlayer(), 7);
		info.give(event.getPlayer(), 8);
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}
	
	private void registerHotbarOnClicks() {
		// Hooks up the hotbar items
		nav.setOnClick(new HotbarRunnable(Action.RIGHT_CLICK_AIR , Action.RIGHT_CLICK_BLOCK) {
			@Override
			public void onHotbarItemUsed(PlayerInteractEvent event) {
				InventoryS.openInventory(event.getPlayer(), "Navigation");
			}
		});

		cosmetics.setOnClick(new HotbarRunnable(Action.RIGHT_CLICK_AIR , Action.RIGHT_CLICK_BLOCK) {
			@Override
			public void onHotbarItemUsed(PlayerInteractEvent event) {
				event.getPlayer().sendMessage(NetworkCore.prefixStandard + ChatColor.GOLD + "Coming Soon!");
			}
		});

		stats.setOnClick(new HotbarRunnable(Action.RIGHT_CLICK_AIR , Action.RIGHT_CLICK_BLOCK) {
			@Override
			public void onHotbarItemUsed(PlayerInteractEvent event) {
				InventoryS.openInventory(event.getPlayer(), "Stats: Select Gametype");
			}
		});
		
		shop.setOnClick(new HotbarRunnable(Action.RIGHT_CLICK_AIR , Action.RIGHT_CLICK_BLOCK) {
			@Override
			public void onHotbarItemUsed(PlayerInteractEvent event) {
				event.getPlayer().chat("/buy");
			}
		});
		
		info.setOnClick(new HotbarRunnable(Action.RIGHT_CLICK_AIR , Action.RIGHT_CLICK_BLOCK) {
			@Override
			public void onHotbarItemUsed(PlayerInteractEvent event) {
				InventoryS.openInventory(event.getPlayer(), "Info");
			}
		});
	}
	
	private void setupNavigationItem() {
		SlotItem joinUsg = new SlotItem(ChatColor.GOLD + "" + ChatColor.BOLD + "" + "Join USG", ChatColor.LIGHT_PURPLE + "Click Here!", 0, Material.DIAMOND_SWORD);
		navInv.addSlotItem(4, joinUsg);
		
		joinUsg.setOnClick(new InventoryRunnable() {
			@Override
			public void runOnClick(InventoryClickEvent e) {
				((Player) e.getWhoClicked()).chat("/join usg");
				((Player) e.getWhoClicked()).closeInventory();				
			}
		});
	}
	
	private void setupStatsItem() {
		//Gamemode slot item in top level
		SlotItem usg = new SlotItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Ultra Survival Games!", "", 0, Material.DIAMOND_SWORD);
		
		//Gamemode selected, menu available
		Inventory usgSelMenu = new Inventory("Leaderboards & Stats", 1);
		SlotItem lbs = new SlotItem(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Leaderboards", ChatColor.GOLD + "Sort by Category.", 0, Material.LADDER);
		SlotItem stat = new SlotItem(ChatColor.YELLOW + "" + ChatColor.BOLD + "Your Stats", ChatColor.GOLD + "" + ChatColor.BOLD + "View your statistics!", 0, Material.DIAMOND_SWORD);
		SlotItem reset = new SlotItem(ChatColor.BLUE + "" + ChatColor.BOLD + "Reset Stats", ChatColor.GOLD + "" + ChatColor.BOLD + "COMING SOON!", 0, Material.TNT);
		usgSelMenu.addSlotItem(2, lbs);
		usgSelMenu.addSlotItem(4, stat);
		usgSelMenu.addSlotItem(6, reset);
		InventoryS.registerInventory(NetworkCore.getInstance(), usgSelMenu);
		
		usg.setOnClick(new InventoryRunnable() {
			@Override
			public void runOnClick(InventoryClickEvent e) {
				InventoryS.openInventory((Player) e.getWhoClicked(), "Leaderboards & Stats");
			}
		});
		
		////Leaderboards - Top 50
		//////Options
		Inventory opt = new Inventory("USG: Select Category", 1);
		SlotItem elo = new SlotItem(ChatColor.RED + "" + ChatColor.BOLD + "ELO", "", 0, Material.MAGMA_CREAM);
		SlotItem wins = new SlotItem(ChatColor.AQUA + "" + ChatColor.BOLD + "Wins", "", 0, Material.DIAMOND);
		SlotItem kills = new SlotItem(ChatColor.GRAY + "" + ChatColor.BOLD + "Kills", "", 0, Material.IRON_AXE);
		SlotItem games = new SlotItem(ChatColor.GREEN + "" + ChatColor.BOLD + "Games", "", 0, Material.SLIME_BALL);
		opt.addSlotItem(1, elo);
		opt.addSlotItem(3, wins);
		opt.addSlotItem(5, kills);
		opt.addSlotItem(7, games);
		InventoryS.registerInventory(NetworkCore.getInstance(), opt);
		lbs.setOnClick(new InventoryRunnable() {
			@Override
			public void runOnClick(InventoryClickEvent e) {
				InventoryS.openInventory((Player) e.getWhoClicked(), "USG: Select Category");
			}
		});
		
		////////Opens top 50 of type
		//////////Elo
		Inventory top50EloInv = new Inventory("USG Top 50: Elo", 6);
		elo.setOnClick(new InventoryRunnable() {
			@Override
			public void runOnClick(InventoryClickEvent e) {
				String str = RethinkDB.r.table("usgStatsBeta").union(RethinkDB.r.table("usgStats")).eqJoin("id", RethinkDB.r.table("playerdata")).zip().orderBy(RethinkDB.r.desc("elo")).limit(50).toJson().run(NetworkCore.getInstance().getCon());
				JSONArray top50Elo = new JSONArray(str.equals("null") ? ("[]"):(str));
				
				for (int i = 0; i < 50; i++) {
					top50EloInv.removeSlotItem(i);
				}
				for (int i = 0; i < top50Elo.length(); i++) {
					ItemStack is = new ItemStackBuilderUtil().toSkullBuilder().withOwner(top50Elo.getJSONObject(i).getString("name")).buildSkull();
					SkullMeta meta = (SkullMeta) is.getItemMeta();
					meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "" + (i + 1) + ChatColor.GRAY + ": " + ChatColor.RESET + "" + PlayerRank.formatTextByRank(PlayerRank.fromString(top50Elo.getJSONObject(i).getString("rank")), top50Elo.getJSONObject(i).getString("name")));
					meta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "Elo: " + ChatColor.GOLD + top50Elo.getJSONObject(i).getInt("elo")));
					is.setItemMeta(meta);
					SlotItem entry = new SlotItem(is);
					entry.setLore(ChatColor.LIGHT_PURPLE + "Elo: " + ChatColor.GOLD + top50Elo.getJSONObject(i).getInt("elo"));
					top50EloInv.addSlotItem(i, entry);
					entry.setOnClick(new InventoryRunnable() {
						@Override
						public void runOnClick(InventoryClickEvent e) {
							int i = e.getRawSlot();
							Inventory playersStats = new Inventory(top50Elo.getJSONObject(i).getString("name") + "'s USG Stats", 1);
							
							ItemStack is = new ItemStackBuilderUtil().toSkullBuilder().withOwner(top50Elo.getJSONObject(i).getString("name")).buildSkull();
							SkullMeta meta = (SkullMeta) is.getItemMeta();
							meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "" + (i + 1) + ChatColor.GRAY + ": " + ChatColor.RESET + "" + PlayerRank.formatTextByRank(PlayerRank.fromString(top50Elo.getJSONObject(i).getString("rank")), top50Elo.getJSONObject(i).getString("name")));
							is.setItemMeta(meta);
							SlotItem head = new SlotItem(is);
							SlotItem elo = new SlotItem(ChatColor.RED + "" + ChatColor.BOLD + "ELO", ChatColor.GOLD + "" + top50Elo.getJSONObject(i).getInt("elo"), 0, Material.MAGMA_CREAM);
							SlotItem wins = new SlotItem(ChatColor.AQUA + "" + ChatColor.BOLD + "Wins", ChatColor.GOLD + "" + top50Elo.getJSONObject(i).getInt("wins"), 0, Material.DIAMOND);
							SlotItem kills = new SlotItem(ChatColor.GRAY + "" + ChatColor.BOLD + "Kills", ChatColor.GOLD + "" + top50Elo.getJSONObject(i).getInt("kills"), 0, Material.IRON_AXE);
							SlotItem games = new SlotItem(ChatColor.GREEN + "" + ChatColor.BOLD + "Games", ChatColor.GOLD + "" + top50Elo.getJSONObject(i).getInt("games"), 0, Material.SLIME_BALL);
							SlotItem bowAccuracy = new SlotItem(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Bow Accuracy", ChatColor.GOLD + "" + String.format("%6.4f", (top50Elo.getJSONObject(i).getDouble("arrowsLanded") / (top50Elo.getJSONObject(i).getDouble("arrowsFired") == 0 ? (1):(top50Elo.getJSONObject(i).getDouble("arrowsFired"))))), 0, Material.BOW);
							
							playersStats.removeSlotItem(1);
							playersStats.removeSlotItem(3);
							playersStats.removeSlotItem(4);
							playersStats.removeSlotItem(5);
							playersStats.removeSlotItem(6);
							playersStats.removeSlotItem(7);
							
							playersStats.addSlotItem(1, head);
							playersStats.addSlotItem(3, elo);
							playersStats.addSlotItem(4, wins);
							playersStats.addSlotItem(5, kills);
							playersStats.addSlotItem(6, games);
							playersStats.addSlotItem(7, bowAccuracy);
							
							InventoryS.openInventory((Player) e.getWhoClicked(), top50Elo.getJSONObject(i).getString("name") + "'s USG Stats");
							
							InventoryS.registerInventory(NetworkCore.getInstance(), playersStats);
						}
					});
					InventoryS.openInventory((Player) e.getWhoClicked(), "USG Top 50: Elo");
				}
			}
		});
		InventoryS.registerInventory(NetworkCore.getInstance(), top50EloInv);
		
		//////////Wins
		Inventory top50WinsInv = new Inventory("USG Top 50: Wins", 6);
		wins.setOnClick(new InventoryRunnable() {
			@Override
			public void runOnClick(InventoryClickEvent e) {
				String str = RethinkDB.r.table("usgStatsBeta").union(RethinkDB.r.table("usgStats")).eqJoin("id", RethinkDB.r.table("playerdata")).zip().orderBy(RethinkDB.r.desc("wins")).limit(50).toJson().run(NetworkCore.getInstance().getCon());
				JSONArray top50Wins = new JSONArray(str.equals("null") ? ("[]"):(str));				
				
				for (int i = 0; i < 50; i++) {
					top50WinsInv.removeSlotItem(i);
				}
				for (int i = 0; i < top50Wins.length(); i++) {
					ItemStack is = new ItemStackBuilderUtil().toSkullBuilder().withOwner(top50Wins.getJSONObject(i).getString("name")).buildSkull();
					SkullMeta meta = (SkullMeta) is.getItemMeta();
					meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "" + (i + 1) + ChatColor.GRAY + ": " + ChatColor.RESET + "" + PlayerRank.formatTextByRank(PlayerRank.fromString(top50Wins.getJSONObject(i).getString("rank")), top50Wins.getJSONObject(i).getString("name")));
					meta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "Wins: " + ChatColor.GOLD + top50Wins.getJSONObject(i).getInt("wins")));
					is.setItemMeta(meta);
					SlotItem entry = new SlotItem(is);
					entry.setLore(ChatColor.LIGHT_PURPLE + "Wins: " + ChatColor.GOLD + top50Wins.getJSONObject(i).getInt("wins"));
					top50WinsInv.addSlotItem(i, entry);
					entry.setOnClick(new InventoryRunnable() {
						@Override
						public void runOnClick(InventoryClickEvent e) {
							int i = e.getRawSlot();
							Inventory playersStats = new Inventory(top50Wins.getJSONObject(i).getString("name") + "'s USG Stats", 1);
							
							ItemStack is = new ItemStackBuilderUtil().toSkullBuilder().withOwner(top50Wins.getJSONObject(i).getString("name")).buildSkull();
							SkullMeta meta = (SkullMeta) is.getItemMeta();
							meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "" + (i + 1) + ChatColor.GRAY + ": " + ChatColor.RESET + "" + PlayerRank.formatTextByRank(PlayerRank.fromString(top50Wins.getJSONObject(i).getString("rank")), top50Wins.getJSONObject(i).getString("name")));
							is.setItemMeta(meta);
							SlotItem head = new SlotItem(is);
							SlotItem elo = new SlotItem(ChatColor.RED + "" + ChatColor.BOLD + "ELO", ChatColor.GOLD + "" + top50Wins.getJSONObject(i).getInt("elo"), 0, Material.MAGMA_CREAM);
							SlotItem wins = new SlotItem(ChatColor.AQUA + "" + ChatColor.BOLD + "Wins", ChatColor.GOLD + "" + top50Wins.getJSONObject(i).getInt("wins"), 0, Material.DIAMOND);
							SlotItem kills = new SlotItem(ChatColor.GRAY + "" + ChatColor.BOLD + "Kills", ChatColor.GOLD + "" + top50Wins.getJSONObject(i).getInt("kills"), 0, Material.IRON_AXE);
							SlotItem games = new SlotItem(ChatColor.GREEN + "" + ChatColor.BOLD + "Games", ChatColor.GOLD + "" + top50Wins.getJSONObject(i).getInt("games"), 0, Material.SLIME_BALL);
							SlotItem bowAccuracy = new SlotItem(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Bow Accuracy", ChatColor.GOLD + "" + String.format("%6.4f", (top50Wins.getJSONObject(i).getDouble("arrowsLanded") / (top50Wins.getJSONObject(i).getDouble("arrowsFired") == 0 ? (1):(top50Wins.getJSONObject(i).getDouble("arrowsFired"))))), 0, Material.BOW);
							
							playersStats.removeSlotItem(1);
							playersStats.removeSlotItem(3);
							playersStats.removeSlotItem(4);
							playersStats.removeSlotItem(5);
							playersStats.removeSlotItem(6);
							playersStats.removeSlotItem(7);
							
							playersStats.addSlotItem(1, head);
							playersStats.addSlotItem(3, elo);
							playersStats.addSlotItem(4, wins);
							playersStats.addSlotItem(5, kills);
							playersStats.addSlotItem(6, games);
							playersStats.addSlotItem(7, bowAccuracy);
							
							InventoryS.openInventory((Player) e.getWhoClicked(), top50Wins.getJSONObject(i).getString("name") + "'s USG Stats");
							
							InventoryS.registerInventory(NetworkCore.getInstance(), playersStats);
						}
					});
					InventoryS.openInventory((Player) e.getWhoClicked(), "USG Top 50: Wins");
				}
			}
		});
		InventoryS.registerInventory(NetworkCore.getInstance(), top50WinsInv);
		
		//////////Kills
		Inventory top50KillsInv = new Inventory("USG Top 50: Kills", 6);
		kills.setOnClick(new InventoryRunnable() {
			@Override
			public void runOnClick(InventoryClickEvent e) {
				String str = RethinkDB.r.table("usgStatsBeta").union(RethinkDB.r.table("usgStats")).eqJoin("id", RethinkDB.r.table("playerdata")).zip().orderBy(RethinkDB.r.desc("kills")).limit(50).toJson().run(NetworkCore.getInstance().getCon());
				JSONArray top50Kills = new JSONArray(str.equals("null") ? ("[]"):(str));				
				
				for (int i = 0; i < 50; i++) {
					top50KillsInv.removeSlotItem(i);
				}
				for (int i = 0; i < top50Kills.length(); i++) {
					ItemStack is = new ItemStackBuilderUtil().toSkullBuilder().withOwner(top50Kills.getJSONObject(i).getString("name")).buildSkull();
					SkullMeta meta = (SkullMeta) is.getItemMeta();
					meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "" + (i + 1) + ChatColor.GRAY + ": " + ChatColor.RESET + "" + PlayerRank.formatTextByRank(PlayerRank.fromString(top50Kills.getJSONObject(i).getString("rank")), top50Kills.getJSONObject(i).getString("name")));
					meta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "Kills: " + ChatColor.GOLD + top50Kills.getJSONObject(i).getInt("kills")));
					is.setItemMeta(meta);
					SlotItem entry = new SlotItem(is);
					entry.setLore(ChatColor.LIGHT_PURPLE + "Kills: " + ChatColor.GOLD + top50Kills.getJSONObject(i).getInt("kills"));
					top50KillsInv.addSlotItem(i, entry);
					entry.setOnClick(new InventoryRunnable() {
						@Override
						public void runOnClick(InventoryClickEvent e) {
							int i = e.getRawSlot();
							Inventory playersStats = new Inventory(top50Kills.getJSONObject(i).getString("name") + "'s USG Stats", 1);
							
							ItemStack is = new ItemStackBuilderUtil().toSkullBuilder().withOwner(top50Kills.getJSONObject(i).getString("name")).buildSkull();
							SkullMeta meta = (SkullMeta) is.getItemMeta();
							meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "" + (i + 1) + ChatColor.GRAY + ": " + ChatColor.RESET + "" + PlayerRank.formatTextByRank(PlayerRank.fromString(top50Kills.getJSONObject(i).getString("rank")), top50Kills.getJSONObject(i).getString("name")));
							is.setItemMeta(meta);
							SlotItem head = new SlotItem(is);
							SlotItem elo = new SlotItem(ChatColor.RED + "" + ChatColor.BOLD + "ELO", ChatColor.GOLD + "" + top50Kills.getJSONObject(i).getInt("elo"), 0, Material.MAGMA_CREAM);
							SlotItem wins = new SlotItem(ChatColor.AQUA + "" + ChatColor.BOLD + "Wins", ChatColor.GOLD + "" + top50Kills.getJSONObject(i).getInt("wins"), 0, Material.DIAMOND);
							SlotItem kills = new SlotItem(ChatColor.GRAY + "" + ChatColor.BOLD + "Kills", ChatColor.GOLD + "" + top50Kills.getJSONObject(i).getInt("kills"), 0, Material.IRON_AXE);
							SlotItem games = new SlotItem(ChatColor.GREEN + "" + ChatColor.BOLD + "Games", ChatColor.GOLD + "" + top50Kills.getJSONObject(i).getInt("games"), 0, Material.SLIME_BALL);
							SlotItem bowAccuracy = new SlotItem(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Bow Accuracy", ChatColor.GOLD + "" + String.format("%6.4f", (top50Kills.getJSONObject(i).getDouble("arrowsLanded") / (top50Kills.getJSONObject(i).getDouble("arrowsFired") == 0 ? (1):(top50Kills.getJSONObject(i).getDouble("arrowsFired"))))), 0, Material.BOW);
							
							playersStats.removeSlotItem(1);
							playersStats.removeSlotItem(3);
							playersStats.removeSlotItem(4);
							playersStats.removeSlotItem(5);
							playersStats.removeSlotItem(6);
							playersStats.removeSlotItem(7);
							
							playersStats.addSlotItem(1, head);
							playersStats.addSlotItem(3, elo);
							playersStats.addSlotItem(4, wins);
							playersStats.addSlotItem(5, kills);
							playersStats.addSlotItem(6, games);
							playersStats.addSlotItem(7, bowAccuracy);
							
							InventoryS.openInventory((Player) e.getWhoClicked(), top50Kills.getJSONObject(i).getString("name") + "'s USG Stats");
							
							InventoryS.registerInventory(NetworkCore.getInstance(), playersStats);
						}
					});
					InventoryS.openInventory((Player) e.getWhoClicked(), "USG Top 50: Kills");
				}
			}
		});
		InventoryS.registerInventory(NetworkCore.getInstance(), top50KillsInv);
		
		//////////Games
		Inventory top50GamesInv = new Inventory("USG Top 50: Games", 6);
		games.setOnClick(new InventoryRunnable() {
			@Override
			public void runOnClick(InventoryClickEvent e) {
				String str = RethinkDB.r.table("usgStatsBeta").union(RethinkDB.r.table("usgStats")).eqJoin("id", RethinkDB.r.table("playerdata")).zip().orderBy(RethinkDB.r.desc("games")).limit(50).toJson().run(NetworkCore.getInstance().getCon());
				JSONArray top50Games = new JSONArray(str.equals("null") ? ("[]"):(str));				
				
				for (int i = 0; i < 50; i++) {
					top50GamesInv.removeSlotItem(i);
				}
				
				for (int i = 0; i < top50Games.length(); i++) {
					ItemStack is = new ItemStackBuilderUtil().toSkullBuilder().withOwner(top50Games.getJSONObject(i).getString("name")).buildSkull();
					SkullMeta meta = (SkullMeta) is.getItemMeta();
					meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "" + (i + 1) + ChatColor.GRAY + ": " + ChatColor.RESET + "" + PlayerRank.formatTextByRank(PlayerRank.fromString(top50Games.getJSONObject(i).getString("rank")), top50Games.getJSONObject(i).getString("name")));
					meta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "Games: " + ChatColor.GOLD + top50Games.getJSONObject(i).getInt("games")));
					is.setItemMeta(meta);
					SlotItem entry = new SlotItem(is);
					entry.setLore(ChatColor.LIGHT_PURPLE + "Games: " + ChatColor.GOLD + top50Games.getJSONObject(i).getInt("games"));
					
					top50GamesInv.addSlotItem(i, entry);
					entry.setOnClick(new InventoryRunnable() {
						@Override
						public void runOnClick(InventoryClickEvent e) {
							int i = e.getRawSlot();
							Inventory playersStats = new Inventory(top50Games.getJSONObject(i).getString("name") + "'s USG Stats", 1);
							
							ItemStack is = new ItemStackBuilderUtil().toSkullBuilder().withOwner(top50Games.getJSONObject(i).getString("name")).buildSkull();
							SkullMeta meta = (SkullMeta) is.getItemMeta();
							meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "" + (i + 1) + ChatColor.GRAY + ": " + ChatColor.RESET + "" + PlayerRank.formatTextByRank(PlayerRank.fromString(top50Games.getJSONObject(i).getString("rank")), top50Games.getJSONObject(i).getString("name")));
							is.setItemMeta(meta);
							SlotItem head = new SlotItem(is);
							SlotItem elo = new SlotItem(ChatColor.RED + "" + ChatColor.BOLD + "ELO", ChatColor.GOLD + "" + top50Games.getJSONObject(i).getInt("elo"), 0, Material.MAGMA_CREAM);
							SlotItem wins = new SlotItem(ChatColor.AQUA + "" + ChatColor.BOLD + "Wins", ChatColor.GOLD + "" + top50Games.getJSONObject(i).getInt("wins"), 0, Material.DIAMOND);
							SlotItem kills = new SlotItem(ChatColor.GRAY + "" + ChatColor.BOLD + "Kills", ChatColor.GOLD + "" + top50Games.getJSONObject(i).getInt("kills"), 0, Material.IRON_AXE);
							SlotItem games = new SlotItem(ChatColor.GREEN + "" + ChatColor.BOLD + "Games", ChatColor.GOLD + "" + top50Games.getJSONObject(i).getInt("games"), 0, Material.SLIME_BALL);
							SlotItem bowAccuracy = new SlotItem(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Bow Accuracy", ChatColor.GOLD + "" + String.format("%6.4f", (top50Games.getJSONObject(i).getDouble("arrowsLanded") / (top50Games.getJSONObject(i).getDouble("arrowsFired") == 0 ? (1):(top50Games.getJSONObject(i).getDouble("arrowsFired"))))), 0, Material.BOW);
							
							playersStats.removeSlotItem(1);
							playersStats.removeSlotItem(3);
							playersStats.removeSlotItem(4);
							playersStats.removeSlotItem(5);
							playersStats.removeSlotItem(6);
							playersStats.removeSlotItem(7);
							
							playersStats.addSlotItem(1, head);
							playersStats.addSlotItem(3, elo);
							playersStats.addSlotItem(4, wins);
							playersStats.addSlotItem(5, kills);
							playersStats.addSlotItem(6, games);
							playersStats.addSlotItem(7, bowAccuracy);
							
							InventoryS.openInventory((Player) e.getWhoClicked(), top50Games.getJSONObject(i).getString("name") + "'s USG Stats");
							
							InventoryS.registerInventory(NetworkCore.getInstance(), playersStats);
						}
					});
					InventoryS.openInventory((Player) e.getWhoClicked(), "USG Top 50: Games");
				}
			}
		});
		InventoryS.registerInventory(NetworkCore.getInstance(), top50GamesInv);
		
		
		//////// Your stats
		stat.setOnClick(new InventoryRunnable() {
			@Override
			public void runOnClick(InventoryClickEvent e) {
				NetworkPlayer np = NetworkPlayer.getNetworkPlayerByUUID(e.getWhoClicked().getUniqueId().toString());
				Player p = np.getPlayer();
				String eloStr = RethinkDB.r.table("usgStatsBeta").union(RethinkDB.r.table("usgStats")).orderBy(RethinkDB.r.desc("elo")).offsetsOf(row -> row.g("id").eq(p.getUniqueId().toString())).toJson().run(NetworkCore.getInstance().getCon());
				int eloPos = Integer.parseInt(eloStr.replaceAll("[^0-9]", "").equals("") ? (-2 + ""):eloStr.replaceAll("[^0-9]", "")) + 1;
				
				String winsStr = RethinkDB.r.table("usgStatsBeta").union(RethinkDB.r.table("usgStats")).orderBy(RethinkDB.r.desc("wins")).offsetsOf(row -> row.g("id").eq(p.getUniqueId().toString())).toJson().run(NetworkCore.getInstance().getCon());
				int winsPos = Integer.parseInt(winsStr.replaceAll("[^0-9]", "").equals("") ? (-2 + ""):winsStr.replaceAll("[^0-9]", "")) + 1;
				
				String killsStr = RethinkDB.r.table("usgStatsBeta").union(RethinkDB.r.table("usgStats")).orderBy(RethinkDB.r.desc("kills")).offsetsOf(row -> row.g("id").eq(p.getUniqueId().toString())).toJson().run(NetworkCore.getInstance().getCon());
				int killsPos = Integer.parseInt(killsStr.replaceAll("[^0-9]", "").equals("") ? (-2 + ""):killsStr.replaceAll("[^0-9]", "")) + 1;
				
				String gamesStr = RethinkDB.r.table("usgStatsBeta").union(RethinkDB.r.table("usgStats")).orderBy(RethinkDB.r.desc("games")).offsetsOf(row -> row.g("id").eq(p.getUniqueId().toString())).toJson().run(NetworkCore.getInstance().getCon());
				int gamesPos = Integer.parseInt(gamesStr.replaceAll("[^0-9]", "").equals("") ? (-2 + ""):gamesStr.replaceAll("[^0-9]", "")) + 1;
				
				Inventory playersStats = new Inventory(p.getName() + "'s USG Stats", 1);
				
				ItemStack is = new ItemStackBuilderUtil().toSkullBuilder().withOwner(p.getName()).buildSkull();
				SkullMeta meta = (SkullMeta) is.getItemMeta();
				meta.setDisplayName(PlayerRank.formatTextByRank(np.getPlayerRank(), p.getName()) + "" + ChatColor.RESET + "'s Stats");
				List<String> loreList = new ArrayList<String>();
				loreList.add(ChatColor.GOLD + "" + ChatColor.BOLD + "" + eloPos + "" + ChatColor.GRAY + ": " + ChatColor.LIGHT_PURPLE + "ELO Rank");
				loreList.add(ChatColor.GOLD + "" + ChatColor.BOLD + "" + winsPos + "" + ChatColor.GRAY + ": " + ChatColor.LIGHT_PURPLE + "Wins Rank");
				loreList.add(ChatColor.GOLD + "" + ChatColor.BOLD + "" + killsPos + "" + ChatColor.GRAY + ": " + ChatColor.LIGHT_PURPLE + "Kills Rank");
				loreList.add(ChatColor.GOLD + "" + ChatColor.BOLD + "" + gamesPos + "" + ChatColor.GRAY + ": " + ChatColor.LIGHT_PURPLE + "Games Rank");
				meta.setLore(loreList);
				is.setItemMeta(meta);
				
				SlotItem head = new SlotItem(is);
				SlotItem elo = new SlotItem(ChatColor.RED + "" + ChatColor.BOLD + "ELO", ChatColor.GOLD + "" + (np.getData().has("usgStats") ? np.getData().getJSONObject("usgStats").getInt("elo"):0), 0, Material.MAGMA_CREAM);
				SlotItem wins = new SlotItem(ChatColor.AQUA + "" + ChatColor.BOLD + "Wins", ChatColor.GOLD + "" + (np.getData().has("usgStats") ? np.getData().getJSONObject("usgStats").getInt("wins"):0), 0, Material.DIAMOND);
				SlotItem kills = new SlotItem(ChatColor.GRAY + "" + ChatColor.BOLD + "Kills", ChatColor.GOLD + "" + (np.getData().has("usgStats") ? np.getData().getJSONObject("usgStats").getInt("kills"):0), 0, Material.IRON_AXE);
				SlotItem games = new SlotItem(ChatColor.GREEN + "" + ChatColor.BOLD + "Games", ChatColor.GOLD + "" + (np.getData().has("usgStats") ? np.getData().getJSONObject("usgStats").getInt("games"):0), 0, Material.SLIME_BALL);
				SlotItem bowAccuracy = new SlotItem(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Bow Accuracy",
						ChatColor.GOLD + ""
								+ String.format("%6.4f",
										(np.getData().has("usgStats")
												? (np.getData().getJSONObject("usgStats").getDouble("arrowsLanded")
														/ (np.getData().getJSONObject("usgStats").getDouble("arrowsFired") == 0
																? (1)
																: (np.getData().getJSONObject("usgStats")
																		.getDouble("arrowsFired"))))
												: 0)),
						0, Material.BOW);
				
				playersStats.removeSlotItem(1);
				playersStats.removeSlotItem(3);
				playersStats.removeSlotItem(4);
				playersStats.removeSlotItem(5);
				playersStats.removeSlotItem(6);
				playersStats.removeSlotItem(7);
				
				playersStats.addSlotItem(1, head);
				playersStats.addSlotItem(3, elo);
				playersStats.addSlotItem(4, wins);
				playersStats.addSlotItem(5, kills);
				playersStats.addSlotItem(6, games);
				playersStats.addSlotItem(7, bowAccuracy);
				
				InventoryS.registerInventory(NetworkCore.getInstance(), playersStats);
				InventoryS.openInventory(p, p.getName() + "'s USG Stats");
				
			}
		});
		
		
		statsInv.addSlotItem(4, usg);
	}
	
	private void setupInfoItem() {
		SlotItem rules = new SlotItem(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Rules", "", 0, Material.ANVIL);
		SlotItem website = new SlotItem(ChatColor.RED + "" + ChatColor.BOLD + "Website", "", 0, Material.BLAZE_ROD);
		SlotItem twitter = new SlotItem(ChatColor.BLUE + "" + ChatColor.BOLD + "Twitter", "", 0, Material.BOOK_AND_QUILL);
		SlotItem ts = new SlotItem(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "TeamSpeak", "", 0, Material.SNOW_BALL);
		SlotItem discord = new SlotItem(ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Discord", "", 0, Material.BAKED_POTATO);
		
		rules.setOnClick(new InventoryRunnable() {
			@Override
			public void runOnClick(InventoryClickEvent e) {
				e.getWhoClicked().sendMessage("");

				FancyMessage fm = new FancyMessage(NetworkCore.prefixStandard + "" + ChatColor.GOLD + "" + ChatColor.BOLD + "Click here to open the Rules!");
				fm.link("https://forums.orionmc.net/link-forums/rules.57/");
				fm.tooltip(ChatColor.GOLD + "" + ChatColor.BOLD + "Click this message to view the Rules!");
				fm.send(e.getWhoClicked());
				
				e.getWhoClicked().sendMessage("");
			}
		});

		website.setOnClick(new InventoryRunnable() {
			@Override
			public void runOnClick(InventoryClickEvent e) {
				e.getWhoClicked().sendMessage("");
				
				FancyMessage fm = new FancyMessage(NetworkCore.prefixStandard + "" + ChatColor.GOLD + "" + ChatColor.BOLD + "Click here to open the Website!");
				fm.link("https://orionmc.net/");
				fm.tooltip(ChatColor.GOLD + "" + ChatColor.BOLD + "Click this message to view the Website!");
				fm.send(e.getWhoClicked());
				
				e.getWhoClicked().sendMessage("");
			}
		});

		twitter.setOnClick(new InventoryRunnable() {
			@Override
			public void runOnClick(InventoryClickEvent e) {
				e.getWhoClicked().sendMessage("");
				
				FancyMessage fm = new FancyMessage(NetworkCore.prefixStandard + "" + ChatColor.GOLD + "" + ChatColor.BOLD + "Click here to visit our Twitter page!");
				fm.link("https://twitter.com/orionnetworkmc?lang=en");
				fm.tooltip(ChatColor.GOLD + "" + ChatColor.BOLD + "Click this message to visit our Twitter Page!");
				fm.send(e.getWhoClicked());
				
				e.getWhoClicked().sendMessage("");
			}
		});

		ts.setOnClick(new InventoryRunnable() {
			@Override
			public void runOnClick(InventoryClickEvent e) {
				e.getWhoClicked().sendMessage("");
				
				FancyMessage fm = new FancyMessage(NetworkCore.prefixStandard + "" + ChatColor.GOLD + "" + ChatColor.BOLD + "Click here to join ts.orionmc.net!");
				fm.link("teamspeakweb.orionmc.net");
				fm.tooltip(ChatColor.GOLD + "" + ChatColor.BOLD + "Click this message to Connect to our Teamspeak Server!");
				fm.send(e.getWhoClicked());
				
				e.getWhoClicked().sendMessage("");
			}
		});

		discord.setOnClick(new InventoryRunnable() {
			@Override
			public void runOnClick(InventoryClickEvent e) {
				e.getWhoClicked().sendMessage("");
				
				FancyMessage fm = new FancyMessage(NetworkCore.prefixStandard + "" + ChatColor.GOLD + "" + ChatColor.BOLD + "Click here to connect to our Discord server!");
				fm.link("https://discord.gg/wY55PCv");
				fm.tooltip(ChatColor.GOLD + "" + ChatColor.BOLD + "Click this message to Connect to our Discord Server!");
				fm.send(e.getWhoClicked());
				
				e.getWhoClicked().sendMessage("");
			}
		});
		
		infoInv.addSlotItem(0, rules);
		infoInv.addSlotItem(2, website);
		infoInv.addSlotItem(4, twitter);
		infoInv.addSlotItem(6, ts);
		infoInv.addSlotItem(8, discord);
		
	}
	
}
