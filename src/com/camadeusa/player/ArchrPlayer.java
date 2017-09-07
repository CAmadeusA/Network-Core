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
import com.camadeusa.module.network.points.Basepoint;
import com.google.gdata.data.spreadsheet.ListEntry;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;

public class ArchrPlayer implements Listener {
	private static List<ArchrPlayer> archrPlayerList = new ArrayList<ArchrPlayer>();
	private PlayerState playerstate;
	private PlayerRank rank;
	private JSONObject elostore = new JSONObject();
	private JSONObject playersettings = new JSONObject();
	
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
	
	public static int getOnlinePlayers() {
		int count = 0;
		for (ArchrPlayer ap : getArchrPlayerList()) {
			if (ap.getPlayerState() != PlayerState.GHOST) {
				count++;
			}
		}
		return count;
	}
	
	public static int getOnlinePlayersIncludingGhosts() {
		return getArchrPlayerList().size();
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
	}

	// Database data pulling and updating on login/join.
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		ArchrPlayer aP = new ArchrPlayer(event.getPlayer());
		Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
			ListEntry row;

			@Override
			public void run() {

				try {
					row = NetworkCore.getInstance().playersDB.getRow("uuid",
							event.getPlayer().getUniqueId().toString());
				} catch (Exception e) {
				}
				if (row != null) {
					aP.setData(NetworkCore.getInstance().playersDB.getRowData(row));
				} else {
					Map<String, Object> data = generateBaseDBData(aP);
					NetworkCore.getInstance().playersDB.addData(data);
					aP.setData(data);

				}
				aP.setRank(PlayerRank.fromString(aP.getData().get("rank").toString()));

				aP.getPlayer().setDisplayName(PlayerRank.formatNameByRank(aP));

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

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		event.setJoinMessage("");
		archrPlayerList.add(aP);
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
