package de.shadowcrest.mod;

import de.shadowcrest.mod.commands.*;
import de.shadowcrest.mod.data.PlayerDataManager;
import de.shadowcrest.mod.listeners.JoinListener;
import de.shadowcrest.mod.tickets.TicketManager;
import de.shadowcrest.mod.tickets.gui.TicketGuiListener;
import de.shadowcrest.mod.tickets.listeners.TicketChatListener;
import de.shadowcrest.mod.tickets.listeners.TicketQuitListener;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class ShadowCrestMod extends JavaPlugin {

    private PlayerDataManager dataManager;
    private TicketManager ticketManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Tickets
        this.ticketManager = new TicketManager(this);
        this.ticketManager.load();

        printBanner();

        this.dataManager = new PlayerDataManager(this);

        register("warn", new WarnCommand(this));
        register("warns", new WarnsCommand(this));
        register("clearwarns", new ClearWarnsCommand(this));

        register("kick", new KickCommand(this));
        register("ban", new BanCommand(this));
        register("tempban", new TempbanCommand(this));
        register("unban", new UnbanCommand(this));

        register("ipban", new IpbanCommand(this));
        register("unipban", new UnipbanCommand(this));
        register("ip", new IpCommand(this));

        register("playtime", new PlaytimeCommand(this));

        register("scm", new ScmCommand(this));
        register("ticket", new TicketCommand(this));

        getServer().getPluginManager().registerEvents(new JoinListener(this), this);

        // Ticket listeners
        getServer().getPluginManager().registerEvents(new TicketGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new TicketChatListener(this), this);
        getServer().getPluginManager().registerEvents(new TicketQuitListener(this), this);

        getLogger().info("ShadowCrestModeration enabled.");
    }

    private void register(String cmd, CommandExecutor exec) {
        var c = getCommand(cmd);
        if (c != null) c.setExecutor(exec);
        else getLogger().warning("Command '" + cmd + "' not found in plugin.yml!");
    }

    private void printBanner() {
        getLogger().info("================================");
        getLogger().info("   ███████╗ ██████╗ ███╗   ███╗");
        getLogger().info("   ██╔════╝██╔════╝ ████╗ ████║");
        getLogger().info("   ███████╗██║      ██╔████╔██║");
        getLogger().info("   ╚════██║██║      ██║╚██╔╝██║");
        getLogger().info("   ███████║╚██████╗ ██║ ╚═╝ ██║");
        getLogger().info("   ╚══════╝ ╚═════╝ ╚═╝     ╚═╝");
        getLogger().info("");
        getLogger().info("       SCM Moderation");
        getLogger().info("       Version: " + getDescription().getVersion());
        getLogger().info("       Author: ShadowCrest");
        getLogger().info("================================");
    }

    public PlayerDataManager getDataManager() {
        return dataManager;
    }

    public TicketManager getTicketManager() {
        return ticketManager;
    }
}
