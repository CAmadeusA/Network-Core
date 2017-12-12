package com.camadeusa.module.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.UUID;

import org.json.JSONObject;

import com.camadeusa.network.points.Basepoint;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.utility.Eloable;
import com.camadeusa.utility.Eloable.Outcome;
import com.camadeusa.utility.subservers.packet.PacketUpdateDatabaseValue;

import net.ME1312.SubServers.Client.Bukkit.SubAPI;

public class LeaderboardToken {
	LinkedHashMap<UUID, LeaderboardPlayerStatToken> stats = new LinkedHashMap<>();
	
	public void addPlayer(NetworkPlayer np) {
		if (np.getData().has(GamemodeManager.getInstance().getGamemode().getValue().toLowerCase() + "Stats")) {
			stats.put(np.getPlayer().getUniqueId(), new LeaderboardPlayerStatToken(np.getData().getJSONObject(GamemodeManager.getInstance().getGamemode().getValue().toLowerCase() + "Stats").toString()));			
		} else {
			stats.put(np.getPlayer().getUniqueId(), new LeaderboardPlayerStatToken(np.getPlayer().getUniqueId(), 0, 0, 0, 0, 0f, 0, new Basepoint(), 0, 0));
		}
		stats.get(np.getPlayer().getUniqueId()).setGames(stats.get(np.getPlayer().getUniqueId()).getGames() + 1);
	}
	
	public void registerKill(UUID killer, UUID killed) {
		stats.get(killer).setKills(stats.get(killer).getKills() + 1);
		stats.get(killed).setDeaths(stats.get(killed).getDeaths() + 1);
		stats.get(killer).setCoins(stats.get(killer).getCoins() + 10);
		
		Eloable.Calculations.applySingle(stats.get(killer).getElo(), stats.get(killed).getElo(), Outcome.WON);
		Eloable.Calculations.applySingle(stats.get(killed).getElo(), stats.get(killer).getElo(), Outcome.LOST);
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
			SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(GamemodeManager.getInstance().getGamemode().getValue().toLowerCase() + "Stats" + (beta ? "Beta":""), uuid.toString(), "stats", stats.get(uuid).toString()));
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
		Basepoint elo;
		int arrowsFired;
		int arrowsLanded;
		
		public LeaderboardPlayerStatToken(UUID uuid, int wins, int kills, int deaths, int games,
				float averageKillsPerGame, int coins, Basepoint elo, int arrowsFired, int arrowsLanded) {
			super();
			this.uuid = uuid;
			this.wins = wins;
			this.kills = kills;
			this.deaths = deaths;
			this.games = games;
			this.averageKillsPerGame = averageKillsPerGame;
			this.coins = coins;
			this.elo = elo;
			this.arrowsFired = arrowsFired;
			this.arrowsLanded = arrowsLanded;
		}
		
		public LeaderboardPlayerStatToken(String json) {
			JSONObject js = new JSONObject(json);
			this.uuid = UUID.fromString(js.getString("uuid"));
			this.wins = js.getInt("wins");
			this.kills = js.getInt("kills");
			this.deaths = js.getInt("deaths");
			this.games = js.getInt("games");
			this.averageKillsPerGame = js.getLong("averageKillsPerGame");
			this.coins = js.getInt("coins");
			this.elo = Basepoint.fromString(js.getJSONObject("elo").toString());
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
		public Basepoint getElo() {
			return elo;
		}
		public void setElo(Basepoint elo) {
			this.elo = elo;
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
			json.put("elo", Basepoint.toString(elo));
			json.put("arrowsFired", arrowsFired);
			json.put("arrowsLanded", arrowsLanded);
			return json.toString();
		}

	}
	
}
