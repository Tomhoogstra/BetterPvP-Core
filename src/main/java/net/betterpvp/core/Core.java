package net.betterpvp.core;


import net.betterpvp.core.client.commands.SpawnCommand;
import net.betterpvp.core.client.listeners.ConnectionListener;
import net.betterpvp.core.command.CommandCenter;
import net.betterpvp.core.command.CommandManager;
import net.betterpvp.core.configs.ConfigManager;
import net.betterpvp.core.database.Connect;
import net.betterpvp.core.database.QueryFactory;
import net.betterpvp.core.donation.DonationManager;
import net.betterpvp.core.donation.menu.DonationMenuListener;
import net.betterpvp.core.framework.CoreLoadedEvent;
import net.betterpvp.core.framework.Options;
import net.betterpvp.core.framework.Updater;
import net.betterpvp.core.interfaces.MenuManager;
import net.betterpvp.core.networking.NetworkReceiver;
import net.betterpvp.core.networking.commands.HubCommand;
import net.betterpvp.core.networking.discord.DiscordCommandListener;
import net.betterpvp.core.proxy.ProxyDetector;
import net.betterpvp.core.punish.PunishManager;
import net.betterpvp.core.punish.listeners.GriefListener;
import net.betterpvp.core.settings.SettingsListener;
import net.betterpvp.core.utility.recharge.RechargeManager;
import net.betterpvp.core.utility.restoration.BlockRestore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Core extends JavaPlugin {

    private ConfigManager config;
    private static Options options;
    private Updater updater;
    private NetworkReceiver networkReceiver;
    private boolean hasStarted;

    @Override
    public void onEnable() {
        config = new ConfigManager(this);
        options = new Options(this);
        updater = new Updater("Updater");
        networkReceiver = new NetworkReceiver(this);

        new Connect(this);
        new QueryFactory(this);

        QueryFactory.loadRepositories("net.betterpvp.core", this);
        CommandManager.registerCommands("net.betterpvp.core", this);
        DonationManager.registerDonations("net.betterpvp.core", this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        new PunishManager(this);
        new BlockRestore(this);
        new SettingsListener(this);

        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    updater.run();
                } catch (Exception e) {

                    System.out.println("Update Event threw an error!");

                }

            }

        }.runTaskTimer(this, 0, 1);

        new BukkitRunnable() {
            @Override
            public void run() {
                RechargeManager.getInstance().handleCooldowns();
                PunishManager.handlePunishments();

            }
        }.runTaskTimerAsynchronously(this, 0L, 2L);

        registerListeners();
        loadCommands();

        new BukkitRunnable(){
            public void run(){
                Bukkit.getPluginManager().callEvent(new CoreLoadedEvent(Core.this));
            }
        }.runTaskLater(this, 20L);


    }

    @Override
    public void onDisable() {
        networkReceiver.stop();

    }

    public ConfigManager getConfigManager() {
        return config;
    }

    public boolean hasStarted() {
        // TODO Auto-generated method stub
        return hasStarted;
    }

    public void setStarted(boolean started){
        this.hasStarted = started;
    }

    private void registerListeners(){
        new ConnectionListener(this);
        new CommandCenter((this));
        new PunishManager(this);
        new GriefListener(this);
        new MenuManager(this);
        new DonationMenuListener(this);
        new DiscordCommandListener(this);
    }

    private void loadCommands(){
      //  new SpawnCommand();
        CommandManager.addCommand(new HubCommand(this));
    }

    public static Options getOptions() {
        return options;
    }

}
