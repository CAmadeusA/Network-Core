package com.camadeusa.utility.menu;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.camadeusa.utility.menu.hotbar.HotbarItem;
import com.camadeusa.utility.menu.hotbar.HotbarManager;

/**
 * InventoryS - API for easy to Handle - Inventory Menus. Created by TheSilentHorizon. (c) 2015 - If
 * you want to use this API , please note me as creator of the API.
 *
 * @author TheSilentHorizon
 */
public class InventoryS {

    /**
     * Returns true, if at least one {@link Inventory} is registered. Else false;
     */
    private static boolean isInvEnabled = false;

    /**
     * Returns true, if at least one {@link HotbarItem} is registered. Else falase;
     */
    private static boolean isHBIEnabled = false;

    /**
     * Registers an {@link Inventory} to get viewed by the {@link InventoryManager}.
     *
     * @param inv - The {@link Inventory} to register.
     * @param pl  - The {@link JavaPlugin} Instance
     */
    public static void registerInventory(JavaPlugin pl, Inventory inv) {
        if (!isInvEnabled) {
            pl.getServer().getPluginManager().registerEvents(new InventoryManager(), pl);
            isInvEnabled = true;
        }
        InventoryManager.registerInventory(inv);
    }

    /**
     * Registers an {@link HotbarItem} to get viewed by the {@link HotbarManager}.
     *
     * @param pl   - The {@link JavaPlugin} Instance
     * @param item - The {@link HotbarItem} to register.
     */

    public static void registerHotbarItem(JavaPlugin pl, HotbarItem item) {
        if (!isHBIEnabled) {
            pl.getServer().getPluginManager().registerEvents(new HotbarManager(), pl);
            isHBIEnabled = true;
        }
        HotbarManager.registerHotbarItem(item);
    }

    /**
     * Builds the requested {@link Inventory}, and opens it. (If it exists. If not, an error will be
     * printed.)
     *
     * @param p     - The {@link Player} to handle the event.
     * @param title - The Title to search for the {@link Inventory}.
     * @return The  {@link org.bukkit.inventory.Inventory} which has been openend.
     */
    public static org.bukkit.inventory.Inventory openInventory(Player p, String title) {
        Inventory iv = InventoryManager.getInventory(title);
        if (iv != null) {
            org.bukkit.inventory.Inventory invent = iv.build(p);
            p.openInventory(invent);
            return invent;
        }
        return null;
    }


}
