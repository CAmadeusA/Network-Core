package com.camadeusa.player;

public enum PlayerState {
NORMAL,
SPECTATOR,
GHOST;

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
}
