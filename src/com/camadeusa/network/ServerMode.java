package com.camadeusa.network;

import org.bukkit.event.Listener;

import com.camadeusa.player.PlayerRank;

public class ServerMode implements Listener {
	private static ServerJoinMode currentMode = ServerJoinMode.PUBLIC;
	
	public static ServerJoinMode getMode() {
		return currentMode;
	}
	
	public static void setMode(ServerJoinMode sjm) {
		currentMode = sjm;
	}
	
	public static boolean canJoin(PlayerRank pr) {
		return currentMode == ServerJoinMode.PUBLIC ? true:(currentMode == ServerJoinMode.DONORS ? (pr.getValue() >= PlayerRank.Iron.getValue() ? true:false):(currentMode == ServerJoinMode.STAFF ? (pr.getValue() >= PlayerRank.Helper.getValue() ? true:false):(currentMode == ServerJoinMode.ADMIN ?(pr.getValue() >= PlayerRank.Admin.getValue() ? true:false):(false))));
		
	}
	public static boolean canJoin(ServerJoinMode currentMode, PlayerRank pr) {
		return currentMode == ServerJoinMode.PUBLIC ? true:(currentMode == ServerJoinMode.DONORS ? (pr.getValue() >= PlayerRank.Iron.getValue() ? true:false):(currentMode == ServerJoinMode.STAFF ? (pr.getValue() >= PlayerRank.Helper.getValue() ? true:false):(currentMode == ServerJoinMode.ADMIN ?(pr.getValue() >= PlayerRank.Admin.getValue() ? true:false):(false))));
		
	}
	
	public static enum ServerJoinMode {
		PUBLIC("public"),
		DONORS("donors"),
		STAFF("staff"),
		ADMIN("admin");
		
		String value;
		
		private ServerJoinMode(String mode) {
			this.value = mode;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ServerJoinMode fromString(String s) {
			return (s.equals(PUBLIC.getValue()) ? PUBLIC:(s.equals(DONORS.getValue()) ? DONORS:(s.equals(STAFF.getValue()) ? STAFF:(s.equals(ADMIN.getValue()) ? ADMIN:null))));
		}
	}
}
