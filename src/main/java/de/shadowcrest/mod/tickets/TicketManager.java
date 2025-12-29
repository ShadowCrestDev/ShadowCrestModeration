package de.shadowcrest.mod.tickets;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TicketManager {

    private final ShadowCrestMod plugin;
    private final Map<Integer, Ticket> tickets = new LinkedHashMap<>();
    private int nextId = 1;

    // Spam + sessions
    private final Map<UUID, Long> lastCreate = new ConcurrentHashMap<>();
    private final Map<UUID, TicketSession> sessions = new ConcurrentHashMap<>();

    public TicketManager(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    public Map<UUID, TicketSession> getSessions() {
        return sessions;
    }

    /** OPEN + CLAIMED Tickets des Creators */
    public int getOpenCount(UUID creator) {
        int c = 0;
        for (Ticket t : tickets.values()) {
            if (t.getCreatorUuid().equals(creator)
                    && (t.getStatus() == TicketStatus.OPEN || t.getStatus() == TicketStatus.CLAIMED)) {
                c++;
            }
        }
        return c;
    }

    public long getCooldownRemainingSeconds(UUID creator) {
        int cd = plugin.getConfig().getInt("tickets.cooldown_seconds", 30);
        long last = lastCreate.getOrDefault(creator, 0L);
        long now = System.currentTimeMillis();
        long diff = now - last;
        long remainingMs = (cd * 1000L) - diff;
        return Math.max(0L, (remainingMs + 999) / 1000);
    }

    public void markCreatedNow(UUID creator) {
        lastCreate.put(creator, System.currentTimeMillis());
    }

    public Ticket createTicket(UUID creatorUuid, String creatorName,
                               UUID targetUuid, String targetName,
                               String category, String info) {

        int id = nextId++;
        Ticket t = new Ticket(id, System.currentTimeMillis(),
                creatorUuid, creatorName,
                targetUuid, targetName,
                category, info);

        tickets.put(id, t);

        markCreatedNow(creatorUuid);
        save();

        String notify = MessageUtil.format(
                plugin,
                "messages.ticket_notify",
                MessageUtil.ph("id", id, "player", creatorName, "category", category)
        );
        MessageUtil.broadcastToStaff("shadowcrest.mod.ticket.notify", notify);

        return t;
    }

    public Ticket getTicket(int id) {
        return tickets.get(id);
    }

    public Ticket getNextOpenTicket() {
        for (Ticket t : tickets.values()) {
            if (t.getStatus() == TicketStatus.OPEN) return t;
        }
        return null;
    }

    /** OPEN + CLAIMED Tickets (Staff Übersicht) */
    public List<Ticket> getOpenTickets() {
        List<Ticket> list = new ArrayList<>();
        for (Ticket t : tickets.values()) {
            if (t.getStatus() == TicketStatus.OPEN || t.getStatus() == TicketStatus.CLAIMED) {
                list.add(t);
            }
        }
        return list;
    }

    public void load() {
        try {
            File f = new File(plugin.getDataFolder(),
                    plugin.getConfig().getString("storage.tickets_file", "tickets.yml"));
            if (!f.exists()) return;

            YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
            nextId = yml.getInt("next_id", 1);

            tickets.clear();
            var sec = yml.getConfigurationSection("tickets");
            if (sec != null) {
                for (String key : sec.getKeys(false)) {
                    int id = Integer.parseInt(key);
                    var s = sec.getConfigurationSection(key);
                    if (s == null) continue;

                    long createdAt = s.getLong("createdAt");
                    UUID creatorUuid = UUID.fromString(s.getString("creatorUuid", UUID.randomUUID().toString()));
                    String creatorName = s.getString("creatorName", "Unknown");

                    String targetUuidStr = s.getString("targetUuid", null);
                    UUID targetUuid = (targetUuidStr == null) ? null : UUID.fromString(targetUuidStr);
                    String targetName = s.getString("targetName", "Unknown");

                    String reason = s.getString("reason", "Unbekannt");
                    String info = s.getString("info", "");

                    Ticket t = new Ticket(id, createdAt, creatorUuid, creatorName, targetUuid, targetName, reason, info);

                    String st = s.getString("status", "OPEN");
                    try { t.setStatus(TicketStatus.valueOf(st)); } catch (Exception ignored) {}

                    tickets.put(id, t);
                }
            }

            plugin.getLogger().info("Loaded " + tickets.size() + " ticket(s).");
        } catch (Exception ex) {
            plugin.getLogger().severe("Ticket load failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void save() {
        try {
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
            File f = new File(plugin.getDataFolder(),
                    plugin.getConfig().getString("storage.tickets_file", "tickets.yml"));

            YamlConfiguration yml = new YamlConfiguration();
            yml.set("next_id", nextId);

            for (Ticket t : tickets.values()) {
                String path = "tickets." + t.getId();
                yml.set(path + ".createdAt", t.getCreatedAt());
                yml.set(path + ".creatorUuid", t.getCreatorUuid().toString());
                yml.set(path + ".creatorName", t.getCreatorName());
                yml.set(path + ".targetUuid", t.getTargetUuid() == null ? null : t.getTargetUuid().toString());
                yml.set(path + ".targetName", t.getTargetName());
                yml.set(path + ".reason", t.getReason());
                yml.set(path + ".info", t.getInfo());
                yml.set(path + ".status", t.getStatus().name());
            }

            yml.save(f);
        } catch (Exception ex) {
            plugin.getLogger().severe("Ticket save failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
