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

public class OfflinePlayerSelectGuiListener implements Listener {

    private final ShadowCrestMod plugin;

    public OfflinePlayerSelectGuiListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    private boolean isOfflineGui(String title) {
        String base = MessageUtil.color("&8SCM &7Offline Spieler");
        return title != null && title.startsWith(base);
    }

    private int parsePage(String title) {
        try {
            String clean = title.replace("§", "");
            int idx = clean.lastIndexOf("Seite ");
            if (idx == -1) return 1;
            String num = clean.substring(idx + "Seite ".length()).trim();
            return Integer.parseInt(num);
        } catch (Exception ignored) {
            return 1;
        }
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
        String itemName = e.getCurrentItem().getItemMeta().getDisplayName();
        itemName = itemName == null ? "" : MessageUtil.color(itemName);

        // Session holen
        TicketSession session = plugin.getTicketManager().getSessions().get(p.getUniqueId());
        if (session == null) {
            session = new TicketSession();
            session.setStep(TicketSession.Step.TARGET);
        }

        // Zurück zur Online-Auswahl
        if (e.getCurrentItem().getType() == Material.BARRIER) {
            plugin.getTicketManager().getSessions().put(p.getUniqueId(), session);
            p.openInventory(PlayerSelectGui.build(plugin, p));
            return;
        }

        // Refresh
        if (e.getCurrentItem().getType() == Material.SUNFLOWER || itemName.contains("Refresh")) {
            plugin.getTicketManager().getSessions().put(p.getUniqueId(), session);
            p.openInventory(OfflinePlayerSelectGui.build(plugin, p, page));
            return;
        }

        // Paging
        if (e.getCurrentItem().getType() == Material.ARROW && itemName.contains("Zurück")) {
            plugin.getTicketManager().getSessions().put(p.getUniqueId(), session);
            p.openInventory(OfflinePlayerSelectGui.build(plugin, p, Math.max(1, page - 1)));
            return;
        }

        if (e.getCurrentItem().getType() == Material.ARROW && itemName.contains("Weiter")) {
            plugin.getTicketManager().getSessions().put(p.getUniqueId(), session);
            p.openInventory(OfflinePlayerSelectGui.build(plugin, p, page + 1));
            return;
        }

        // Nur Player-Head klickbar
        if (e.getCurrentItem().getType() != Material.PLAYER_HEAD) return;

        String display = e.getCurrentItem().getItemMeta().getDisplayName();
        if (display == null || display.isBlank()) return;

        String targetName = MessageUtil.color(display).replace("§", "").trim();

        // UUID setzen (offline möglich)
        OfflinePlayer off = Bukkit.getOfflinePlayer(targetName);

        session.setTargetName(targetName);
        session.setTargetUuid(off != null ? off.getUniqueId() : null);
        session.setStep(TicketSession.Step.INFO);

        plugin.getTicketManager().getSessions().put(p.getUniqueId(), session);

        p.closeInventory();
        p.sendMessage(MessageUtil.msg(plugin, "messages.ticket_enter_info"));
    }
}
