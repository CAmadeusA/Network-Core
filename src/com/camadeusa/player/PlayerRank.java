package com.camadeusa.player;

import java.util.ArrayList;
import java.util.LinkedList;

import org.bukkit.ChatColor;

import com.camadeusa.module.game.Gamemode;
import com.camadeusa.module.game.GamemodeManager;
import com.camadeusa.utility.TextUtil;

public enum PlayerRank {
	Owner(Integer.MAX_VALUE),
	Pikachu(Integer.MAX_VALUE),
	Developer(Integer.MAX_VALUE - 1),
	Manager(500),
	Admin(450),
	SrMod(400),
	Mod(300),
	Helper(200),
	Vip(90),
	Contributer(80),
	Donator7(70),
	Donator6(60),
	Donator5(50),
	Emerald(40),
	Diamond(30),
	Gold(20),
	Iron(10),
	Player(1),
	//Should never be used. Just an intentional delimiter.
	Banned(-1);
	
	private final int value;
	
	PlayerRank(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static String formatTextByRank(PlayerRank r, String s) {
		String colorPrefix = ChatColor.RESET + "";
		switch (r) {
		case Owner:
			colorPrefix = ChatColor.DARK_RED + "" + ChatColor.BOLD;
			break;
		case Pikachu:
			colorPrefix = ChatColor.DARK_PURPLE + "";
			break;
		case Developer:
			colorPrefix = TextUtil.toRainbow(s);
			break;
		case Manager: 
			colorPrefix = ChatColor.DARK_RED + "" + ChatColor.BOLD;
			break;
		case Admin: 
			colorPrefix = ChatColor.DARK_RED + "" + ChatColor.BOLD;
			break;
		case SrMod: 
			colorPrefix = ChatColor.DARK_RED + "";
			break;
		case Mod:
			colorPrefix = ChatColor.RED + "";
			break;
		case Helper:
			colorPrefix = ChatColor.RED + "" + ChatColor.ITALIC;
			break;
		case Vip:
			colorPrefix = ChatColor.DARK_PURPLE + "";
			break;
		case Contributer:
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
		case Emerald:
			colorPrefix = ChatColor.GREEN + "";
			break;
		case Diamond:
			colorPrefix = ChatColor.AQUA + "";
			break;
		case Gold:
			colorPrefix = ChatColor.GOLD + "";
			break;
		case Iron:
			colorPrefix = ChatColor.GRAY + "";
			break;
		case Player:
			colorPrefix = ChatColor.DARK_GREEN + "";
			break;
		case Banned:
			colorPrefix = ChatColor.BLACK + "";
			break;
		default:
			break;
		}
		return r == PlayerRank.Developer ? (colorPrefix + ChatColor.RESET):(colorPrefix + s + ChatColor.RESET);
	}

	
	public static String formatNameByRank(NetworkPlayer a) {
		String colorPrefix = ChatColor.RESET + "";
		switch (a.getPlayerRank()) {
		case Owner:
			colorPrefix = ChatColor.BOLD + "" + ChatColor.DARK_RED;
			break;
		case Pikachu:
			colorPrefix = ChatColor.DARK_PURPLE + "";
			break;
		case Developer:
			colorPrefix = TextUtil.toRainbow(a.getPlayer().getName());
			break;
		case Manager: 
			colorPrefix = ChatColor.DARK_RED + "" + ChatColor.ITALIC;
			break;
		case Admin: 
			colorPrefix = ChatColor.BOLD + "" + ChatColor.DARK_RED;
			break;
		case SrMod: 
			colorPrefix = ChatColor.DARK_RED + "";
			break;
		case Mod:
			colorPrefix = ChatColor.RED + "";
			break;
		case Helper:
			colorPrefix = ChatColor.RED + "" + ChatColor.ITALIC;
			break;
		case Vip:
			colorPrefix = ChatColor.DARK_PURPLE + "";
			break;
		case Contributer:
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
		case Emerald:
			colorPrefix = ChatColor.GREEN + "";
			break;
		case Diamond:
			colorPrefix = ChatColor.AQUA + "";
			break;
		case Gold:
			colorPrefix = ChatColor.GOLD + "";
			break;
		case Iron:
			colorPrefix = ChatColor.GRAY + "";
			break;
		case Player:
			colorPrefix = ChatColor.DARK_GREEN + "";
			break;
		case Banned:
			colorPrefix = ChatColor.BLACK + "";
			break;
		default:
			colorPrefix = ChatColor.BLACK + "";
			break;
		}
			return (colorPrefix + a.getPlayer().getName() + ChatColor.RESET);			

		
	}
	
	@Override
	public String toString() {
		switch (this) {
		case Owner:
			return "Owner";
		case Pikachu:
			return "Pikachu";
		case Developer:
			return "Developer";
		case Manager:
			return "Manager";
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
		case Contributer:
			return "Contributer";
		case Donator7:
			return "Donator7";
		case Donator6: 
			return "Donator6";
		case Donator5: 
			return "Donator5";
		case Emerald:
			return "Donator4";
		case Diamond:
			return "Donator3";
		case Gold:
			return "Donator2";
		case Iron:
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
		case "Pikachu":
			return PlayerRank.Pikachu;
		case "Developer":
			return PlayerRank.Developer;
		case "Manager":
			return PlayerRank.Manager;
		case "Admin":
			return PlayerRank.Admin;
		case "SrMod":
			return PlayerRank.SrMod;
		case "Mod":
			return PlayerRank.Mod;
		case "Helper":
			return PlayerRank.Helper;
		case "Vip":
			return PlayerRank.Vip;
		case "Contributer":
			return PlayerRank.Contributer;
		case "Donator7":
			return PlayerRank.Donator7;
		case "Donator6":
			return PlayerRank.Donator6;
		case "Donator5":
			return PlayerRank.Donator5;
		case "Donator4":
			return PlayerRank.Emerald;
		case "Donator3":
			return PlayerRank.Diamond;
		case "Donator2":
			return PlayerRank.Gold;
		case "Donator1":
			return PlayerRank.Iron;
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
			commandsp.add("directserver");
			if (GamemodeManager.getInstance().getGamemode() == Gamemode.USG) {
				commandsp.add("vote");
			}
			return commandsp;
		case Iron:
			ArrayList<String> commandsd1 = getCommandsAvailable(PlayerRank.Player);
			// commands.add("foobar");
			return commandsd1;
		case Gold:
			ArrayList<String> commandsd2 = getCommandsAvailable(PlayerRank.Iron);
			// commands.add("foobar");
			return commandsd2;
		case Diamond:
			ArrayList<String> commandsd3 = getCommandsAvailable(PlayerRank.Gold);
			// commands.add("foobar");
			return commandsd3;
		case Emerald:
			ArrayList<String> commandsd4 = getCommandsAvailable(PlayerRank.Diamond);
			// commands.add("foobar");
			return commandsd4;
		case Donator5:
			ArrayList<String> commandsd5 = getCommandsAvailable(PlayerRank.Emerald);
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
			if (GamemodeManager.getInstance().getGamemode() == Gamemode.MAPEDITOR) {
				commandsH.add("loadmap");
				commandsH.add("savemap");
				commandsH.add("setmapname");
				commandsH.add("setmapauthor");
				commandsH.add("setmaplink");
				commandsH.add("setradius");
				commandsH.add("adddeathmatchspawn");
				commandsH.add("addworldspawn");
				commandsH.add("setoworldspawn");
				commandsH.add("setodeathmatchspawn");	
				commandsH.add("setwallpos1");
				commandsH.add("setwallpos2");
				commandsH.add("toggleselectable");
				// World edit commands
				commandsH.add("undo");				commandsH.add("redo");			commandsH.add("wand");
				commandsH.add("toggleeditwand");		commandsH.add("sel");			commandsH.add("desel");
				commandsH.add("pos1");				commandsH.add("pos2");			commandsH.add("hpos1");
				commandsH.add("hpos2");				commandsH.add("chunk");			commandsH.add("expand");
				commandsH.add("contract");			commandsH.add("outset");			commandsH.add("inset");
				commandsH.add("shift");				commandsH.add("size");			commandsH.add("count");
				commandsH.add("distr");				commandsH.add("set");			commandsH.add("replace");
				commandsH.add("overlay");			commandsH.add("walls");			commandsH.add("outline");
				commandsH.add("center");				commandsH.add("smooth");			commandsH.add("deform");
				commandsH.add("hollow");				commandsH.add("regen");			commandsH.add("move");
				commandsH.add("stack");				commandsH.add("naturalize");		commandsH.add("line");
				commandsH.add("curve");				commandsH.add("forest");			commandsH.add("flora");
				commandsH.add("copy");				commandsH.add("cut");			commandsH.add("paste");
				commandsH.add("rotate");				commandsH.add("flip");			commandsH.add("schematic");
				commandsH.add("schem");				commandsH.add("generate");		commandsH.add("generatebiome");
				commandsH.add("hcyl");				commandsH.add("cyl");			commandsH.add("sphere");
				commandsH.add("hsphere");			commandsH.add("pyramid");		commandsH.add("hpyramid");
				commandsH.add("forestgen");			commandsH.add("pumpkins");		commandsH.add("toggleplace");
				commandsH.add("fill");				commandsH.add("fillr");			commandsH.add("drain");
				commandsH.add("fixwater");			commandsH.add("fixlava");		commandsH.add("removeabove");
				commandsH.add("removebelow");		commandsH.add("replacenear");	commandsH.add("removenear");
				commandsH.add("snow");				commandsH.add("thaw");			commandsH.add("ex");
				commandsH.add("butcher");			commandsH.add("remove");			commandsH.add("green");
				commandsH.add("calc");				commandsH.add("unstuck");		commandsH.add("ascend");
				commandsH.add("descend");			commandsH.add("ceil");			commandsH.add("thru");
				commandsH.add("jumpto");				commandsH.add("up");				commandsH.add("fast");				
			}
			return commandsH;
		case Mod:
			ArrayList<String> commandsM = getCommandsAvailable(PlayerRank.Helper);
			commandsM.add("kick");
			commandsM.add("kik");
			commandsM.add("boot");
			commandsM.add("gtfo");
			commandsM.add("bye");
			commandsM.add("slap");
			commandsM.add("lookup");
			commandsM.add("punish");
			commandsM.add("openplayermanagmentmenu");
			commandsM.add("ban");
			commandsM.add("banhammer");
			commandsM.add("mute");
			commandsM.add("gag");
			return commandsM;
		case SrMod:
			ArrayList<String> commandsSM = getCommandsAvailable(PlayerRank.Mod);
			return commandsSM;
		case Admin:
			ArrayList<String> commandsA = getCommandsAvailable(PlayerRank.SrMod);
			if (GamemodeManager.getInstance().getGamemode() == Gamemode.USG) {
				commandsA.add("nextsegment");
				commandsA.add("debug");
			}
			commandsA.add("setservermode");
			commandsA.add("setrank");
			return commandsA;
		case Manager:
			ArrayList<String> commandsD = getCommandsAvailable(PlayerRank.Admin);
			// commands.add("foobar");
			return commandsD;
		case Developer:
			ArrayList<String> commandsDD = getCommandsAvailable(PlayerRank.Manager);
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
		case Pikachu:
			ArrayList<String> commandsPikachu = getCommandsAvailable(PlayerRank.Developer);
			return commandsPikachu;
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
		ll.add(Iron);
		ll.add(Gold);
		ll.add(Diamond);
		ll.add(Emerald);
		ll.add(Donator5);
		ll.add(Donator6);
		ll.add(Donator7);
		ll.add(Contributer);
		ll.add(Vip);
		ll.add(Helper);
		ll.add(Mod);
		ll.add(SrMod);
		ll.add(Admin);
		ll.add(Manager);
		ll.add(Developer);
		ll.add(Pikachu);
		ll.add(Owner);
		
		return ll;
	}
}
