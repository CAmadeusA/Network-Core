package com.camadeusa.module.game;

import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.json.JSONObject;

import com.camadeusa.NetworkCore;
import com.camadeusa.chat.ChatManager;
import com.camadeusa.network.points.Basepoint;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.utility.Eloable;
import com.camadeusa.utility.Eloable.Outcome;
import com.camadeusa.utility.subservers.packet.PacketLogLeaderboardStats;

import net.ME1312.SubServers.Client.Bukkit.SubAPI;

public class LeaderboardToken {
	LinkedHashMap<UUID, LeaderboardPlayerStatToken> stats = new LinkedHashMap<>();
	
	public void addPlayer(NetworkPlayer np) {
		if (np.getData().has(GamemodeManager.getInstance().getGamemode().getValue().toLowerCase() + "Stats")) {
			stats.put(np.getPlayer().getUniqueId(), new LeaderboardPlayerStatToken(np.getData().getJSONObject(GamemodeManager.getInstance().getGamemode().getValue().toLowerCase() + "Stats").toString()));			
		} else {
			stats.put(np.getPlayer().getUniqueId(), new LeaderboardPlayerStatToken(np.getPlayer().getUniqueId(), 0, 0, 0, 0, 0f, 0, Basepoint.DEFAULT_INITIAL, Basepoint.DEFAULT_INITIAL, 0, 0));
		}
		stats.get(np.getPlayer().getUniqueId()).setGames(stats.get(np.getPlayer().getUniqueId()).getGames() + 1);
	}
	
	public void registerKill(UUID killer, UUID killed) {
		stats.get(killer).setKills(stats.get(killer).getKills() + 1);
		stats.get(killed).setDeaths(stats.get(killed).getDeaths() + 1);
		stats.get(killer).setCoins(stats.get(killer).getCoins() + 10);
		
		if (Bukkit.getPlayer(killer) != null) {
			Bukkit.getPlayer(killer).sendMessage(NetworkCore.prefixStandard + ChatManager.translateFor("en", NetworkPlayer.getNetworkPlayerByUUID(killer.toString()), "You have gained 10 coins for killing " + Bukkit.getPlayer(killed) != null ? Bukkit.getPlayer(killed).getDisplayName() + ". ":"that player."));
			
		}
		Basepoint killerPoint = new Basepoint();
		killerPoint.currentElo = stats.get(killer).getElo();
		killerPoint.lastElo = stats.get(killer).getLastelo();
		
		Basepoint killedPoint = new Basepoint();
		killedPoint.currentElo = stats.get(killed).getElo();
		killedPoint.lastElo = stats.get(killed).getLastelo();
		
		stats.get(killer).setLastelo(killerPoint.getElo());
		stats.get(killed).setLastelo(killedPoint.getElo());
		
		Eloable.Calculations.applySingle(killerPoint, killedPoint, Outcome.WON);
		Eloable.Calculations.applySingle(killedPoint, killerPoint, Outcome.LOST);
				
		stats.get(killer).setElo(killerPoint.getElo());
		stats.get(killed).setElo(killedPoint.getElo());
		
	}
	
	public void registerBow(UUID shooter, boolean hit) {
		// Use projectile hit event later.
		stats.get(shooter).setArrowsFired(stats.get(shooter).getArrowsFired() + 1);
		if (hit) {
			stats.get(shooter).setArrowsLanded(stats.get(shooter).getArrowsLanded() + 1);
		}
	}
	
