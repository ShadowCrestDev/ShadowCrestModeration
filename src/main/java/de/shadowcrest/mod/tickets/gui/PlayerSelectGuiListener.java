package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.TicketSession;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PlayerSelectGuiListener implements Listener {

    private final ShadowCrestMod plugin;

    public PlayerSelectGuiListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;

        String title = MessageUtil.color("&8SCM &7Spieler auswählen");
        if (e.getView().getTitle() == null || !e.getView().getTitle().equals(title)) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        if (e.getCurrentItem().getItemMeta() == null) return;

        String rawName = e.getCurrentItem().getItemMeta().getDisplayName();
        String itemName = rawName == null ? "" : ChatColor.stripColor(rawName).trim(); // ✅ sauber

        // Session holen (kommt aus TicketGuiListener)
        TicketSession session = plugin.getTicketManager().getSessions().get(p.getUniqueId());
        if (session == null) {
            session = new TicketSession();
            session.setStep(TicketSession.Step.TARGET);
        }

        // Zurück
        if (e.getCurrentItem().getType() == Material.BARRIER) {
            p.openInventory(TicketGui.build(plugin));
            return;
        }

        // Offline Spieler anzeigen (Button)
        if (itemName.equalsIgnoreCase("Offline Spieler anzeigen")) {
            plugin.getTicketManager().getSessions().put(p.getUniqueId(), session);
            p.openInventory(OfflinePlayerSelectGui.build(plugin, p, 1));
            return;
        }

        // Name nicht bekannt (Button)
        if (itemName.equalsIgnoreCase("Name nicht bekannt")) {
            session.setTargetName("Unbekannt");
            session.setTargetUuid(null);
            session.setStep(TicketSession.Step.INFO);

            plugin.getTicketManager().getSessions().put(p.getUniqueId(), session);
            p.closeInventory();
            p.sendMessage(MessageUtil.msg(plugin, "messages.ticket_enter_info"));
            return;
        }

        // Nur Player-Head klickbar (Online)
        if (e.getCurrentItem().getType() != Material.PLAYER_HEAD) return;

        String display = rawName;
        if (display == null || display.isBlank()) return;

        String targetName = ChatColor.stripColor(display).trim(); // ✅ wichtig

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            p.sendMessage(MessageUtil.color("&cSpieler nicht gefunden (vielleicht offline)."));
            return;
        }

        // ❌ Selbst reporten verhindern
        if (target.getUniqueId().equals(p.getUniqueId())) {
            p.sendMessage(MessageUtil.color("&cDu kannst dich nicht selbst reporten."));
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
