package fr.shark_zekrom.Fastpass;

import fr.shark_zekrom.Fastpass.Commands.Commands;
import fr.shark_zekrom.Fastpass.Listener.OnRightClick;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Wrapper;

public class Main extends JavaPlugin {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    private Wrapper wrapper;

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getLogger().info("Plugin enabled !");
        PluginManager pm = getServer().getPluginManager();

        this.getCommand("fastpass").setExecutor(new Commands());
        pm.registerEvents(new OnRightClick(), this);

        getConfig().options().copyDefaults();


        Config.setup();

        Config.get().addDefault("TicketInInventory", "§cYou already have a ticket in your inventory.");
        Config.get().addDefault("TicketIsNotInHand", "§cYou don't have a ticket in your hand.");
        Config.get().addDefault("TeleportationInProgress", "§eTeleportation in progress.");
        Config.get().addDefault("FastpassReceived", "§bFastpass received.");
        Config.get().addDefault("FastpassClosed", "§cFastpass closed.");
        Config.get().addDefault("FastpassReceived", "§6Fastpass received.");
        Config.get().addDefault("FastpassGive", "§6Fastpass give to");



        Config.get().addDefault("Prefix", "§b[FastPass+] ");

        Config.get().options().copyDefaults(true);
        Config.save();

    }









    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Plugin disabled !");
    }
}