	public void endGame(boolean beta, UUID ...winner) {
		for (UUID uuid : winner) {
			stats.get(uuid).setCoins(stats.get(uuid).getCoins() + 100);
			stats.get(uuid).setWins(stats.get(uuid).getWins() + 1);
		}
		
		for (UUID uuid : stats.keySet()) {
			SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketLogLeaderboardStats(GamemodeManager.getInstance().getGamemode().getValue().toLowerCase() + "Stats" + (beta ? "Beta":""), uuid.toString(), stats.get(uuid).toString()));
		}
	}
	
	//https://www.rethinkdb.com/api/java/offsets_of/ for leaderboard notation
	
	public class LeaderboardPlayerStatToken {

		UUID uuid;
		int wins;
		int kills;
		int deaths;
		int games;
		float averageKillsPerGame;
		int coins;
		int elo;
		int lastelo;
		int arrowsFired;
		int arrowsLanded;
		
		public LeaderboardPlayerStatToken(UUID uuid, int wins, int kills, int deaths, int games,
				float averageKillsPerGame, int coins, int elo, int lastelo, int arrowsFired, int arrowsLanded) {
			super();
			this.uuid = uuid;
			this.wins = wins;
			this.kills = kills;
			this.deaths = deaths;
			this.games = games;
			this.averageKillsPerGame = averageKillsPerGame;
			this.coins = coins;
			this.elo = elo;
			this.lastelo = elo;
			this.arrowsFired = arrowsFired;
			this.arrowsLanded = arrowsLanded;
		}
		
		public LeaderboardPlayerStatToken(String json) {
			JSONObject js = new JSONObject(json);
			this.uuid = UUID.fromString(js.getString("id").replace("\\", ""));
			this.wins = js.getInt("wins");
			this.kills = js.getInt("kills");
			this.deaths = js.getInt("deaths");
			this.games = js.getInt("games");
			this.averageKillsPerGame = js.getLong("averageKillsPerGame");
			this.coins = js.getInt("coins");
			this.elo = js.getInt("elo");
			this.lastelo = js.getInt("lastelo");
			this.arrowsFired = js.getInt("arrowsFired");
			this.arrowsLanded = js.getInt("arrowsLanded");
			
		}
		
		
		public UUID getUuid() {
			return uuid;
		}
		public int getWins() {
			return wins;
		}
		public void setWins(int wins) {
			this.wins = wins;
		}
		public int getKills() {
			return kills;
		}
		public void setKills(int kills) {
			this.kills = kills;
			setAverageKillsPerGame((float) this.kills / (float) games);
		}
		public int getDeaths() {
			return deaths;
		}
		public void setDeaths(int deaths) {
			this.deaths = deaths;
		}
		public int getGames() {
			return games;
		}
		public void setGames(int games) {
			this.games = games;
			setAverageKillsPerGame((float) this.kills / (float) games);
		}
		public float getAverageKillsPerGame() {
			return averageKillsPerGame;
		}
		public float getKillDeathRatio() {
			return ((float) kills / (float) deaths);
		}
		public void setAverageKillsPerGame(float averageKillsPerGame) {
			this.averageKillsPerGame = averageKillsPerGame;
		}
		public int getCoins() {
			return coins;
		}
		public void setCoins(int coins) {
			this.coins = coins;
		}
		public int getElo() {
			return elo;
		}
		public void setElo(int elo) {
			this.elo = elo;
		}
		
		public int getLastelo() {
			return lastelo;
		}

		public void setLastelo(int lastelo) {
			this.lastelo = lastelo;
		}

		public int getArrowsFired() {
			return arrowsFired;
		}

		public void setArrowsFired(int arrowsFired) {
			this.arrowsFired = arrowsFired;
		}

		public int getArrowsLanded() {
			return arrowsLanded;
		}

		public void setArrowsLanded(int arrowsLanded) {
			this.arrowsLanded = arrowsLanded;
		}
		
		public double getBowAccuracy() {
			return (((float) arrowsLanded) / ((float) arrowsFired));
		}

		@Override
		public String toString() {
			JSONObject json = new JSONObject();
			json.put("uuid", uuid.toString());
			json.put("wins", wins);
			json.put("kills", kills);
			json.put("deaths", deaths);
			json.put("games", games);
			json.put("averageKillsPerGame", averageKillsPerGame);
			json.put("coins", coins);
			json.put("elo", elo);
			json.put("lastelo", lastelo);
			json.put("arrowsFired", arrowsFired);
			json.put("arrowsLanded", arrowsLanded);
			return json.toString();
		}

	}
	
}
