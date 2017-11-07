package com.camadeusa.module.game.uhcsg.segments;

import com.camadeusa.module.game.OrionSegment;
import com.camadeusa.module.game.uhcsg.UHCSGOrionGame;

public class Lobby extends OrionSegment {

	@Override
	public void activate() {
		UHCSGOrionGame.getInstance().setCurrentSegment(this);
		this.activateModule();
	}
	
	@Override
	public void deactivate() {
		this.deactivateModule();
	}
	
}
