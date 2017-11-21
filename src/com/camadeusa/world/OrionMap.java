package com.camadeusa.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.JSONArray;
import org.json.JSONObject;

import com.camadeusa.module.game.Gamemode;

public class OrionMap {
	private String mapName;
	private String mapAuthor;
	private String mapLink;
	private LinkedList<SoftLocation> spawns = new LinkedList<>();
	private LinkedList<SoftLocation> deathmatchSpawns = new LinkedList<>();
	private SoftLocation worldSpawn;
	private SoftLocation deathmatchSpawn;
	private Gamemode gamemode;
	private int radius;
	private boolean selectable = true;
	private ArrayList<SoftLocation> wall = new ArrayList<>();
	
	private HashMap<String, String> dataCache = new HashMap<>();
	
	public OrionMap() {}

	public OrionMap(String mapName, String mapAuthor, String mapLink, LinkedList<SoftLocation> spawns, LinkedList<SoftLocation> deathmatchSpawns, SoftLocation worldSpawn,
			SoftLocation deathmatchSpawn, Gamemode gm, int radius, boolean selectable, ArrayList<SoftLocation> wall) {
		this.mapName = mapName;
		this.mapAuthor = mapAuthor;
		this.mapLink = mapLink;
		this.spawns = spawns;
		this.deathmatchSpawns = deathmatchSpawns;
		this.worldSpawn = worldSpawn;
		this.deathmatchSpawn = deathmatchSpawn;
		this.gamemode = gm;
		this.radius = radius;
		this.selectable = selectable;
		this.wall = wall;
	}

	public OrionMap(String json) {
		JSONObject omJson = new JSONObject(json);
		
		this.worldSpawn = new SoftLocation(omJson.getJSONObject("worldSpawn").toString());
		this.deathmatchSpawn = new SoftLocation(omJson.getJSONObject("deathmatchSpawn").toString());
		this.gamemode = Gamemode.valueof(omJson.getString("gamemode"));
		this.radius = omJson.getInt("radius");
		
		LinkedList<SoftLocation> spwns = new LinkedList<>();
		LinkedList<SoftLocation> dmspwns = new LinkedList<>();
		ArrayList<SoftLocation> wallLocs = new ArrayList<>();
		
		omJson.getJSONArray("spawns").forEach(s -> {
			spwns.add(new SoftLocation((String) s));
		});
		
		omJson.getJSONArray("deathmatchSpawns").forEach(s -> {
			dmspwns.add(new SoftLocation((String) s));
		});
		
		omJson.getJSONArray("wall").forEach(s -> {
			wallLocs.add(new SoftLocation((String) s));
		});
		
		this.spawns = spwns;
		this.deathmatchSpawns = dmspwns;
		this.wall = wallLocs;
		
		this.mapName = omJson.getString("mapName");
		this.mapAuthor = omJson.getString("mapAuthor");
		this.mapLink = omJson.getString("mapLink");
		this.selectable = omJson.getBoolean("selectable");
	}
	
	public String toJSONString() {
		JSONObject map = new JSONObject();
		
		JSONArray spwns = new JSONArray();
		JSONArray dmspwns = new JSONArray();
		JSONArray wallocs = new JSONArray();
		
		spawns.forEach(sl -> {
			spwns.put(sl);
		});
		
		deathmatchSpawns.forEach(dms -> {
			dmspwns.put(dms);
		});
		
		wall.forEach(b -> {
			wallocs.put(b);
		});
		
		map.put("spawns", spwns);
		map.put("deathmatchSpawns", dmspwns);
		
		map.put("worldSpawn", new JSONObject(worldSpawn.toString()));
		map.put("deathmatchSpawn", new JSONObject(deathmatchSpawn.toString()));
		
		map.put("gamemode", gamemode.getValue());
		map.put("radius", radius);
		
		map.put("mapName", mapName);
		map.put("mapAuthor", mapAuthor);
		map.put("mapLink", mapLink);
		map.put("selectable", selectable);
		map.put("wall", wallocs);
		
		return map.toString();
	}
	
	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public String getMapAuthor() {
		return mapAuthor;
	}

	public void setMapAuthor(String mapAuthor) {
		this.mapAuthor = mapAuthor;
	}

	public String getMapLink() {
		return mapLink;
	}

	public void setMapLink(String mapLink) {
		this.mapLink = mapLink;
	}

	public LinkedList<SoftLocation> getSpawns() {
		return spawns;
	}
	
	public void addSpawn(SoftLocation sl) {
		spawns.add(sl);
	}

	public void setSpawns(LinkedList<SoftLocation> spawns) {
		this.spawns = spawns;
	}
	
