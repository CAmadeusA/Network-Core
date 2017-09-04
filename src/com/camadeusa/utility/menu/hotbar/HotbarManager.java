package com.camadeusa.utility.menu.hotbar;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class HotbarManager implements Listener {

    /**
     * Hotbarmap which contains the HotbarItems to handle.
     */
    private static HashMap<String, HotbarItem> hotbarmap = new HashMap<String, HotbarItem>();

    /**
     * Registers a HotbarItem.
     *
     * @param i - The Item to register
     */
    public static void registerHotbarItem(HotbarItem i) {
        hotbarmap.put(i.getTitle(), i);
    }

    /**
     * Returns the HotbarItem with the given Title.
     *
     * @param title - The Title of the HotbarItem.
     * @return The HotbarItem
     */
    public static HotbarItem getHotbarItem(String title) {
        return hotbarmap.get(title);
    }

    /**
     * Updated to 1.9 : Handles now Cooldown.
     */
    @EventHandler
    void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getItem() != null) {
            if (hotbarmap.containsKey(e.getItem().getItemMeta()
                    .getDisplayName())) {
                HotbarItem hotbitem = hotbarmap.get(e.getItem().getItemMeta()
                        .getDisplayName());
                if (hotbitem.getOnClick() != null) {
                    hotbitem.getOnClick().setInteractEvent(e);
                    hotbitem.getOnClick().run();
                }
            }
        }
    }

}
