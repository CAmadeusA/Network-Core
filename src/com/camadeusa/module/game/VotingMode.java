package com.camadeusa.module.game;

import com.camadeusa.player.PlayerRank;

public enum VotingMode {
	DIRECT,
	PERCENTAGE;
	
	
	public int getVotesByRank(PlayerRank r) {
		switch (r) {
		case Owner:
			return 10;
		case Developer:
			return 10;
		case Manager:
			return 10;
		case Admin:
			return 10;
		case SrMod:
			return 9;
		case Mod: 
			return 8;
		case Helper:
			return 8;
		case Vip:
			return 8;
		case Contributer:
			return 8;
		case Donator7:
			return 8;
		case Donator6: 
			return 7;
		case Donator5: 
			return 6;
		case Emerald:
			return 5;
		case Diamond:
			return 4;
		case Gold:
			return 3;
		case Iron:
			return 2;
		case Player:
			return 1;
		case Banned:
			return 0;
		default: 
			return 1;
		}
	}
}