	public void clearSpawns() {
		spawns = new LinkedList<SoftLocation>();
	}

	public LinkedList<SoftLocation> getDeathmatchSpawns() {
		return deathmatchSpawns;
	}
	
	public void addDMSpawn(SoftLocation sl) {
		deathmatchSpawns.add(sl);
	}

	public void setDeathmatchSpawns(LinkedList<SoftLocation> deathmatchSpawns) {
		this.deathmatchSpawns = deathmatchSpawns;
	}
	
	public void clearDeathmatchSpawns() {
		deathmatchSpawns = new LinkedList<SoftLocation>();
	}

	public SoftLocation getWorldSpawn() {
		return worldSpawn;
	}

	public void setWorldSpawn(SoftLocation worldSpawn) {
		this.worldSpawn = worldSpawn;
	}

	public SoftLocation getDeathmatchSpawn() {
		return deathmatchSpawn;
	}

	public void setDeathmatchSpawn(SoftLocation deathmatchSpawn) {
		this.deathmatchSpawn = deathmatchSpawn;
	}

	public Gamemode getGamemode() {
		return gamemode;
	}

	public void setGamemode(Gamemode gamemode) {
		this.gamemode = gamemode;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	public void addToCache(String key, String value) {
		dataCache.put(key, value);
	}
	
	public void removeFromCache(String key) {
		dataCache.remove(key);
	}
	
	public String getCachedValue(String key) {
		return dataCache.get(key);
	}
	
	public boolean hasCached(String key) {
		return dataCache.containsKey(key);
	}
	
	public boolean isSelectable() {
		return selectable;
	}
	
	public void toggleSelectable() {
		this.selectable = !this.selectable;
	}

	public ArrayList<SoftLocation> getWall() {
		return wall;
	}
	
	public void addToWall(SoftLocation loc) {
		wall.add(loc);
	}
	
	public void clearWall() {
		wall = new ArrayList<SoftLocation>();
	}

	public static class SoftLocation {
		private String worldName;
		private double x;
		private double y;
		private double z;
		private double yaw = 0;
		public SoftLocation(String worldName, double d, double e, double f, float yaw) {
			this.worldName = worldName;
			this.x = d;
			this.y = e;
			this.z = f;
			this.yaw = yaw;
		}
		public SoftLocation(String worldName, double d, double e, double f) {
			this.worldName = worldName;
			this.x = d;
			this.y = e;
			this.z = f;
		}
		
		public SoftLocation(Location loc) {
			this.worldName = loc.getWorld().getName();
			this.x = loc.getX();
			this.y = loc.getY();
			this.z = loc.getZ();
			this.yaw = loc.getYaw();
		}

		public SoftLocation(String jsonString) {
			JSONObject json = new JSONObject(jsonString);
			this.x = (double) json.getDouble("x");
			this.y = (double) json.getDouble("y");
			this.z = (double) json.getDouble("z");
			this.yaw = (double) json.getDouble("yaw");
			
			worldName = json.getString("worldName");
		}
		
		@Override 
		public String toString() {
			JSONObject sl = new JSONObject();
			sl.put("worldName", worldName);
			sl.put("x", x);
			sl.put("y", y);
			sl.put("z", z);
			sl.put("yaw", yaw);
			return sl.toString();
		}
		
		public boolean equals(SoftLocation loc) {
			if (worldName.equals(loc.getWorldName())) {
				if (x == loc.getX()) {
					if (y == loc.getY()) {
						if (z == loc.getZ()) {
							if (yaw == loc.getYaw()) {
								return true;
							}
						}
					}
				}
			}
			return false;
		}

		public boolean equalsSoft(SoftLocation loc) {
			if (worldName.equals(loc.getWorldName())) {
				if (Math.floor(x) == Math.floor(loc.getX())) {
					if (Math.floor(y) == Math.floor(loc.getY())) {
						if (Math.floor(z) == Math.floor(loc.getZ())) {
							return true;
						}
					}
				}
			}
			return false;
		}
		
		public String getWorldName() {
			return worldName;
		}
		
		public void setWorldName(String worldName) {
			this.worldName = worldName;
		}
		
		public double getX() {
			return x;
		}
		
		public void setX(double x) {
			this.x = x;
		}
		
		public double getY() {
			return y;
		}
		
		public void setY(double y) {
			this.y = y;
		}
		
		public double getZ() {
			return z;
		}
		
		public void setZ(double z) {
			this.z = z;
		}
		
		public double getYaw() {
			return yaw;
		}
		
		public void setYaw(double yaw) {
			this.yaw = yaw;
		}
		
		public Location toLocation() {
			return new Location(Bukkit.getWorld(worldName), x, y, z, 0, (float) yaw);
		}
	}
}
