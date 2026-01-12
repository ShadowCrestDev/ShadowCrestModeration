package de.shadowcrest.mod.tickets;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TicketContextManager {

    private final Map<UUID, Integer> activeTicket = new ConcurrentHashMap<>();

    public void set(UUID staffUuid, Integer ticketId) {
        if (ticketId == null || ticketId <= 0) {
            activeTicket.remove(staffUuid);
            return;
        }
        activeTicket.put(staffUuid, ticketId);
    }

    public Integer get(UUID staffUuid) {
        return activeTicket.get(staffUuid);
    }

    public void clear(UUID staffUuid) {
        activeTicket.remove(staffUuid);
    }
}
