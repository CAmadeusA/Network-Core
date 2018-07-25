package com.camadeusa.network;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.JSONObject;

import com.camadeusa.NetworkCore;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.utility.ItemStackBuilderUtil;
import com.camadeusa.utility.fetcher.UUIDFetcher;
import com.camadeusa.utility.menu.Inventory;
import com.camadeusa.utility.menu.InventoryS;
import com.camadeusa.utility.menu.SlotItem;
import com.camadeusa.utility.subservers.packet.PacketDownloadPlayerInfo;

import net.ME1312.SubServers.Client.Bukkit.SubAPI;

public class PlayerManagementMenu {

	Inventory topLevel;
		
	public PlayerManagementMenu(String name, NetworkPlayer opener) {
		topLevel = new Inventory(name + "'s Management Menu", 1);
		InventoryS.registerInventory(NetworkCore.getInstance(), topLevel);
		writePlayerDataToInventory(name, opener);
		
		topLevel.build(opener.getPlayer());
	}
	
	public void writePlayerDataToInventory(String name, NetworkPlayer opener) {
		UUID uuid = UUIDFetcher.getUUID(name);
		JSONObject data;
		if (NetworkPlayer.getNetworkPlayerByUUID(uuid.toString()) != null) {
			data = NetworkPlayer.getNetworkPlayerByUUID(uuid.toString()).getData();
			
			ItemStack head = new ItemStackBuilderUtil().toSkullBuilder().withOwner(name).buildSkull();
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setDisplayName(data.getString("name") + "'s Information");
			SlotItem item = new SlotItem(head);
			item.setLore(ChatColor.DARK_PURPLE + "Rank: " + ChatColor.RESET + data.getString("rank") + "\n"
					+ ChatColor.DARK_PURPLE + "State: " + ChatColor.RESET + data.getString("state") + "\n"
					+ ChatColor.DARK_PURPLE + "Name: " + ChatColor.RESET + (name.equals(data.getString(name)) ? name:(data.getString("name") + ": (" + name + ")")) + "\n"
					+ ChatColor.DARK_PURPLE + "UUID: " + ChatColor.RESET + uuid.toString() + "\n"
					+ ChatColor.DARK_PURPLE + "First Login: " + ChatColor.RESET + (new SimpleDateFormat("yyyy/MM/dd").format(new Date((long) data.getDouble("firstlogin")))) + "\n"
					+ ChatColor.DARK_PURPLE + "Last Login: " + ChatColor.RESET + (new SimpleDateFormat("yyyy/MM/dd").format(new Date((long) data.getDouble("lastLogin")))) + "\n"
					+ ((opener.getPlayerRank().getValue() >= PlayerRank.SrMod.getValue()) ? (ChatColor.DARK_PURPLE + "IP Addr:  " + ChatColor.RESET + data.getString("ipaddress")):(""))
					);
			topLevel.addSlotItem(1, item);
			
			// Do more With K/B/M/FS
			// Pardon from here also
			
			
		} else {
			SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketDownloadPlayerInfo());
			
		}
		
	}
	
}
