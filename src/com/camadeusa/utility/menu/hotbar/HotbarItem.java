package com.camadeusa.utility.menu.hotbar;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.camadeusa.utility.TextUtil;

public class HotbarItem {

    private int data;
    private String title;
    private String lore;
    private Material material;
    private HotbarRunnable onClick;
    private ItemStack item;
    private double cooldown;

    /**
     * Represents a HotbarItem in the PlayerInventory
     *
     * @param title    - The Title of the SlotItem
     * @param lore     - The Lore of the SlotItem
     * @param data     - Only used for woolcolor etc.
     * @param material - The Material of the SlotItem
     */
    public HotbarItem(String title, String lore, int data, Material material) {
        this.data = data;
        this.title = title;
        this.lore = lore;
        this.material = material;
        this.cooldown = 0L;
    }

    /**
     * Creates a Hotbar Item from an already given ItemStack.
     *
     * @param item - The Item.
     */
    public HotbarItem(ItemStack item) {
        this.item = item;
        this.cooldown = 0L;
    }

    /**
     * Creates the SlotItem in the Game to display it.
     *
     * @param ammount - The Amount of Items , which the Stack should have
     * @return The ItemStack
     */
    @SuppressWarnings("deprecation")
    public ItemStack build(int ammount) {
        if (item != null)
            return item;
        ItemStack is = new ItemStack(material, ammount, (short) data);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(title);
        im.setLore(TextUtil.getSubStrings(lore, 30));
        is.setItemMeta(im);
        if (data != 0) {
            MaterialData id = is.getData();
            id.setData((byte) data);
            is.setData(id);
        }
        return is;
    }

    /**
     * Gives the Item to the given player.
     *
     * @param p        - The Player
     * @param position - The position from 0-7
     */
    public void give(Player p, int position) {
        p.getInventory().setItem(position, build(1));
    }


    /**
     * Returns the data of the SlotItem
     */
    public int getData() {
        return data;
    }

    /**
     * Sets the data of an item
     */
    public void setData(int data) {
        this.data = data;
    }

    /**
     * Returns the DisplayName of the Item
     *
     * @return The Title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title for the item
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the Lore of the Item (The Description)
     *
     * @return The Description
     */
    public String getLore() {
        return lore;
    }

    /**
     * Sets the Lore of the Item (The Description)
     */
    public void setLore(String lore) {
        this.lore = lore;
    }

    /**
     * Returns the ItemType of the Item
     *
     * @return material
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Sets the Material of the Item
     */
    public void setMaterial(Material material) {
        this.material = material;
    }

    /**
     * Returns the Runnable which will be called on the onClick method
     *
     * @return The Runnable
     */
    public HotbarRunnable getOnClick() {
        return onClick;
    }

    /**
     * Sets the Runnable for the onClick Method
     *
     * @param onClick - The Runnable
     */
    public void setOnClick(HotbarRunnable onClick) {
        this.onClick = onClick;
    }

    /**
     * Returns the time you need to wait, until you can use this item again..
     *
     * @return The Cooldown of the Item
     */
    public double getCooldown() {
        return cooldown;
    }

    /**
     * Sets the Cooldown of this Item.
     *
     * @param cooldown - The Time you need to wait, until this Item can be used again.
     */
    public void setCooldown(double cooldown) {
        this.cooldown = cooldown;
    }
}
