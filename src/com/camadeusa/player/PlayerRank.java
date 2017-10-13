package com.camadeusa.player;

import java.util.ArrayList;
import java.util.LinkedList;

import org.bukkit.ChatColor;

import com.camadeusa.utility.TextUtil;

public enum PlayerRank {
	Owner,
	Developer,
	Director,
	Admin,
	SrMod,
	Mod,
	Helper,
	Vip,
	Friend,
	Donator7,
	Donator6,
	Donator5,
	Donator4,
	Donator3,
	Donator2,
	Donator1,
	Player,
	Banned;
	
	public static int getValueByRank(PlayerRank r) {
		switch (r) {
		case Owner:
			return Integer.MAX_VALUE;
		case Developer:
			return Integer.MAX_VALUE-1;
		case Director:
			return 500;
		case Admin:
			return 450;
		case SrMod:
			return 400;
		case Mod: 
			return 300;
		case Helper:
			return 200;
		case Vip:
			return 90;
		case Friend:
			return 80;
		case Donator7:
			return 70;
		case Donator6: 
			return 60;
		case Donator5: 
			return 50;
		case Donator4:
			return 40;
		case Donator3:
			return 30;
		case Donator2:
			return 20;
		case Donator1:
			return 10;
		case Player:
			return 1;
		case Banned:
			return -1;
		default: 
			return Integer.MIN_VALUE;
		}
	}
	
	public static String formatNameByRank(ArchrPlayer a) {
		String icon = "•";
		String colorPrefix = ChatColor.RESET + "";
		switch (a.getPlayerRank()) {
		case Owner:
			icon = "\u2654";
			colorPrefix = TextUtil.toBoldRainbow(a.getPlayer().getName());
			break;
		case Developer:
			colorPrefix = TextUtil.toRainbow(a.getPlayer().getName());
			icon = "\u2328";
			break;
		case Director: 
			colorPrefix = ChatColor.DARK_RED + "" + ChatColor.ITALIC;
			icon = "\u2658";
			break;
		case Admin: 
			colorPrefix = ChatColor.DARK_RED + "" + ChatColor.BOLD;
			icon = "\u2658";
			break;
		case SrMod: 
			colorPrefix = ChatColor.RED + "" + ChatColor.BOLD + "" + ChatColor.ITALIC;
			icon = "\u2656";
			break;
		case Mod:
			colorPrefix = ChatColor.RED + "" + ChatColor.ITALIC;
			icon = "\u2657";
			break;
		case Helper:
			colorPrefix = ChatColor.RED + "";
			icon = "\u2659";
			break;
		case Vip:
			colorPrefix = ChatColor.DARK_PURPLE + "";
			icon = "\u2023";
			break;
		case Friend:
			colorPrefix = ChatColor.LIGHT_PURPLE + "";
			icon = "\u221E";
			break;
		case Donator7:
			colorPrefix = ChatColor.AQUA + "";
			icon = "\u2166";
			break;
		case Donator6:
			colorPrefix = ChatColor.AQUA + "";
			icon = "\u2165";
			break;
		case Donator5:
			colorPrefix = ChatColor.AQUA + "";
			icon = "\u2164";
			break;
		case Donator4:
			colorPrefix = ChatColor.GOLD + "";
			icon = "\u2163";
			break;
		case Donator3:
			colorPrefix = ChatColor.GOLD + "";
			icon = "\u2162";
			break;
		case Donator2:
			colorPrefix = ChatColor.GOLD + "";
			icon = "\u2161";
		case Donator1:
			colorPrefix = ChatColor.GOLD + "";
			icon = "\u2160";
		case Player:
			colorPrefix = ChatColor.BLUE + "";
			break;
		case Banned:
			colorPrefix = ChatColor.BLACK + "";
			break;
		default:
			break;
		}
		if (getValueByRank(a.getPlayerRank()) > getValueByRank(PlayerRank.Player)) {
			if (getValueByRank(a.getPlayerRank()) > getValueByRank(PlayerRank.Director)) {
				return (ChatColor.DARK_GRAY + "[" + ChatColor.RESET + icon + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " " + colorPrefix + ChatColor.RESET);			
				
			}
			return (ChatColor.DARK_GRAY + "[" + ChatColor.RESET + icon + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " " + colorPrefix + a.getPlayer().getName() + ChatColor.RESET);			
		} else {
			return (colorPrefix + a.getPlayer().getName() + ChatColor.RESET);
		}
		
	}
	public static String formatNameByRankWOIcon(ArchrPlayer a) {
		String colorPrefix = ChatColor.RESET + "";
		switch (a.getPlayerRank()) {
		case Owner:
			colorPrefix = TextUtil.toBoldRainbow(a.getPlayer().getName());
			break;
		case Developer:
			colorPrefix = TextUtil.toRainbow(a.getPlayer().getName());
			break;
		case Director: 
			colorPrefix = ChatColor.DARK_RED + "" + ChatColor.ITALIC;
			break;
		case Admin: 
			colorPrefix = ChatColor.DARK_RED + "" + ChatColor.BOLD;
			break;
		case SrMod: 
			colorPrefix = ChatColor.RED + "" + ChatColor.BOLD + "" + ChatColor.ITALIC;
			break;
		case Mod:
			colorPrefix = ChatColor.RED + "" + ChatColor.ITALIC;
			break;
		case Helper:
			colorPrefix = ChatColor.RED + "";
			break;
		case Vip:
			colorPrefix = ChatColor.DARK_PURPLE + "";
			break;
		case Friend:
			colorPrefix = ChatColor.LIGHT_PURPLE + "";
			break;
		case Donator7:
			colorPrefix = ChatColor.AQUA + "";
			break;
		case Donator6:
			colorPrefix = ChatColor.AQUA + "";
			break;
		case Donator5:
			colorPrefix = ChatColor.AQUA + "";
			break;
		case Donator4:
			colorPrefix = ChatColor.GOLD + "";
			break;
		case Donator3:
			colorPrefix = ChatColor.GOLD + "";
			break;
		case Donator2:
			colorPrefix = ChatColor.GOLD + "";
		case Donator1:
			colorPrefix = ChatColor.GOLD + "";
		case Player:
			colorPrefix = ChatColor.BLUE + "";
			break;
		case Banned:
			colorPrefix = ChatColor.BLACK + "";
			break;
		default:
			break;
		}
		if (getValueByRank(a.getPlayerRank()) > getValueByRank(PlayerRank.Player)) {
			if (getValueByRank(a.getPlayerRank()) > getValueByRank(PlayerRank.Director)) {
				return (colorPrefix + ChatColor.RESET);			
				
			}
			return (colorPrefix + a.getPlayer().getName() + ChatColor.RESET);			
		} else {
			return (colorPrefix + a.getPlayer().getName() + ChatColor.RESET);
		}
		
	}
	
