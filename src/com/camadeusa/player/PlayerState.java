package com.camadeusa.player;

import java.util.LinkedList;

public enum PlayerState {
	NORMAL, SPECTATOR, GHOST;

	@Override
	public String toString() {
		switch (this) {
		case NORMAL:
			return "normal";
		case SPECTATOR:
			return "spectator";
		case GHOST:
			return "ghost";
		default:
			return "normal";
		}

	}
	
	public static PlayerState fromString(String s) {
		switch (s) {
		case "normal":
			return NORMAL;
		case "spectator":
			return SPECTATOR;
		case "ghost":
			return GHOST;
			default:
				return NORMAL;
		}
			
	}

	public static boolean canSee(PlayerState ps1, PlayerState ps2) {
		switch (ps1.toString()) {
		case "normal":
			switch (ps2.toString()) {
			case "normal":
				return true;
			case "spectator":
				return false;
			case "ghost":
				return false;
			default:
				return true;
			}
		case "spectator":
			switch (ps2.toString()) {
			case "normal":
				return true;
			case "spectator":
				return true;
			case "ghost":
				return false;
			default:
				return true;
			}
		case "ghost":
			switch (ps2.toString()) {
			case "normal":
				return true;
			case "spectator":
				return true;
			case "ghost":
				return true;
			default:
				return true;
			}

		}
		return false;
	}

	public static LinkedList<PlayerState> valuesordered() {
		LinkedList<PlayerState> psl = new LinkedList<>();
		psl.add(NORMAL);
		psl.add(SPECTATOR);
		psl.add(GHOST);
		return psl;
	}

	public static LinkedList<PlayerState> valuesOrderedForKickOrder() {
		LinkedList<PlayerState> psl = new LinkedList<>();
		psl.add(SPECTATOR);
		psl.add(NORMAL);
		psl.add(GHOST);
		return psl;
	}
	
}
