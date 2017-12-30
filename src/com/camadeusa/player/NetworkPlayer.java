package com.camadeusa.player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.JSONObject;

import com.camadeusa.NetworkCore;
import com.camadeusa.chat.ChatManager;
import com.camadeusa.module.anticheat.CheckType;
import com.camadeusa.network.ServerMode;
import com.camadeusa.utility.Random;
import com.camadeusa.utility.subservers.event.SubserversEvents;
import com.camadeusa.utility.subservers.packet.PacketDownloadPlayerInfo;
import com.camadeusa.utility.subservers.packet.PacketUpdateDatabaseValue;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Cursor;

import net.ME1312.SubServers.Client.Bukkit.SubAPI;

public class NetworkPlayer implements Listener {
	private static List<NetworkPlayer> archrPlayerList = new ArrayList<NetworkPlayer>();
	private PlayerState playerstate;
	private PlayerRank rank;
	private PlayerSettings playerSettings;	
	private HashMap<String, JSONObject> datacache = new HashMap<>();
	String playeruuid;
	JSONObject data;
	HashMap<CheckType, Integer> violationLevels = new HashMap<>();
	
	ArrayList<Cursor> cursors = new ArrayList<>();

	public NetworkPlayer() {
		playerstate = PlayerState.NORMAL;
		rank = PlayerRank.Player;
	}

