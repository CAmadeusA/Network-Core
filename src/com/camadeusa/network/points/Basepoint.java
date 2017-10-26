package com.camadeusa.network.points;

import org.json.JSONObject;

import com.camadeusa.utility.Eloable;

public class Basepoint implements Eloable {

	public int lastElo = 0;
	public int currentElo = this.getInitialElo();
	
	@Override
	public int getElo() {
		return currentElo;
	}

	@Override
	public void deltaElo(int elo) {
		lastElo = currentElo;
		currentElo = elo;
		
	}
	
	public static String toString(Basepoint bp) {
		JSONObject jso = new JSONObject();
		jso.put("lastelo", bp.lastElo);
		jso.put("currentelo", bp.currentElo);
		return jso.toString();
	}
	
	public static Basepoint fromString(String s) {
		Basepoint bp = new Basepoint();
		JSONObject jso = new JSONObject(s);
		bp.lastElo = jso.getInt("lastelo");
		bp.currentElo = jso.getInt("currentelo");
		
		return bp;
	}

}
