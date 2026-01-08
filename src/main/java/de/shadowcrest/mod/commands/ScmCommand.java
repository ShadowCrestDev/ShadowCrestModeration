package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.gui.StaffTicketGui;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ScmCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;

    public ScmCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.scm_usage"));
            return true;
        }

        // /scm reload
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("shadowcrest.mod.reload")) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
                return true;
            }
            plugin.reloadConfig();
            plugin.getLang().reload();
            sender.sendMessage(MessageUtil.msg(plugin, "messages.reload_done"));
            return true;
        }

        // /scm info
        if (args[0].equalsIgnoreCase("info")) {
            if (!sender.hasPermission("shadowcrest.mod.info")) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
                return true;
            }

            String ver = plugin.getDescription().getVersion();
            String author = String.join(", ", plugin.getDescription().getAuthors());

            sender.sendMessage(MessageUtil.format(
                    plugin,
                    "messages.scm_info",
                    MessageUtil.ph("version", ver, "author", author)
            ));
            return true;
        }

        // /scm gui
        if (args[0].equalsIgnoreCase("gui")) {
            if (!sender.hasPermission("shadowcrest.mod.ticket.staff")) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
                return true;
            }

            if (!(sender instanceof Player p)) {
                sender.sendMessage("Only players can use the GUI.");
                return true;
            }

            p.openInventory(StaffTicketGui.build(plugin, 0));
            return true;
        }

        sender.sendMessage(MessageUtil.msg(plugin, "messages.scm_usage"));
        return true;
    }
}
