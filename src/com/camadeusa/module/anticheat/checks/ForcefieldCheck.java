package com.camadeusa.module.anticheat.checks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import com.camadeusa.module.anticheat.Check;
import com.camadeusa.module.anticheat.CheckType;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.utility.subservers.packet.PacketPunishPlayer;
import com.camadeusa.utility.subservers.packet.PacketPunishPlayer.PunishType;

import net.ME1312.SubServers.Client.Bukkit.SubAPI;

public class ForcefieldCheck extends Check {

	public ForcefieldCheck() {
		this.setCheckType(CheckType.FORCEFIELD);
		this.setMaxVL(5);
	}
	
	public static Entity getTarget(final Player player) {
        assert player != null;
        Entity target = null;
        double targetDistanceSquared = 0;
        final double radiusSquared = 1;
        final Vector l = player.getEyeLocation().toVector(), n = player.getLocation().getDirection().normalize();
        final double cos45 = Math.cos(Math.PI / 4);
        for (final LivingEntity other : player.getWorld().getEntitiesByClass(LivingEntity.class)) {
            if (other == player)
                continue;
            if (target == null || targetDistanceSquared > other.getLocation().distanceSquared(player.getLocation())) {
                final Vector t = other.getLocation().add(0, 1, 0).toVector().subtract(l);
                if (n.clone().crossProduct(t).lengthSquared() < radiusSquared && t.normalize().dot(n) >= cos45) {
                    target = other;
                    targetDistanceSquared = target.getLocation().distanceSquared(player.getLocation());
                }
            }
        }
        return target;
    }
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			boolean validHit = false;
			Entity target = getTarget((Player) event.getDamager());
			if (target != null && target.getName().equalsIgnoreCase(event.getEntity().getName())) {
				validHit = true;
			}
			if (!validHit) {
				NetworkPlayer np = NetworkPlayer.getNetworkPlayerByUUID(event.getDamager().getUniqueId().toString());
				if ((np.getViolationLevels().get(getCheckType()) != null ? (np.getViolationLevels().get(getCheckType())):(0)) <= getMaxVL()) {
					incrementVL(NetworkPlayer.getNetworkPlayerByUUID(event.getDamager().getUniqueId().toString()));
				} else {
					vlTriggerAction(np);
					resetVL(np);
				}
				np = null;
			}
		}
	}
	
	public void vlTriggerAction(NetworkPlayer np) {
		SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketPunishPlayer(np.getPlayer().getUniqueId().toString(), PunishType.BAN, Long.MAX_VALUE, "Zeus: Hacking - Forcefield (VL: " + getMaxVL() + ")", "Zues"));
		
	}
	
}
