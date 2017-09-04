package com.camadeusa.utility.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Represents a Single-Page Inventory.
 *
 * @author SilentsPC
 */
public class Inventory {

    private int rows;
    private String title;
    private LinkedHashMap<Integer, SlotItem> inventorymap;
    private SlotItem background;
    private boolean isDragDropEnabled;

    /**
     * Creates an Single Page Inventory
     *
     * @param title - The Title of the Inventory
     * @param rows  - The amount of rows
     */
    public Inventory(String title, int rows) {
        this(title, rows, false);
    }

    /**
     * Creates an Single Page Inventory
     *
     * @param title             - The Title of the Inventory
     * @param rows              - The amount of rows
     * @param isDragDropEnabled - whether Drag&Drop is enabled
     */
    public Inventory(String title, int rows, boolean isDragDropEnabled) {
        this.isDragDropEnabled = isDragDropEnabled;
        this.title = title;
        this.rows = rows;
        inventorymap = new LinkedHashMap<>();
    }

    /**
     * Builds the inventory for the given Player.
     *
     * @param owner - The {@link Player} to create the inventory.
     * @return The builded {@link org.bukkit.inventory.Inventory}.
     */
    public org.bukkit.inventory.Inventory build(Player owner) {
        org.bukkit.inventory.Inventory inv = Bukkit.createInventory(owner, 9 * rows, title);
        for (int i = 0; i < 9 * rows; i++) {
            if (this.getBackground() != null)
                inv.setItem(i, this.getBackground().build(1));
            if (this.getSlotItemAt(i) != null)
                inv.setItem(i, this.getSlotItemAt(i).build(1));
        }
        return inv;
    }

    /**
     * Calls the Runnable of the SlotItem which has been clicked on.
     *
     * @param slotposition - The Slotposition
     * @param e            - The Event , which has been triggered through InventoryClickEvent
     */
    public void onSlotClick(int slotposition, InventoryClickEvent e) {
        if (inventorymap.get(slotposition) != null)
            if (inventorymap.get(slotposition).getOnClick() != null) {
                inventorymap.get(slotposition).getOnClick().setInvEvent(e);
                inventorymap.get(slotposition).getOnClick().run();
            }
    }

    public int getSize() {
        return inventorymap.size();
    }

    /**
     * Returns the Slot at the given position.
     *
     * @param slotposition - The Slotposition
     * @return the SlotItem
     */
    public SlotItem getSlotItemAt(int slotposition) {
        return inventorymap.get(slotposition);
    }

    /**
     * Adds the Slotitem to the given position
     *
     * @param slotposition - the Position
     * @param slot         - The Slotname
     */
    public void addSlotItem(int slotposition, SlotItem slot) {
        inventorymap.put(slotposition, slot);
    }

    /**
     * Removes the SlotItem at the given position
     *
     * @param slotposition - The Position
     */
    public void removeSlotItem(int slotposition) {
        inventorymap.remove(slotposition);
    }

    /**
     * Returns the Size of the Inventory
     *
     * @return the Size of the Inventory
     */
    public int getRows() {
        return rows;
    }


    /**
     * Sets the Size of the Inventory
     *
     * @param rows The Size of the Inventory
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * The Title of the Inventory
     *
     * @return The Title of the Inventory
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the inventory.
     *
     * @param title - The new Title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the contents of the Inventory
     *
     * @return a HashMap of the Inventory Contents.
     */
    public HashMap<Integer, SlotItem> getInventorymap() {
        return inventorymap;
    }

    /**
     * Overrides the current Inventory Contents.
     *
     * @param inventorymap - The Inventory Contents to override-
     */
    public void setInventorymap(LinkedHashMap<Integer, SlotItem> inventorymap) {
        this.inventorymap = inventorymap;
    }

    /**
     * Returns the Item, which creates the Background of the Inventory.
     *
     * @return The Background of the Inventory as {@link SlotItem}
     */
    public SlotItem getBackground() {
        return background;
    }

    /**
     * Sets the BackgroundItem which fills the empty spots of the Inventory.
     *
     * @param background - The {@link SlotItem} which fills the empty spots
     */
    public void setBackground(SlotItem background) {
        this.background = background;
    }

    /**
     * Returns whether Drag&Drop is enabled
     *
     * @return Drag&Drop
     */
    public boolean isDragDropEnabled() {
        return isDragDropEnabled;
    }

    /**
     * Sets whether Drag&Drop is enabled
     *
     * @param dragDropEnabled Drag&Drop
     */
    public void setDragDropEnabled(boolean dragDropEnabled) {
        isDragDropEnabled = dragDropEnabled;
    }
}
