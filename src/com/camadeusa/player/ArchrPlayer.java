package com.camadeusa.player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.game.Gamemode;
import com.camadeusa.module.network.event.NetworkServerInfoEvents;
import com.camadeusa.module.network.points.Basepoint;
import com.camadeusa.utility.Random;
import com.camadeusa.utility.fetcher.NameFetcher;
import com.google.gdata.data.spreadsheet.ListEntry;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;

public class ArchrPlayer implements Listener {
	private static List<ArchrPlayer> archrPlayerList = new ArrayList<ArchrPlayer>();
	private PlayerState playerstate;
	private PlayerRank rank;
	private JSONObject elostore = new JSONObject();
	private JSONObject playersettings = new JSONObject();

	HashMap<String, Map<String, Object>> dataCache = new HashMap<>();

	Player player;
	Map<String, Object> data = new HashMap<>();

	public ArchrPlayer() {
		playerstate = PlayerState.NORMAL;
		rank = PlayerRank.Player;
	}

	public ArchrPlayer(Player p) {
		player = p;
		playerstate = PlayerState.NORMAL;
		rank = PlayerRank.Player;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player p) {
		player = p;
	}

	public PlayerState getPlayerState() {
		return playerstate;
	}

	public PlayerRank getPlayerRank() {
		return rank;
	}

	public static List<ArchrPlayer> getArchrPlayerList() {
		return archrPlayerList;
	}

