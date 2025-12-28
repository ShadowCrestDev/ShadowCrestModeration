package de.shadowcrest.mod;

import de.shadowcrest.mod.commands.*;
import de.shadowcrest.mod.data.PlayerDataManager;
import de.shadowcrest.mod.listeners.JoinListener;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class ShadowCrestMod extends JavaPlugin {

    private PlayerDataManager dataManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

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

        getServer().getPluginManager().registerEvents(new JoinListener(this), this);

        getLogger().info("ShadowCrestModeration enabled.");
    }

    private void register(String cmd, CommandExecutor exec) {
        var c = getCommand(cmd);
        if (c != null) c.setExecutor(exec);
        else getLogger().warning("Command '" + cmd + "' not found in plugin.yml!");
    }

    private void printBanner() {
        String gray = ChatColor.GRAY.toString();
        String blue = ChatColor.BLUE.toString();
        String reset = ChatColor.RESET.toString();

        getLogger().info(gray + "================================");
        getLogger().info(gray + "   ███████╗ ██████╗ ███╗   ███╗");
        getLogger().info(gray + "   ██╔════╝██╔════╝ ████╗ ████║");
        getLogger().info(gray + "   ███████╗██║      ██╔████╔██║");
        getLogger().info(gray + "   ╚════██║██║      ██║╚██╔╝██║");
        getLogger().info(gray + "   ███████║╚██████╗ ██║ ╚═╝ ██║");
        getLogger().info(gray + "   ╚══════╝ ╚═════╝ ╚═╝     ╚═╝");
        getLogger().info(gray + " ");
        getLogger().info(gray + "       " + blue + "SCM" + reset + " Moderation");
        getLogger().info(gray + "       Version: " + reset + getDescription().getVersion());
        getLogger().info(gray + "       Author: " + blue + "ShadowCrest");
        getLogger().info(gray + "================================");
    }

    public PlayerDataManager getDataManager() {
        return dataManager;
    }
}
