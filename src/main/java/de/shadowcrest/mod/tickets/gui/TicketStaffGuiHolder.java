package de.shadowcrest.mod.tickets.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class TicketStaffGuiHolder implements InventoryHolder {

    private final int ticketId;

    public TicketStaffGuiHolder(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getTicketId() {
        return ticketId;
    }

    @Override
    public Inventory getInventory() {
        return null; // wird extern gebaut
    }
}