	public String toString() {
		switch (this) {
		case Owner:
			return "Owner";
		case Developer:
			return "Developer";
		case Director:
			return "Director";
		case Admin:
			return "Admin";
		case SrMod:
			return "SrMod";
		case Mod: 
			return "Mod";
		case Helper:
			return "Helper";
		case Vip:
			return "Vip";
		case Friend:
			return "Friend";
		case Donator7:
			return "Donator7";
		case Donator6: 
			return "Donator6";
		case Donator5: 
			return "Donator5";
		case Donator4:
			return "Donator4";
		case Donator3:
			return "Donator3";
		case Donator2:
			return "Donator2";
		case Donator1:
			return "Donator1";
		case Player:
			return "Player";
		case Banned:
			return "Banned";
		default: 
			return "Player";
		}
	}
	
	public static PlayerRank fromString(String s) {
		switch (s) {
		case "Owner":
			return PlayerRank.Owner;
		case "Developer":
			return PlayerRank.Developer;
		case "Director":
			return PlayerRank.Director;
		case "Admin":
			return PlayerRank.Admin;
		case "SrMod":
			return PlayerRank.SrMod;
		case "Mod":
			return PlayerRank.Mod;
		case "Helper":
			return PlayerRank.Helper;
		case "Donator7":
			return PlayerRank.Donator7;
		case "Donator6":
			return PlayerRank.Donator6;
		case "Donator5":
			return PlayerRank.Donator5;
		case "Donator4":
			return PlayerRank.Donator4;
		case "Donator3":
			return PlayerRank.Donator3;
		case "Donator2":
			return PlayerRank.Donator2;
		case "Donator1":
			return PlayerRank.Donator1;
		case "Player":
			return PlayerRank.Player;
		case "Banned":
			return PlayerRank.Banned;
			default:
				return PlayerRank.Player;
		}
	}
	
	public static boolean canUseCommand(PlayerRank playerrank, String s) {
		if (getCommandsAvailable(playerrank).contains(s.toLowerCase())) {
			return true;
		} else {
			return false;
		}
	}
	
