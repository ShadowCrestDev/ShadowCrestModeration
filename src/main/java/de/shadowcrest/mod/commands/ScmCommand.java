package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.command.*;

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

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("shadowcrest.mod.reload")) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
                return true;
            }
            plugin.reloadConfig();
            sender.sendMessage(MessageUtil.msg(plugin, "messages.reload_done"));
            return true;
        }

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
                    MessageUtil.ph(
                            "version", ver,
                            "author", author
                    )
            ));
            return true;
        }


        sender.sendMessage(MessageUtil.msg(plugin, "messages.scm_usage"));
        return true;
    }
}
