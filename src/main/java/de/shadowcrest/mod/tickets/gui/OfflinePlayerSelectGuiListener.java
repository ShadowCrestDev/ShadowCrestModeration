package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.TicketSession;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OfflinePlayerSelectGuiListener implements Listener {

    private final ShadowCrestMod plugin;

    public OfflinePlayerSelectGuiListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    private boolean isOfflineGui(String title) {
        if (title == null) return false;

        // Titel-Pattern aus Language, alles vor {page} als Prefix nutzen
        String pattern = plugin.getLang().get("messages.gui.offline_player_select.title");
        int idx = pattern.indexOf("{page}");
        String prefix = idx >= 0 ? pattern.substring(0, idx) : pattern;

        // Titel & Prefix sind bereits farbig
        return title.startsWith(prefix);
    }

    private int parsePage(String title) {
        if (title == null) return 1;

        // letzte Zahl im Titel nehmen (robust für jede Sprache)
        String clean = title.replace("§", "");
        Matcher m = Pattern.compile("(\\d+)\\s*$").matcher(clean);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (Exception ignored) {}
        }
        return 1;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;

        String title = e.getView().getTitle();
        if (!isOfflineGui(title)) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        if (e.getCurrentItem().getItemMeta() == null) return;

        int page = parsePage(title);

        // Session holen
        TicketSession session = plugin.getTicketManager().getSessions().get(p.getUniqueId());
        if (session == null) {
            session = new TicketSession();
            session.setStep(TicketSession.Step.TARGET);
        }

        int slot = e.getRawSlot();
        Material type = e.getCurrentItem().getType();

        // ✅ Zurück zur Online-Auswahl (Slot 49)
        if (slot == 49 && type == Material.BARRIER) {
            plugin.getTicketManager().getSessions().put(p.getUniqueId(), session);
            p.openInventory(PlayerSelectGui.build(plugin, p));
            return;
        }

        // ✅ Refresh (Slot 53)
        if (slot == 53 && type == Material.SUNFLOWER) {
            plugin.getTicketManager().getSessions().put(p.getUniqueId(), session);
            p.openInventory(OfflinePlayerSelectGui.build(plugin, p, page));
            return;
        }

        // ✅ Paging (Slot 48 = prev, Slot 50 = next)
        if (slot == 48 && type == Material.ARROW) {
            plugin.getTicketManager().getSessions().put(p.getUniqueId(), session);
            p.openInventory(OfflinePlayerSelectGui.build(plugin, p, Math.max(1, page - 1)));
            return;
        }

        if (slot == 50 && type == Material.ARROW) {
            plugin.getTicketManager().getSessions().put(p.getUniqueId(), session);
            p.openInventory(OfflinePlayerSelectGui.build(plugin, p, page + 1));
            return;
        }

        // Nur Player-Head klickbar
        if (type != Material.PLAYER_HEAD) return;

        // ✅ Robust: Name über OwningPlayer holen (nicht DisplayName parsen)
        OfflinePlayer target = null;
        var meta = e.getCurrentItem().getItemMeta();
        if (meta instanceof org.bukkit.inventory.meta.SkullMeta skullMeta) {
            target = skullMeta.getOwningPlayer(); // OfflinePlayer
        }

        if (target == null || target.getName() == null || target.getName().isBlank()) {
            return;
        }

        session.setTargetName(target.getName());
        session.setTargetUuid(target.getUniqueId());
        session.setStep(TicketSession.Step.INFO);

        plugin.getTicketManager().getSessions().put(p.getUniqueId(), session);

        p.closeInventory();
        p.sendMessage(MessageUtil.msg(plugin, "messages.ticket_enter_info"));
    }
}