	// Builds command inheritance
	public static ArrayList<String> getCommandsAvailable(PlayerRank playerrank) {
		switch (playerrank) {
		case Banned:
			return new ArrayList<String>();
		case Player:
			ArrayList<String> commandsp = getCommandsAvailable(PlayerRank.Banned);
			commandsp.add("help");
			commandsp.add("?");
			commandsp.add("join");
			commandsp.add("hub");
			commandsp.add("setuppassword");
			commandsp.add("authenticate");
			commandsp.add("setpasswordpromptonlogin");
			commandsp.add("changepassword");
			return commandsp;
		case Donator1:
			ArrayList<String> commandsd1 = getCommandsAvailable(PlayerRank.Player);
			// commands.add("foobar");
			return commandsd1;
		case Donator2:
			ArrayList<String> commandsd2 = getCommandsAvailable(PlayerRank.Donator1);
			// commands.add("foobar");
			return commandsd2;
		case Donator3:
			ArrayList<String> commandsd3 = getCommandsAvailable(PlayerRank.Donator2);
			// commands.add("foobar");
			return commandsd3;
		case Donator4:
			ArrayList<String> commandsd4 = getCommandsAvailable(PlayerRank.Donator3);
			// commands.add("foobar");
			return commandsd4;
		case Donator5:
			ArrayList<String> commandsd5 = getCommandsAvailable(PlayerRank.Donator4);
			// commands.add("foobar");
			return commandsd5;
		case Donator6:
			ArrayList<String> commandsd6 = getCommandsAvailable(PlayerRank.Donator5);
			// commands.add("foobar");
			return commandsd6;
		case Donator7:
			ArrayList<String> commandsd7 = getCommandsAvailable(PlayerRank.Donator6);
			// commands.add("foobar");
			return commandsd7;
		case Helper:
			ArrayList<String> commandsH = getCommandsAvailable(PlayerRank.Donator7);
			commandsH.add("kick");
			commandsH.add("kik");
			commandsH.add("boot");
			commandsH.add("gtfo");
			commandsH.add("bye");
			commandsH.add("slap");
			commandsH.add("lookup");
			commandsH.add("punish");
			commandsH.add("openplayermanagmentmenu");
			return commandsH;
		case Mod:
			ArrayList<String> commandsM = getCommandsAvailable(PlayerRank.Helper);
			commandsM.add("ban");
			commandsM.add("banhammer");
			commandsM.add("mute");
			commandsM.add("gag");
			return commandsM;
		case SrMod:
			ArrayList<String> commandsSM = getCommandsAvailable(PlayerRank.Mod);
			commandsSM.add("pardon");
			return commandsSM;
		case Admin:
			ArrayList<String> commandsA = getCommandsAvailable(PlayerRank.SrMod);
			return commandsA;
		case Director:
			ArrayList<String> commandsD = getCommandsAvailable(PlayerRank.Admin);
			// commands.add("foobar");
			return commandsD;
		case Developer:
			ArrayList<String> commandsDD = getCommandsAvailable(PlayerRank.Director);
			// Junk Minecraft commands and Aliases
			commandsDD.add("about");
			commandsDD.add("version");
			commandsDD.add("msg");
			commandsDD.add("w");
			commandsDD.add("tell");
			commandsDD.add("ocm");
			commandsDD.add("oldcombatmechanics");
			commandsDD.add("pl");
			commandsDD.add("plugins");
			commandsDD.add("ps");
			commandsDD.add("protocolsupport");
			commandsDD.add("reload");
			commandsDD.add("rl");
			commandsDD.add("ver");
			commandsDD.add("version");
			// Bukkit Commands
			commandsDD.add("gamemode");
			commandsDD.add("list");
			commandsDD.add("stop");
			commandsDD.add("time");
			commandsDD.add("toggledownfall");
			commandsDD.add("tp");
			commandsDD.add("teleport");
			commandsDD.add("whitelist");
			// Custom commands
			commandsDD.add("checkdata");
			commandsDD.add("changestate");
			commandsDD.add("setstate");
			commandsDD.add("generaterandomdata");
			commandsDD.add("cmds");
			commandsDD.add("sub");
			return commandsDD;
		case Owner:
			ArrayList<String> commandsO = getCommandsAvailable(PlayerRank.Developer);
			return commandsO;
		default:
			return null;
		}
		
		
	}
	
	public static LinkedList<PlayerRank> valuesordered() {
		LinkedList<PlayerRank> ll = new LinkedList<>();
		ll.add(Player);
		ll.add(Donator1);
		ll.add(Donator2);
		ll.add(Donator3);
		ll.add(Donator4);
		ll.add(Donator5);
		ll.add(Donator6);
		ll.add(Donator7);
		ll.add(Friend);
		ll.add(Vip);
		ll.add(Helper);
		ll.add(Mod);
		ll.add(SrMod);
		ll.add(Admin);
		ll.add(Director);
		ll.add(Developer);
		ll.add(Owner);
		
		return ll;
	}
}
