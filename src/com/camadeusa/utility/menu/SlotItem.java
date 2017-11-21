package com.camadeusa.utility.menu;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.camadeusa.utility.TextUtil;

public class SlotItem {

    private int data;
    private String title;
    private String lore;
    private Material material;
    private InventoryRunnable onClick;
    private ItemStack premadeItem;
    private int amount = 0;


    /**
     * Represents a SlotItem in an Inventory
     *
     * @param premadeItem - The Material of the SlotItem
     */
    public SlotItem(ItemStack premadeItem) {
        this.data = 0;
        this.title = "";
        this.lore = "";
        this.premadeItem = premadeItem;
    }

    /**
     * Represents a SlotItem in an Inventory
     *
     * @param title    - The Title of the SlotItem
     * @param lore     - The Lore of the SlotItem
     * @param data     - Only used for woolcolor etc.
     * @param material - The Material of the SlotItem
     */
    public SlotItem(String title, String lore, int data, Material material) {
        this.data = data;
        this.title = title;
        this.lore = lore;
        this.material = material;
    }

    public SlotItem(String title, String lore, int data, Material material, int amount) {
    	this.data = data;
    	this.title = title;
    	this.lore = lore;
    	this.material = material;
    	this.amount = amount;
    }

    /**
     * Creates the SlotItem in the Game to display it.
     *
     * @param ammount - The Amount of Items , which the Stack should have
     * @return The ItemStack
     */
    public ItemStack build(int ammount) {
    	if (this.amount == 0) {
    		this.amount = ammount;
    	}
        if (premadeItem == null) {
            ItemStack is = new ItemStack(material, this.amount, (byte) data);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(title);
            im.addItemFlags(ItemFlag.values());
            im.setLore(TextUtil.getSubStrings(lore, 30));
            is.setItemMeta(im);
            premadeItem = is;
            return is;
        } else {
            ItemStack is = premadeItem;
            return is;
        }
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
    public InventoryRunnable getOnClick() {
        return onClick;
    }

    /**
     * Sets the Runnable for the onClick Method
     *
     * @param onClick - The Runnable
     */
    public void setOnClick(InventoryRunnable onClick) {
        this.onClick = onClick;
    }
    
    public ItemStack getItemStack() {
    		return premadeItem;
    }

}
