package com.camadeusa.utility.menu;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;

public class InventoryManager implements Listener {

    /**
     * If true, onInventoryInteract will be disabled, else not.
     */
    private static boolean onInteract = true;

    /**
     * The Cache for the Inventories.
     */
    private static HashMap<String, Inventory> inventories = new HashMap<String, Inventory>();

    /**
     * Registers a new Inventory
     *
     * @param inv - The Inventory to register
     */
    public static void registerInventory(Inventory inv) {
        inventories.put(inv.getTitle(), inv);
    }

    /**
     * Helper Method to register Inventorys with a custom String.
     *
     * @param inv   - The Inventory
     * @param title - The Id
     */
    public static void registerInventoryAndTitle(Inventory inv, String title) {
        inventories.put(title, inv);
    }

    /**
     * Unregisters an Inventory.
     *
     * @param inv - The Inventory, which should get unregistered.
     */
    public static void unregisterInventory(Inventory inv) {
        inventories.remove(inv);
    }

    /**
     * If true, InventoryInteractEvent will be always cancelled, else not.
     *
     * @return onInteract
     */
    public static boolean isOnInteract() {
        return onInteract;
    }

    /**
     * Sets the OnInteractEvent permanently disabled (true) , or active (false).
     */
    public static void setOnInteract(boolean onInteract) {
        InventoryManager.onInteract = onInteract;
    }

    /**
     * Returns the Inventory with the given title.
     *
     * @param title - The Title of the Inventory
     * @return The Inventory
     */
    public static Inventory getInventory(String title) {
        return inventories.get(title);
    }

    @EventHandler
    void onInvClick(InventoryClickEvent e) {
        if (inventories.containsKey(e.getInventory().getTitle())) {
            Inventory inv = inventories.get(e.getInventory().getTitle());
            if (e.getSlot() < inv.getRows() * 9 && !inv.isDragDropEnabled()) {
                inv.onSlotClick(e.getSlot(), e);
                e.setCancelled(true);
            }

        } else if (inventories.containsKey(e.getInventory().getTitle() + "_"
                + e.getWhoClicked().getUniqueId().toString())) {
            Inventory inv = inventories.get(e.getInventory().getTitle() + "_"
                    + e.getWhoClicked().getUniqueId().toString());
            if (e.getSlot() < inv.getRows() * 9 && !inv.isDragDropEnabled()) {
                inv.onSlotClick(e.getSlot(), e);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    void onInvDragDrop(InventoryInteractEvent e) {
        if (inventories.containsKey(e.getInventory().getTitle())) {
            Inventory inv = inventories.get(e.getInventory().getTitle());
            if (!inv.isDragDropEnabled())
                e.setCancelled(true);
        } else if (inventories.containsKey(e.getInventory().getTitle() + "_"
                + e.getWhoClicked().getUniqueId().toString())) {
            Inventory inv = inventories.get(e.getInventory().getTitle() + "_"
                    + e.getWhoClicked().getUniqueId().toString());
            if (!inv.isDragDropEnabled())
                e.setCancelled(true);

        }
    }

}
