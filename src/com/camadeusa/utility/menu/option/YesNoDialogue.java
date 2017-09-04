package com.camadeusa.utility.menu.option;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.camadeusa.utility.menu.Inventory;
import com.camadeusa.utility.menu.InventoryRunnable;
import com.camadeusa.utility.menu.InventoryS;
import com.camadeusa.utility.menu.SlotItem;

public class YesNoDialogue extends Inventory {

    private SlotItem noItem;

    /**
     * Simple Inventory for a Yes No Dialogue.
     *
     * @param pl         - The Plugin where to bind this on.
     * @param name       - The Name of the Inventory.
     * @param accept     - The text of the accept option.
     * @param decline    - The Text of the decline option.
     * @param onClickYes - The On Click Yes Event.
     */
    public YesNoDialogue(JavaPlugin pl, String name, String accept, String decline, InventoryRunnable onClickYes) {
        super(name, 3);
        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 3; j++) {
                int slot = (i * 9) + j;
                SlotItem slotItem = new SlotItem(ChatColor.GREEN + accept, "", 5, Material.STAINED_GLASS_PANE);
                slotItem.setOnClick(onClickYes);
                addSlotItem(slot, slotItem);
            }

            for (int j = 5; j <= 8; j++) {
                int slot = (i * 9) + j;
                noItem = new SlotItem(ChatColor.RED + decline, "", 14, Material.STAINED_GLASS_PANE);
                noItem.setOnClick(new InventoryRunnable() {
                    @Override
                    public void runOnClick(InventoryClickEvent inventoryClickEvent) {
                        inventoryClickEvent.getWhoClicked().closeInventory();
                    }
                });
                addSlotItem(slot, noItem);
            }
        }
        InventoryS.registerInventory(pl, this);
    }

    /**
     * Replaces the default InventoryRunnable of the no Option with the given Runnable.
     *
     * @param runnable - The Runnable to replace with.
     */
    public void onNoClick(InventoryRunnable runnable) {
        noItem.setOnClick(runnable);
        for (int i = 0; i <= 2; i++)
            for (int j = 5; j <= 8; j++) {
                int slot = (i * 9) + j;
                addSlotItem(slot, noItem);
            }
    }
}
