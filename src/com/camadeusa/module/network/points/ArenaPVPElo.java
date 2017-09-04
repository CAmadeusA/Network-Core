package com.camadeusa.module.network.points;

import com.camadeusa.module.game.Gamemode;

public class ArenaPVPElo extends Basepoint {	
	//Allows for custom elo methods per gamemode, for example, arenapvp
	String tag = Gamemode.ArenaPVP.getValue();
	
	public ArenaPVPElo() {
		
	}
	
	

}
