package de.shadowcrest.mod;

import de.shadowcrest.mod.commands.*;
import de.shadowcrest.mod.data.PlayerDataManager;
import de.shadowcrest.mod.listeners.JoinListener;
import org.bukkit.plugin.java.JavaPlugin;
import de.shadowcrest.mod.data.PlaytimeUtil;



public class ShadowCrestMod extends JavaPlugin {

    private PlayerDataManager dataManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.dataManager = new PlayerDataManager(this);

        getCommand("warn").setExecutor(new WarnCommand(this));
        getCommand("kick").setExecutor(new KickCommand(this));
        getCommand("ban").setExecutor(new BanCommand(this));
        getCommand("tempban").setExecutor(new TempbanCommand(this));
        getCommand("ipban").setExecutor(new IpbanCommand(this));
        getCommand("scm").setExecutor(new ScmCommand(this));
        getCommand("unban").setExecutor(new UnbanCommand(this));
        getCommand("unipban").setExecutor(new UnipbanCommand(this));
        getCommand("clearwarns").setExecutor(new ClearWarnsCommand(this));
        getCommand("warns").setExecutor(new WarnsCommand(this));
        getCommand("ip").setExecutor(new IpCommand(this));
        getCommand("playtime").setExecutor(new PlaytimeCommand(this));








        getServer().getPluginManager().registerEvents(new JoinListener(this), this);

        getLogger().info("ShadowCrestModeration enabled.");
    }

    public PlayerDataManager getDataManager() {
        return dataManager;
    }
}