	public static ArchrPlayer getArchrPlayerByUUID(String uuid) {
		for (ArchrPlayer ap : archrPlayerList) {
			if (uuid.equals(ap.getPlayer().getUniqueId().toString())) {
				return ap;
			}
		}
		return null;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public void setPlayerstate(PlayerState playerstate) {
		this.playerstate = playerstate;
	}

	public void setRank(PlayerRank rank) {
		this.rank = rank;
	}

	public <T extends Basepoint> T getElo(Gamemode eloid) {
		return (T) elostore.get(eloid.getValue());
	}

	public void setElo(Gamemode eloid, int value) {
		elostore.put(eloid.getValue(), value);
	}

	public Object getPlayerSettings(String tag) {
		return playersettings.get(tag);
	}

	public void setPlayerSettings(String tag, Object value) {
		elostore.put(tag, value);
	}

	public static Map<String, Object> generateBaseDBData(ArchrPlayer aP) {
		Map<String, Object> data = new HashMap<>();
		data.put("uuid", aP.getPlayer().getUniqueId().toString());
		data.put("username", aP.getPlayer().getName());
		data.put("rank", PlayerRank.Player);
		data.put("ipaddress", "0");
		data.put("banexpiredate", -1);
		data.put("muteexpiredate", -1);
		data.put("firstlogin", System.currentTimeMillis());
		data.put("previoususernames", new JSONArray().toString());
		data.put("previousipaddresses", new JSONArray().toString());
		data.put("kicks", new JSONArray().toString());
		data.put("mutes", new JSONArray().toString());
		data.put("bans", new JSONArray().toString());
		data.put("elos", new JSONObject().toString());
		data.put("playersettings", new JSONArray().toString());

		return data;
	}

	public static Map<String, Object> generateBaseDBData(String uuid, String username, String rank, String ipaddress,
			int banexpiredate, int muteexpiredate, long firstlogin) {
		Map<String, Object> data = new HashMap<>();
		data.put("uuid", uuid);
		data.put("username", username);
		data.put("rank", rank);
		data.put("ipaddress", ipaddress);
		data.put("banexpiredate", banexpiredate);
		data.put("muteexpiredate", muteexpiredate);
		data.put("firstlogin", firstlogin);
		data.put("previoususernames", new JSONArray().toString());
		data.put("previousipaddresses", new JSONArray().toString());
		data.put("kicks", new JSONArray().toString());
		data.put("mutes", new JSONArray().toString());
		data.put("bans", new JSONArray().toString());
		data.put("elos", new JSONObject().toString());
		data.put("playersettings", new JSONArray().toString());

		return data;
	}

	public static ArrayList<ArchrPlayer> getOnlinePlayers() {
		ArrayList<ArchrPlayer> aList = new ArrayList<>();
		for (ArchrPlayer ap : getArchrPlayerList()) {
			if (ap.getPlayerState() != PlayerState.GHOST) {
				aList.add(ap);
			}
		}
		return aList;
	}

	public static ArrayList<ArchrPlayer> getOnlinePlayersByRank(PlayerRank p) {
		ArrayList<ArchrPlayer> aList = new ArrayList<>();
		for (ArchrPlayer ap : getArchrPlayerList()) {
			if (ap.getPlayerState() != PlayerState.GHOST && ap.getPlayerRank() == p) {
				aList.add(ap);
			}
		}
		return aList;
	}

	public static ArrayList<ArchrPlayer> getOnlinePlayersIncludingGhosts() {
		ArrayList<ArchrPlayer> aList = new ArrayList<>();
		for (ArchrPlayer ap : getArchrPlayerList()) {
			aList.add(ap);
		}
		return aList;
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		ArchrPlayer aP = ArchrPlayer.getArchrPlayerByUUID(event.getPlayer().getUniqueId().toString());
		if (archrPlayerList.contains(aP)) {
			archrPlayerList.remove(aP);
		}
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		ArchrPlayer aP = ArchrPlayer.getArchrPlayerByUUID(event.getPlayer().getUniqueId().toString());
		if (archrPlayerList.contains(aP)) {
			archrPlayerList.remove(aP);
		}
	}

	@EventHandler
	public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
		AtomicInteger intA = new AtomicInteger();
		intA.set(1);
		Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
			@Override 
			public void run() {
				
				ListEntry row;
				try {
					row = NetworkCore.getInstance().playersDB.getRow("uuid", event.getUniqueId());
					Map<String, Object> result = NetworkCore.getInstance().playersDB.getRowData(row);
					dataCache.put(event.getUniqueId().toString(), result);
					if (Long.parseLong(result.get("banexpiredate").toString()) > System.currentTimeMillis()) {
						if (Long.parseLong((String) result.get("banexpiredate")) == (Long.MAX_VALUE)) {
							event.disallow(Result.KICK_BANNED,
									NetworkCore.prefixError + "You have been permanently banned.");
							
						} else {
							Date date = new Date(Long.parseLong((String) result.get("banexpiredate")));
							String myDateStr = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(date);
							event.disallow(Result.KICK_BANNED,
									NetworkCore.prefixError + "You have been banned until " + myDateStr + ". ");
							
						}
					}
					intA.set(0);
				} catch (Exception e) {
					Map<String, Object> datatemp = generateBaseDBData(event.getUniqueId().toString(), NameFetcher.getName(event.getUniqueId().toString()), "Player", "0", -1, -1, 0);
					NetworkCore.getInstance().playersDB.addData(datatemp);
					dataCache.put(event.getUniqueId().toString(), datatemp);
					intA.set(0);					
				}
			}
		});

		// Holds event from finishing while async (only to not lag main game thread) fetch finishes.
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis()-start < 9999999999999L) {
			if (intA.get() == 0) {
				break;
			}
		}
		
		
		if (getOnlinePlayers().size() == NetworkCore.getConfigManger().getConfig("server", NetworkCore.getInstance()).getInt("maxplayers")) {
			ArrayList<ArchrPlayer> poolToKickFrom = new ArrayList<>();
			ArchrPlayer playerToKick = null;
			for (PlayerState ps : PlayerState.valuesOrderedForKickOrder()) {
				for (PlayerRank pr : PlayerRank.valuesordered()) {
					if (PlayerRank.getValueByRank(pr) < PlayerRank.getValueByRank(
							PlayerRank.valueOf(dataCache.get(event.getUniqueId()).get("rank").toString()))) {

						for (ArchrPlayer ap : getOnlinePlayers()) {
							if (pr == ap.getPlayerRank() && ps == ap.getPlayerState()) {
								poolToKickFrom.add(ap);
							}
						}

						if (poolToKickFrom.size() > 0) {
							playerToKick = poolToKickFrom.get(Random.instance().nextInt(poolToKickFrom.size()));
							break;
						}
					}

				}
				if (playerToKick != null) {
					break;
				}
			}
			if (playerToKick != null) {
				playerToKick.getPlayer().kickPlayer("You were kicked to make room for a player with a higher rank. We appologize for the inconvienence.");
			} else {
				event.disallow(Result.KICK_OTHER, "This server is full. Please Try again later.");
				try {
					throw new Exception("Wow you really fucked up");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	}

	// Database data pulling and updating on login/join.
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		ArchrPlayer aP = new ArchrPlayer(event.getPlayer());
		aP.setData(dataCache.get(aP.getPlayer().getUniqueId().toString()));
		dataCache.remove(aP.getPlayer().getUniqueId().toString());
		Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
			@Override
			public void run() {

				try {
					if (!aP.getPlayer().getName().equalsIgnoreCase(aP.getData().get("username").toString())) {
						JSONArray previoususernames = new JSONArray(aP.getData().get("previoususernames").toString());
						previoususernames.put(aP.getData().get("username"));
						aP.getData().put("previoususernames", previoususernames);
						aP.getData().put("username", aP.getPlayer().getName());
					}
					if (!event.getPlayer().getAddress().getAddress().toString().replace("/", "")
							.equals(aP.getData().get("ipaddress"))) {
						JSONArray previousips = new JSONArray(aP.getData().get("previousipaddresses").toString());
						previousips.put(aP.getData().get("ipaddress"));
						aP.getData().put("previousipaddresses", previousips);
						aP.getData().put("ipaddress",
								aP.getPlayer().getAddress().getAddress().toString().replace("/", ""));
					}

					ListEntry rowupdate = NetworkCore.getInstance().playersDB.getRow("uuid",
							aP.getPlayer().getUniqueId());
					NetworkCore.getInstance().playersDB.updateRow(rowupdate, aP.getData());
					rowupdate.update();

					aP.setRank(PlayerRank.fromString(aP.getData().get("rank").toString()));
					aP.getPlayer().setDisplayName(PlayerRank.formatNameByRank(aP));
					event.setJoinMessage("");
					archrPlayerList.add(aP);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public static void correctArchrPlayerList() {
		if (Bukkit.getOnlinePlayers().size() != getArchrPlayerList().size()) {
			ArrayList<ArchrPlayer> toRemove = new ArrayList<>();
			for (ArchrPlayer ap : getArchrPlayerList()) {
				if (!ap.getPlayer().isOnline()) {
					toRemove.add(ap);
				}
			}
			archrPlayerList.removeAll(toRemove);
		}
	}

}
