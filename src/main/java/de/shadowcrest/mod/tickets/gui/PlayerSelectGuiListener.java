package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.TicketSession;
import de.shadowcrest.mod.util.MessageUtil;
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

        // Zurück
        if (e.getCurrentItem().getType() == Material.BARRIER) {
            p.openInventory(TicketGui.build(plugin));
            return;
        }

        // Nur Player-Head klickbar
        if (e.getCurrentItem().getType() != Material.PLAYER_HEAD) return;

        String display = e.getCurrentItem().getItemMeta().getDisplayName();
        if (display == null || display.isBlank()) return;

        String targetName = org.bukkit.ChatColor.stripColor(MessageUtil.color(display)).trim();

        // Session existiert bereits (kommt aus TicketGuiListener) – wir füllen nur target
        TicketSession session = plugin.getTicketManager().getSessions().get(p.getUniqueId());
        if (session == null) {
            session = new TicketSession();
            session.setStep(TicketSession.Step.INFO);
        }

        session.setTargetName(targetName);

        // UUID nicht zwingend nötig hier; ChatListener kann auch ohne UUID arbeiten.
        // Wenn du UUID sauber setzen willst, kann ich dir das als Erweiterung geben.
        session.setTargetUuid(null);

        session.setStep(TicketSession.Step.INFO);
        plugin.getTicketManager().getSessions().put(p.getUniqueId(), session);

        p.closeInventory();
        p.sendMessage(MessageUtil.msg(plugin, "messages.ticket_enter_info"));
    }
}
