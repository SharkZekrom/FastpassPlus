package fr.shark_zekrom.Fastpass.Listener;

import fr.shark_zekrom.Fastpass.Config;
import fr.shark_zekrom.Fastpass.Main;
import fr.shark_zekrom.Fastpass.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.sql.Wrapper;

public class OnRightClick implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        String key = ".";
        File file = new File(Main.getInstance().getDataFolder(), "fastpass.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection configsection = config.getConfigurationSection(key);


        if (event.getClickedBlock() != null) {

            if (event.getClickedBlock().getType() == XMaterial.OAK_SIGN.parseMaterial() ||
                    event.getClickedBlock().getType() == XMaterial.ACACIA_SIGN.parseMaterial() ||
                    event.getClickedBlock().getType() == XMaterial.BIRCH_SIGN.parseMaterial() ||
                    event.getClickedBlock().getType() == XMaterial.DARK_OAK_SIGN.parseMaterial() ||
                    event.getClickedBlock().getType() == XMaterial.SPRUCE_SIGN.parseMaterial() ||
                    event.getClickedBlock().getType() == XMaterial.JUNGLE_SIGN.parseMaterial() ||
                    event.getClickedBlock().getType() == XMaterial.OAK_WALL_SIGN.parseMaterial() ||
                    event.getClickedBlock().getType() == XMaterial.ACACIA_WALL_SIGN.parseMaterial() ||
                    event.getClickedBlock().getType() == XMaterial.BIRCH_WALL_SIGN.parseMaterial() ||
                    event.getClickedBlock().getType() == XMaterial.DARK_OAK_WALL_SIGN.parseMaterial() ||
                    event.getClickedBlock().getType() == XMaterial.SPRUCE_WALL_SIGN.parseMaterial() ||
                    event.getClickedBlock().getType() == XMaterial.CRIMSON_WALL_SIGN.parseMaterial() ||
                    event.getClickedBlock().getType() == XMaterial.CRIMSON_SIGN.parseMaterial() ||
                    event.getClickedBlock().getType() == XMaterial.WARPED_WALL_SIGN.parseMaterial() ||
                    event.getClickedBlock().getType() == XMaterial.WARPED_SIGN.parseMaterial() ||
                    event.getClickedBlock().getType() == XMaterial.JUNGLE_WALL_SIGN.parseMaterial()) {



                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Sign sign = (Sign) event.getClickedBlock().getState();
                    String name = sign.getLine(1);
                    if(sign.getLine(0).contains("[Fastpass]")) {
                        String prefix = Config.get().getString("Prefix");
                        if (sign.getLine(2).contains("take")) {
                            if (config.getString("fastpass." + name + ".state").equalsIgnoreCase("on")) {
                                ItemStack ticket = config.getItemStack("fastpass." + name + ".ticket");
                                if(player.getInventory().contains(ticket)){

                                    String message = Config.get().getString("TicketInInventory");
                                    String message2 =  message.replaceAll("&", "§");

                                    player.sendMessage(prefix + message2);
                                }
                                else {
                                    player.getInventory().addItem(ticket);
                                    String message = Config.get().getString("FastpassReceived");
                                    String message2 =  message.replaceAll("&", "§");
                                    player.sendMessage(prefix + message2);
                                }
                            } else {
                                String message = Config.get().getString("FastpassClosed");
                                String message2 =  message.replaceAll("&", "§");

                                player.sendMessage(prefix + message2);
                            }
                        }
                        if (sign.getLine(2).contains("teleport")) {
                            if (config.getString("fastpass." + name + ".state").equalsIgnoreCase("on")) {
                                ItemStack ticket = config.getItemStack("fastpass." + name + ".ticket");
                                if(player.getInventory().getItemInMainHand().equals(ticket)) {

                                    Double locx = config.getDouble("fastpass." + name + ".x");
                                    Double locy = config.getDouble("fastpass." + name + ".y");
                                    Double locz = config.getDouble("fastpass." + name + ".z");
                                    World locworld = Bukkit.getWorld(String.valueOf(config.get("fastpass." + name + ".world")));


                                    Location location = new Location(locworld, locx, locy, locz);
                                    player.teleport(location);
                                    player.getInventory().remove(ticket);

                                    String message = Config.get().getString("TeleportationInProgress");
                                    String message2 =  message.replaceAll("&", "§");

                                    player.sendMessage(prefix + message2);
                                }
                                else {
                                    String message = Config.get().getString("TicketIsNotInHand");
                                    String message2 =  message.replaceAll("&", "§");

                                    player.sendMessage(prefix + message2);
                                }
                            } else {
                                String message = Config.get().getString("FastpassClosed");
                                String message2 =  message.replaceAll("&", "§");

                                player.sendMessage(prefix + message2);
                            }
                        }
                    }
                }
            }
        }
    }

}
