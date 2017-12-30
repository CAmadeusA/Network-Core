package com.camadeusa.module.anticheat.checks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.anticheat.Check;
import com.camadeusa.module.anticheat.CheckType;
import com.camadeusa.utility.subservers.packet.PacketPunishPlayer;
import com.camadeusa.utility.subservers.packet.PacketPunishPlayer.PunishType;

import net.ME1312.SubServers.Client.Bukkit.SubAPI;

public class VapeCheck extends Check implements PluginMessageListener {

	public VapeCheck() {
		this.setCheckType(CheckType.VAPE);
		NetworkCore.getInstance().getServer().getMessenger().registerIncomingPluginChannel(NetworkCore.getInstance(), "LOLIMAHCKER", this);
		this.setMaxVL(1);
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.getPlayer().sendMessage("§8 §8 §1 §3 §3 §7 §8 ");
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketPunishPlayer(player.getUniqueId().toString(), PunishType.BAN, Long.MAX_VALUE, "Zeus: Hacking - Vape Detected (VL: " + getMaxVL() + ")", "Zues"));
		
	}
	
}
