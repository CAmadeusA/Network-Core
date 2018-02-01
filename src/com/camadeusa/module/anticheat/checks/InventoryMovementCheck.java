package com.camadeusa.module.anticheat.checks;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import com.camadeusa.module.anticheat.Check;
import com.camadeusa.module.anticheat.CheckType;
import com.camadeusa.utility.MathUtil;

public class InventoryMovementCheck extends Check {

	HashMap<UUID, Double> distList = new HashMap<>();

	
	public InventoryMovementCheck() {
		setCheckType(CheckType.INVENTORYMOVEMENT);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (MathUtil.distance(event.getFrom().getX(), event.getTo().getX(), event.getFrom().getZ(), event.getTo().getZ()) * 10 >= 2.2) {
			event.getPlayer().closeInventory();			
		}
	}
	
}
