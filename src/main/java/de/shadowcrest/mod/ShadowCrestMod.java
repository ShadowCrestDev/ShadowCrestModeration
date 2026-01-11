package de.shadowcrest.mod;

import de.shadowcrest.mod.commands.*;
import de.shadowcrest.mod.data.PlayerDataManager;
import de.shadowcrest.mod.listeners.JoinListener;
import de.shadowcrest.mod.language.LanguageManager;
import de.shadowcrest.mod.tickets.TicketManager;
import de.shadowcrest.mod.tickets.chat.TicketChatManager;
import de.shadowcrest.mod.tickets.chat.TicketPrivateChatListener;
import de.shadowcrest.mod.tickets.gui.*;
import de.shadowcrest.mod.tickets.listeners.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class ShadowCrestMod extends JavaPlugin {

    private PlayerDataManager dataManager;
    private TicketManager ticketManager;
    private LanguageManager lang;
    private TicketChatManager ticketChatManager;
    private de.shadowcrest.mod.chat.TeamChatManager teamChatManager;
    private de.shadowcrest.mod.mute.MuteManager muteManager;
    private de.shadowcrest.mod.vanish.VanishManager vanishManager;
    private de.shadowcrest.mod.discord.DiscordWebhookService discord;
    private de.shadowcrest.mod.discord.ModerationNotifier modNotifier;



    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Language system
        this.lang = new LanguageManager(this);
        this.lang.init();

        // Ticket Chat Manager (Support-Chat)
        this.ticketChatManager = new TicketChatManager(this);

        getLogger().info("SCM DEBUG: Loaded jar build " +
                getDescription().getVersion() + " / " + System.currentTimeMillis());

        // Tickets
        this.ticketManager = new TicketManager(this);
        this.ticketManager.load();

        // Init PDC keys for Action GUI
        StaffTicketActionsGui.initKeys(this);


        printBanner();

        this.dataManager = new PlayerDataManager(this);

        // Team Chat
        this.teamChatManager = new de.shadowcrest.mod.chat.TeamChatManager(this);
        getServer().getPluginManager().registerEvents(
                new de.shadowcrest.mod.chat.TeamChatListener(this, teamChatManager), this
        );
        register("teamchat", new de.shadowcrest.mod.commands.TeamChatCommand(this, teamChatManager));

        // Mute system
        this.muteManager = new de.shadowcrest.mod.mute.MuteManager(this);
        this.muteManager.load();

        register("mute", new de.shadowcrest.mod.commands.MuteCommand(this));
        register("unmute", new de.shadowcrest.mod.commands.UnmuteCommand(this));

        getServer().getPluginManager().registerEvents(new de.shadowcrest.mod.mute.MuteChatListener(this), this);
// 🔁 Auto-unmute task (every 10 seconds)
        getServer().getScheduler().runTaskTimer(
                this,
                () -> muteManager.tickExpiredMutes(),
                20L * 10, // delay
                20L * 10  // period
        );
// 🔔 Mute ActionBar (every second)
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (var p : getServer().getOnlinePlayers()) {

                var mm = getMuteManager();
                if (mm == null) continue;

                // nur wenn gemutet
                if (!mm.isMuted(p.getUniqueId())) continue;

                var mp = mm.getMute(p.getUniqueId());
                if (mp == null) continue;

                String time = mp.isPermanent()
                        ? getLang().get("messages.mute_time_permanent")
                        : mm.getRemainingText(mp);

                String raw = getLang().get("messages.mute_actionbar", java.util.Map.of(
                        "time", time,
                        "reason", mp.getReason() == null ? "-" : mp.getReason()
                ));

                // ✅ Colors korrekt für Adventure (LanguageManager liefert §-Codes)
                p.sendActionBar(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                        .legacySection()
                        .deserialize(raw));
            }
        }, 20L, 20L);

        getServer().getPluginManager().registerEvents(
                new de.shadowcrest.mod.mute.MuteCommandBlockListener(this),
                this
        );

        // Vanish system
        this.vanishManager = new de.shadowcrest.mod.vanish.VanishManager(this);
        this.vanishManager.load();

        register("vanish", new de.shadowcrest.mod.commands.VanishCommand(this));
        getServer().getPluginManager().registerEvents(new de.shadowcrest.mod.vanish.VanishListener(this), this);
        getServer().getPluginManager().registerEvents(new de.shadowcrest.mod.vanish.VanishPickupListener(this), this);

        getServer().getScheduler().runTaskTimer(this, () -> {
            for (var p : getServer().getOnlinePlayers()) {
                if (getVanishManager().isVanished(p.getUniqueId())) {
                    p.sendActionBar(net.kyori.adventure.text.Component.text(
                            getLang().get("messages.vanish_actionbar")
                    ));
                }
            }
        }, 20L, 20L); // every second


// apply vanish to online players (if plugin reload)
        getServer().getScheduler().runTask(this, () -> {
            for (var p : getServer().getOnlinePlayers()) {
                if (vanishManager.isVanished(p.getUniqueId())) {
                    vanishManager.applyVanishState(p, true);
                }
            }
        });
//Discord
        this.discord = new de.shadowcrest.mod.discord.DiscordWebhookService(this);
        this.modNotifier = new de.shadowcrest.mod.discord.ModerationNotifier(this);



        // Commands
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
        register("t", new TicketReplyCommand(this));
        register("tc", new TicketChatToggleCommand(this));




        // General listeners
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);

        // Ticket GUI + Wizard listeners
        getServer().getPluginManager().registerEvents(new TicketGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerSelectGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new TicketChatListener(this), this); // WIZARD
        getServer().getPluginManager().registerEvents(new TicketQuitListener(this), this);

        // Staff Ticket GUI
        getServer().getPluginManager().registerEvents(new StaffTicketGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new StaffTicketCloseChatListener(this), this);
        getServer().getPluginManager().registerEvents(new OfflinePlayerSelectGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new StaffTicketCloseGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new StaffTicketActionsGuiListener(this), this);

        // ✅ NEU: Support-Privatchat (Toggle-Chat)
        getServer().getPluginManager().registerEvents(new TicketPrivateChatListener(this), this);

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

    public LanguageManager getLang() {
        return lang;
    }

    public TicketChatManager getTicketChatManager() {
        return ticketChatManager;
    }

    public de.shadowcrest.mod.mute.MuteManager getMuteManager() {
        return muteManager;
    }

    public de.shadowcrest.mod.vanish.VanishManager getVanishManager() {
        return vanishManager;
    }

    public de.shadowcrest.mod.discord.DiscordWebhookService getDiscord() {
        return discord;
    }

    public de.shadowcrest.mod.discord.ModerationNotifier getModNotifier() {
        return modNotifier;
    }
}

