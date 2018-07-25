package com.camadeusa.utility.fetcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.json.JSONArray;

import com.camadeusa.NetworkCore;
import com.camadeusa.utility.subservers.packet.PacketUpdateDatabaseValue;
import com.rethinkdb.RethinkDB;

import net.ME1312.SubServers.Client.Bukkit.SubAPI;

/*
Uncomment this if you want the helper method for BungeeCord:
import net.md_5.bungee.api.connection.ProxiedPlayer;
*/

/*
Uncomment this if you want the helper method for Bukkit/Spigot:
import org.bukkit.entity.Player;
*/

/**
 * Helper-class for getting UUIDs of players
 */
public class UUIDFetcher {

	/**
	 * @param player
	 *            The player
	 * @return The UUID of the given player
	 */
	// Uncomment this if you want the helper method for BungeeCord:
	/*
	 * public static UUID getUUID(ProxiedPlayer player) { return
	 * getUUID(player.getName()); }
	 */

	/**
	 * @param player
	 *            The player
	 * @return The UUID of the given player
	 */
	// Uncomment this if you want the helper method for Bukkit/Spigot:
	/*
	 * public static UUID getUUID(Player player) { return getUUID(player.getName());
	 * }
	 */

	/**
	 * @param playername
	 *            The name of the player
	 * @return The UUID of the given player
	 */
	public static UUID getUUID(String playername) {
		if (Bukkit.getPlayer(playername) != null) {
			return Bukkit.getPlayer(playername).getUniqueId();
		}
		
		String dataLookup = RethinkDB.r.table("playerdata").filter(RethinkDB.r.hashMap("name", playername)).orderBy(RethinkDB.r.desc("lastLogin")).toJson().run(NetworkCore.getInstance().getCon());
		if (!dataLookup.equals("null")) {
			JSONArray jsonLookup = new JSONArray(dataLookup);
			return UUID.fromString(jsonLookup.getJSONObject(0).getString("id"));
		}
		
		
		String output = callURL("https://api.mojang.com/users/profiles/minecraft/" + playername);

		StringBuilder result = new StringBuilder();

		readData(output, result);

		String u = result.toString();

		String uuid = "";

		for (int i = 0; i <= 31; i++) {
			uuid = uuid + u.charAt(i);
			if (i == 7 || i == 11 || i == 15 || i == 19) {
				uuid = uuid + "-";
			}
		}

		SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(uuid, "name", playername));
		return UUID.fromString(uuid);
	}

	private static void readData(String toRead, StringBuilder result) {
		
		int i = 7;

		while (i < 200) {
			if (!String.valueOf(toRead.charAt(i)).equalsIgnoreCase("\"")) {

				result.append(String.valueOf(toRead.charAt(i)));

			} else {
				break;
			}

			i++;
		}
	}

	private static String callURL(String URL) {
		StringBuilder sb = new StringBuilder();
				URLConnection urlConn = null;
				InputStreamReader in = null;

				try {
					URL url = new URL(URL);
					urlConn = url.openConnection();

					if (urlConn != null)
						urlConn.setReadTimeout(60 * 1000);

					if (urlConn != null && urlConn.getInputStream() != null) {
						in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
						BufferedReader bufferedReader = new BufferedReader(in);

						if (bufferedReader != null) {
							int cp;

							while ((cp = bufferedReader.read()) != -1) {
								sb.append((char) cp);
							}

							bufferedReader.close();
						}
					}

					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

		return sb.toString();
	}
}
