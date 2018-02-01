package com.camadeusa.network;

import org.bukkit.event.Listener;

import com.camadeusa.NetworkCore;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.utility.command.Command;
import com.camadeusa.utility.command.CommandArgs;

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
		VIP("vip"),
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
	
	@Command(name = "setServerMode", usage = "/setservermode")
	public void setServerMode(CommandArgs args) {
		if (args.length() != 1) {
			args.getPlayer().chat("/setServerMode <What Mode? (PUBLIC/DONORS/VIP/STAFF{includes helper}/ADMIN/?{for current mode})>");
		} else {
			switch (args.getArgs(0).toLowerCase()) {
			case "?":
				args.getPlayer().sendMessage(NetworkCore.prefixStandard + "The Current Mode is: " + currentMode.getValue());
				break;
			case "public":
				currentMode = ServerJoinMode.PUBLIC;
				args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Server mode set, it is now " + currentMode.getValue() + "only.");
				break;
			case "donors":
				currentMode = ServerJoinMode.DONORS;
				args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Server mode set, it is now " + currentMode.getValue() + "only.");
				break;
			case "donor":
				currentMode = ServerJoinMode.DONORS;
				args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Server mode set, it is now " + currentMode.getValue() + "only.");
				break;
			case "vip":
				currentMode = ServerJoinMode.VIP;
				args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Server mode set, it is now " + currentMode.getValue() + "only.");
				break;
			case "staff":
				currentMode = ServerJoinMode.STAFF;
				args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Server mode set, it is now " + currentMode.getValue() + "only.");
				break;
			case "admin":
				currentMode = ServerJoinMode.ADMIN;
				args.getPlayer().sendMessage(NetworkCore.prefixStandard + "Server mode set, it is now " + currentMode.getValue() + "only.");
				break;
			default:
				args.getPlayer().sendMessage(NetworkCore.prefixError + "That is not a mode. Please try again.");
			}
		}
	}
}
