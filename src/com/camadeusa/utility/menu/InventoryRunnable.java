package com.camadeusa.utility.menu;

import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class InventoryRunnable implements Runnable {

    private InventoryClickEvent event;

    /**
     * Sets the event to use for the Runnable
     *
     * @param e - The InventoyClickEvent
     */
    public void setInvEvent(InventoryClickEvent e) {
        event = e;
    }


    @Override
    public void run() {
        if (event != null)
            runOnClick(event);
    }

    /**
     * Works like run()
     *
     * @param e - The Event
     */
    public abstract void runOnClick(InventoryClickEvent e);

}
