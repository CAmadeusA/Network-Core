package com.camadeusa.module.anticheat;

import org.bukkit.ChatColor;

import com.camadeusa.module.Module;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerRank;

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
		NetworkPlayer.getOnlinePlayers().forEach(npStaff -> {
			if (npStaff.getPlayerRank().getValue() > PlayerRank.Helper.getValue() && np.getViolationLevels().get(getCheckType()) > getMaxVL()/2) {
				npStaff.getPlayer().sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "ZUES" + ChatColor.DARK_GRAY + ">> " + ChatColor.RESET + np.getPlayer().getDisplayName() + " has failed check " + getCheckType().name() + " with VL:(" + np.getViolationLevels().get(getCheckType()) + "/" + getMaxVL() + ")");
			}
		});
	}
	
	public void resetVL(NetworkPlayer np) {
		np.getViolationLevels().put(getCheckType(), 0);
	}
	
}
