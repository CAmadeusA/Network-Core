package com.camadeusa.module.anticheat;

import com.camadeusa.module.Module;
import com.camadeusa.player.NetworkPlayer;

public class Check extends Module {
	
	CheckType checkType;
	int maxVL;

	public CheckType getCheckType() {
		return checkType;
	}

	public void setCheckType(CheckType checkType) {
		this.checkType = checkType;
	}

	public int getMaxVL() {
		return maxVL;
	}

	public void setMaxVL(int maxVL) {
		this.maxVL = maxVL;
	}
	
	public void incrementVL(NetworkPlayer np) {
		np.getViolationLevels().put(this.getCheckType(), np.getViolationLevels().get(this.getCheckType()) != null ? (np.getViolationLevels().get(this.getCheckType()) + 1):1);

	}
	
	public void resetVL(NetworkPlayer np) {
		np.getViolationLevels().put(getCheckType(), 0);

	}
	
}
