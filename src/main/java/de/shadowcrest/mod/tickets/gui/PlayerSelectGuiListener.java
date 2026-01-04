package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.TicketSession;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
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

        String title = plugin.getLang().get("messages.gui.player_select.title");
        if (e.getView().getTitle() == null || !e.getView().getTitle().equals(title)) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        if (e.getCurrentItem().getItemMeta() == null) return;

        // Session holen
        TicketSession session = plugin.getTicketManager().getSessions().get(p.getUniqueId());
        if (session == null) {
            session = new TicketSession();
            session.setStep(TicketSession.Step.TARGET);
        }

        int slot = e.getRawSlot();
        Material type = e.getCurrentItem().getType();

        // Buttons (fixe Slots, sprachneutral)
        if (slot == 49 && type == Material.BARRIER) {
            p.openInventory(TicketGui.build(plugin));
            return;
        }

        if (slot == 48 && type == Material.BOOK) {
            plugin.getTicketManager().getSessions().put(p.getUniqueId(), session);
            p.openInventory(OfflinePlayerSelectGui.build(plugin, p, 1));
            return;
        }

        // Name nicht bekannt (Slot 50)
        if (slot == 50 && type == Material.NAME_TAG) {
            session.setTargetName(plugin.getLang().get("messages.ticket_target_unknown"));
            session.setTargetUuid(null);
            session.setStep(TicketSession.Step.INFO);

            plugin.getTicketManager().getSessions().put(p.getUniqueId(), session);
            p.closeInventory();
            p.sendMessage(MessageUtil.msg(plugin, "messages.ticket_enter_info"));
            return;
        }

        // Nur Player-Head klickbar
        if (type != Material.PLAYER_HEAD) return;

        // Target-Name über Skull Owner holen (robuster als DisplayName)
        Player target = null;
        var meta = e.getCurrentItem().getItemMeta();
        if (meta instanceof org.bukkit.inventory.meta.SkullMeta skullMeta) {
            var owning = skullMeta.getOwningPlayer();
            if (owning != null && owning.getName() != null) {
                target = Bukkit.getPlayerExact(owning.getName());
            }
        }

        if (target == null) {
            p.sendMessage(MessageUtil.msg(plugin, "messages.ticket_player_not_found_in_gui"));
            return;
        }

        // Selbst reporten verhindern
        if (target.getUniqueId().equals(p.getUniqueId())) {
            p.sendMessage(MessageUtil.msg(plugin, "messages.ticket_cannot_report_self"));
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