	public NetworkPlayer(Player p) {
		playeruuid = p.getUniqueId().toString();
		playerstate = PlayerState.NORMAL;
		rank = PlayerRank.Player;
		
		//Track Changes to the accrued data across the multiple tables that are combined here.
		Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
			@Override
			public void run() {
				Cursor cur = RethinkDB.r.db("Orion_Network").table("playerdata").get(p.getUniqueId().toString())
						.changes().run(NetworkCore.getInstance().getCon());
				for (Object change : cur) {
					reloadPlayerData();
				}
				cursors.add(cur);
				Cursor curk = RethinkDB.r.db("Orion_Network").table("kicks").get(p.getUniqueId().toString()).changes()
						.run(NetworkCore.getInstance().getCon());
				for (Object change : curk) {
					reloadPlayerData();
				}
				cursors.add(curk);
				Cursor curb = RethinkDB.r.db("Orion_Network").table("bans").get(p.getUniqueId().toString()).changes()
						.run(NetworkCore.getInstance().getCon());
				for (Object change : curb) {
					reloadPlayerData();
				}
				cursors.add(curb);
				Cursor curm = RethinkDB.r.db("Orion_Network").table("mutes").get(p.getUniqueId().toString()).changes()
						.run(NetworkCore.getInstance().getCon());
				for (Object change : curm) {
					reloadPlayerData();
				}
				cursors.add(curm);
				Cursor curs = RethinkDB.r.db("Orion_Network").table("playersettings").get(p.getUniqueId().toString())
						.changes().run(NetworkCore.getInstance().getCon());
				for (Object change : curs) {
					reloadPlayerData();
				}
				cursors.add(curs);

			}
		});
		
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(UUID.fromString(playeruuid));
	}

	public PlayerState getPlayerState() {
		return playerstate;
	}

	public PlayerRank getPlayerRank() {
		return rank;
	}

	public static List<NetworkPlayer> getNetworkPlayerList() {
		return archrPlayerList;
	}

	public static NetworkPlayer getNetworkPlayerByUUID(String uuid) {
		for (NetworkPlayer ap : archrPlayerList) {
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
	
	public void updatePlayerstate(PlayerState state) {
		SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(playeruuid, "state", state.toString()));
	}
	
	public void updateRank(PlayerRank rank) {
		SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(playeruuid, "rank", rank.toString()));
	}

	public void setPlayerstate(PlayerState playerstate) {
		this.playerstate = playerstate;
	}

	public void setRank(PlayerRank rank) {
		this.rank = rank;
	}

	public PlayerSettings getPlayerSettings() {
		return playerSettings;
	}

	private void setPlayerSettings(PlayerSettings playerSettings) {
		this.playerSettings = playerSettings;
	}

	public HashMap<CheckType, Integer> getViolationLevels() {
		return violationLevels;
	}

	public void setViolationLevels(HashMap<CheckType, Integer> violationLevels) {
		this.violationLevels = violationLevels;
	}

	public static ArrayList<NetworkPlayer> getOnlinePlayers() {
		ArrayList<NetworkPlayer> aList = new ArrayList<>();
		for (NetworkPlayer ap : getNetworkPlayerList()) {
			if (!ap.getPlayerState().toString().equals(PlayerState.GHOST.toString())) {
				aList.add(ap);
			}
		}
		return aList;
	}

	public static ArrayList<NetworkPlayer> getOnlinePlayersByRank(PlayerRank p) {
		ArrayList<NetworkPlayer> aList = new ArrayList<>();
		for (NetworkPlayer ap : getNetworkPlayerList()) {
			if (ap.getPlayerState() != PlayerState.GHOST && ap.getPlayerRank() == p) {
				aList.add(ap);
			}
		}
		return aList;
	}

	public static ArrayList<NetworkPlayer> getOnlinePlayersByState(PlayerState p) {
		ArrayList<NetworkPlayer> aList = new ArrayList<>();
		for (NetworkPlayer ap : getNetworkPlayerList()) {
			if (ap.getPlayerState() == p) {
				aList.add(ap);
			}
		}
		return aList;
	}

	public static ArrayList<NetworkPlayer> getOnlinePlayersIncludingGhosts() {
		ArrayList<NetworkPlayer> aList = new ArrayList<>();
		for (NetworkPlayer ap : getNetworkPlayerList()) {
			aList.add(ap);
		}
		return aList;
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		NetworkPlayer aP = NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString());
		if (aP.getPlayerRank().getValue() >= PlayerRank.Admin.getValue()) {
			aP.getPlayer().setOp(false);
		}
		if (archrPlayerList.contains(aP)) {
			archrPlayerList.remove(aP);
		}
		aP.cursors.forEach(c -> {
			c.close();
		});
	}

	@EventHandler
	public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
		AtomicInteger intA = new AtomicInteger();
		intA.set(1);
		
		if (!SubserversEvents.connected) {
			event.disallow(Result.KICK_OTHER, "This server is not online yet.");
			return;
		}
		
		Bukkit.getScheduler().runTaskAsynchronously(NetworkCore.getInstance(), new Runnable() {
			@Override 
			public void run() {
				SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketDownloadPlayerInfo(event.getUniqueId().toString(), event.getName(), "-1", jsoninfo -> {
					if (jsoninfo.getJSONObject("data").has("bans")) {
						long bl = 0;
						for (String key : jsoninfo.getJSONObject("data").getJSONObject("bans").keySet()) {
							if (!key.equals("id")) {
								long lk = jsoninfo.getJSONObject("data").getJSONObject("bans").getJSONObject(key).getLong("banexpiredate");
								bl = bl > lk ? bl : lk; 
							}							
						}	
						if (bl > System.currentTimeMillis()) {
							event.disallow(Result.KICK_BANNED, NetworkCore.prefixStandard + ChatManager.translateFor("en", jsoninfo.getJSONObject("data").getString("locale"), "You have been banned... \nIf you believe this is an error, please post a dispute on our website.\n You are banned until:\n" ) + new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date(bl)));
						} else {
							if (!ServerMode.canJoin(PlayerRank.fromString(jsoninfo.getJSONObject("data").getString("rank")))) {
								event.disallow(Result.KICK_OTHER, NetworkCore.prefixError + ChatManager.translateFor("en", jsoninfo.getJSONObject("data").getString("locale"), "You are unable to join this server. Please try again later."));
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
						}	
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
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		NetworkPlayer aP = new NetworkPlayer(event.getPlayer());
		aP.setRank(PlayerRank.fromString(datacache.get(event.getPlayer().getUniqueId().toString()).getString("rank")));
		aP.setPlayerstate(
				PlayerState.fromString(datacache.get(event.getPlayer().getUniqueId().toString()).getString("state")));
		aP.getPlayer().setDisplayName(PlayerRank.formatNameByRankWOIcon(aP));
		if (aP.getPlayerRank().getValue() >= PlayerRank.Admin.getValue()) {
			aP.getPlayer().setOp(true);
		}
				
		aP.setPlayerSettings(new PlayerSettings(datacache.get(event.getPlayer().getUniqueId().toString()).getJSONObject("playersettings")));
		datacache.get(event.getPlayer().getUniqueId().toString()).remove("playersettings");
		
		aP.setData(datacache.get(event.getPlayer().getUniqueId().toString()));
		archrPlayerList.add(aP);
		datacache.remove(event.getPlayer().getUniqueId().toString());	
		

		event.setJoinMessage("");
		
	}
	
	

	public static void correctArchrPlayerList() {
		if (Bukkit.getOnlinePlayers().size() != getNetworkPlayerList().size()) {
			ArrayList<NetworkPlayer> toRemove = new ArrayList<>();
			for (NetworkPlayer ap : getNetworkPlayerList()) {
				if (!ap.getPlayer().isOnline()) {
					toRemove.add(ap);
					ap.cursors.forEach(c -> {
						c.close();
					});
				}
			}
			archrPlayerList.removeAll(toRemove);
		}
	}
	
	public static boolean kickPlayerForRoom(JSONObject playerData) {
		if (getOnlinePlayers().size() == NetworkCore.getConfigManger().getConfig("server", NetworkCore.getInstance()).getInt("maxplayers")) {
			ArrayList<NetworkPlayer> poolToKickFrom = new ArrayList<>();
			NetworkPlayer playerToKick = null;
			for (PlayerState ps : PlayerState.valuesOrderedForKickOrder()) {
				for (PlayerRank pr : PlayerRank.valuesordered()) {
					if (pr.getValue() < PlayerRank.fromString(playerData.get("rank").toString()).getValue() && PlayerState.fromString(playerData.get("state").toString()) != PlayerState.GHOST) {
						for (NetworkPlayer ap : getOnlinePlayers()) {
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
				final NetworkPlayer pp = playerToKick;
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
		if (getPlayer() != null && getPlayer().isOnline()) {

			SubAPI.getInstance().getSubDataNetwork()
					.sendPacket(new PacketDownloadPlayerInfo(playeruuid, getPlayer().getName(),
							getPlayer().getAddress().getAddress().toString().replace("/", ""), jsoninfo -> {
								setRank(PlayerRank.fromString(jsoninfo.getJSONObject("data").getString("rank")));
								setPlayerstate(
										PlayerState.fromString(jsoninfo.getJSONObject("data").getString("state")));
								getPlayer().setDisplayName(
										PlayerRank.formatNameByRankWOIcon(NetworkPlayer.getNetworkPlayerByUUID(playeruuid)));
								setData(jsoninfo.getJSONObject("data"));

							}));

		}

	}

}
