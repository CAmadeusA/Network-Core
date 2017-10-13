package com.camadeusa.player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.JSONObject;

import com.camadeusa.NetworkCore;
import com.camadeusa.chat.ChatManager;
import com.camadeusa.utility.Random;
import com.camadeusa.utility.subservers.packet.PacketDownloadPlayerInfo;
import com.camadeusa.utility.subservers.packet.PacketUpdateDatabaseValue;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import net.ME1312.SubServers.Client.Bukkit.SubAPI;
import net.wesjd.anvilgui.AnvilGUI;

public class ArchrPlayer implements Listener {
	private static List<ArchrPlayer> archrPlayerList = new ArrayList<ArchrPlayer>();
	private PlayerState playerstate;
	private PlayerRank rank;
	
	private HashMap<String, JSONObject> datacache = new HashMap<>();

	Player player;
	JSONObject data;

	public ArchrPlayer() {
		playerstate = PlayerState.NORMAL;
		rank = PlayerRank.Player;
	}

	public ArchrPlayer(Player p) {
		player = p;
		playerstate = PlayerState.NORMAL;
		rank = PlayerRank.Player;
		
		// Any time the data in the database changes, it updates this NetworkPlayer's referenced data on their object. 
		NetworkCore.getInstance().getDatabase().child("game").child("players").child("data").child(p.getUniqueId().toString()).addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
				if (player.isOnline()) {
					reloadPlayerData();
				}
			}
			@Override
			public void onCancelled(DatabaseError arg0) {}
			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {}
			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {}
			@Override
			public void onChildRemoved(DataSnapshot arg0) {}
			
		});
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

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public void setPlayerstate(PlayerState playerstate) {
		this.playerstate = playerstate;
	}

	public void setRank(PlayerRank rank) {
		this.rank = rank;
	}

	public static ArrayList<ArchrPlayer> getOnlinePlayers() {
		ArrayList<ArchrPlayer> aList = new ArrayList<>();
		for (ArchrPlayer ap : getArchrPlayerList()) {
			if (!ap.getPlayerState().toString().equals(PlayerState.GHOST.toString())) {
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
		if (PlayerRank.getValueByRank(aP.getPlayerRank()) >= PlayerRank.getValueByRank(PlayerRank.Admin)) {
			aP.getPlayer().setOp(false);
		}
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
				SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketDownloadPlayerInfo(event.getUniqueId().toString(), event.getName(), "-1", jsoninfo -> {
					
					long bl = jsoninfo.getJSONObject("data").getLong("banexpiredate");
					if (bl > System.currentTimeMillis()) {
						event.disallow(Result.KICK_BANNED, NetworkCore.prefixStandard + ChatManager.translateFor("en", jsoninfo.getJSONObject("data").getString("locale"), "You have been banned... \nIf you believe this is an error, please post a dispute on our website.\n You are banned until: " ) + new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date(bl)));
					} else {
						Bukkit.getScheduler().runTask(NetworkCore.getInstance(), new Runnable() {
							@Override
							public void run() {
								if (!kickPlayerForRoom(jsoninfo.getJSONObject("data"))) {
									event.disallow(Result.KICK_FULL, NetworkCore.prefixStandard + ChatManager.translateFor("en", jsoninfo.getJSONObject("data").getString("locale"), "This server is full, sorry for the inconvienence."));
								}								
							}
						});
					}
					intA.set(0);
					
					datacache.put(event.getUniqueId().toString(), jsoninfo.getJSONObject("data"));
				}));				
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
		aP.setRank(PlayerRank.fromString(datacache.get(event.getPlayer().getUniqueId().toString()).getString("rank")));
		aP.setPlayerstate(
				PlayerState.fromString(datacache.get(event.getPlayer().getUniqueId().toString()).getString("state")));
		aP.getPlayer().setDisplayName(PlayerRank.formatNameByRank(aP));
		if (PlayerRank.getValueByRank(aP.getPlayerRank()) >= PlayerRank.getValueByRank(PlayerRank.Admin)) {
			aP.getPlayer().setOp(true);
		}
		aP.setData(datacache.get(event.getPlayer().getUniqueId().toString()));
		archrPlayerList.add(aP);
		datacache.remove(event.getPlayer().getUniqueId().toString());	

		event.setJoinMessage("");
		
		aP.reloadPlayerData();
		if (!aP.getData().has("requirepwonlogin")) {
			SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(aP.getPlayer().getUniqueId().toString(), "requirepwonlogin", "false"));			
		}
		
		if (aP.getData().has("requirepwonlogin") && aP.getData().getString("requirepwonlogin").equalsIgnoreCase("true")) {
			aP.getPlayer().chat("/authenticate");
		} else {
			SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(aP.getPlayer().getUniqueId().toString(), "authenticated", "true"));
		}
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
	
	public static boolean kickPlayerForRoom(JSONObject playerData) {
		if (getOnlinePlayers().size() == NetworkCore.getConfigManger().getConfig("server", NetworkCore.getInstance()).getInt("maxplayers")) {
			ArrayList<ArchrPlayer> poolToKickFrom = new ArrayList<>();
			ArchrPlayer playerToKick = null;
			for (PlayerState ps : PlayerState.valuesOrderedForKickOrder()) {
				for (PlayerRank pr : PlayerRank.valuesordered()) {
					if (PlayerRank.getValueByRank(pr) < PlayerRank.getValueByRank(
							PlayerRank.fromString(playerData.get("rank").toString())) && PlayerState.fromString(playerData.get("state").toString()) != PlayerState.GHOST) {

						for (ArchrPlayer ap : getOnlinePlayers()) {
							if (pr == ap.getPlayerRank() && ps == ap.getPlayerState()) {
								poolToKickFrom.add(ap);
							}
						}

						if (poolToKickFrom.size() > 0) {
							playerToKick = poolToKickFrom.get(Random.instance().nextInt(poolToKickFrom.size()));
							break;
						}
					} else if (PlayerState.fromString(playerData.get("state").toString()) == PlayerState.GHOST) {
						return true;
					}

				}
				if (playerToKick != null) {
					break;
				}
			}
			if (playerToKick != null) {
				final ArchrPlayer pp = playerToKick;
				Bukkit.getScheduler().runTask(NetworkCore.getInstance(), new Runnable() {
					@Override
					public void run() {
						pp.getPlayer().kickPlayer(NetworkCore.prefixStandard + ChatManager.translateFor("en", pp, "You were kicked to make room for a player with a higher rank. We appologize for the inconvienence."));
					}
				});
				return true;
			} else {
				return false;
			}

		} else {
			return true;			
		}
	}
	
	public void reloadPlayerData() {
		Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
			@Override 
			public void run() {
				SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketDownloadPlayerInfo(player.getUniqueId().toString(), player.getName(), player.getAddress().getAddress().toString().replace("/", ""), jsoninfo -> {
					setRank(PlayerRank.fromString(jsoninfo.getJSONObject("data").getString("rank")));
					setPlayerstate(PlayerState.fromString(jsoninfo.getJSONObject("data").getString("state")));
					getPlayer().setDisplayName(PlayerRank.formatNameByRank(ArchrPlayer.getArchrPlayerByUUID(player.getUniqueId().toString())));
					
					setData(jsoninfo.getJSONObject("data"));
					
				}));				
			}
		});
	}

}
