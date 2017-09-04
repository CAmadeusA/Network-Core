package com.camadeusa.utility.menu.hotbar;


import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class HotbarRunnable implements Runnable {

    /**
     * Restrictions when the Runnable is allowed to be called.
     */
    private Action[] triggerable;
    private PlayerInteractEvent interact;

    /**
     * A Runnable, only for HotbarItems.
     * <p>
     * Inherits a method which is called, when the user interacts with the item in the required
     * way.
     */
    public HotbarRunnable(Action... trigger) {
        triggerable = trigger;
        if (trigger == null)
            System.out
                    .println("A HotbarRunnable you created, has no Action Trigger. It wont be usable without.");
    }

    /**
     * Sets the InteractEvent for the Runnable.
     *
     * @param e - The PlayerInteractEvent to set
     */
    public void setInteractEvent(PlayerInteractEvent e) {
        interact = e;
    }

    @Override
    public void run() {
        if (interact != null) {
            for (Action a : triggerable) {
                if (a == interact.getAction()) {
                    onHotbarItemUsed(interact);
                    break;
                }
            }
        }
    }

    /**
     * This method will be called, if a user right clicks or left clicks (which depends on Setting)
     * an Item.
     *
     * @param e - The InteractEvent which has been called.
     */
    public abstract void onHotbarItemUsed(PlayerInteractEvent e);

}
