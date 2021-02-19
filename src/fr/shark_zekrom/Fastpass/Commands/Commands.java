package fr.shark_zekrom.Fastpass.Commands;

import fr.shark_zekrom.Fastpass.Config;
import fr.shark_zekrom.Fastpass.Main;
import net.minecraft.server.v1_15_R1.Material;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor , TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("fastpass")) {
            if (args.length > 0) {
                if (sender.hasPermission("fastpass.admin")) {
                    if (args[0].equalsIgnoreCase("help")) {
                        player.sendMessage(ChatColor.AQUA + "§b==========[Fastpass+]==========");
                        player.sendMessage(ChatColor.AQUA + "");
                        player.sendMessage(ChatColor.AQUA + "Commands:");
                        player.sendMessage(ChatColor.AQUA + "");
                        player.sendMessage(ChatColor.AQUA + "/fastpass help §8» §eSee all commands");
                        player.sendMessage(ChatColor.AQUA + "/fastpass create <name> §8» §eCreate a fastpass");
                        player.sendMessage(ChatColor.AQUA + "/fastpass setticket <name> §8» §eSet a ticket for the fastpass");
                        player.sendMessage(ChatColor.AQUA + "/fastpass delete <name> §8» §eDelete a fastpass");
                        player.sendMessage(ChatColor.AQUA + "/fastpass setteleportation <name> §8» §eSet the teleportation point of a fastpass");
                        player.sendMessage(ChatColor.AQUA + "/fastpass state <name> <on/off> §8» §eChange the status of an attraction");
                        player.sendMessage(ChatColor.AQUA + "/fastpass reload §8» §eReload the config");
                    }

                    String key = ".";
                    File file = new File(Main.getInstance().getDataFolder(), "fastpass.yml");
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    ConfigurationSection configsection = config.getConfigurationSection(key);

                    if (args[0].equalsIgnoreCase("create")) {
                        if (args.length > 1) {
                            if (config.getString("fastpass." + args[1]) == null) {

                                config.set("fastpass." + args[1], true);
                                config.set("fastpass." + args[1] + ".state", "off");
                                player.sendMessage(ChatColor.AQUA + "[Fastpass+]" + ChatColor.YELLOW + " Fastpass " + args[1] + " created.");

                                try {
                                    config.save(file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                String prefix = Config.get().getString("Prefix");

                                player.sendMessage(prefix + ChatColor.RED + "There is already a fastpass to this name");
                            }
                        } else {
                            player.sendMessage("§b==========[Fastpass+]==========");
                            player.sendMessage(ChatColor.AQUA + "");
                            player.sendMessage(ChatColor.AQUA + "/fastpass create <name> §8» §eCreate a fastpass");
                        }
                    }
                    if (args[0].equalsIgnoreCase("delete")) {
                        if (args.length > 1) {
                            if (config.getString("fastpass." + args[1]) != null) {
                                config.set("fastpass." + args[1], null);
                                player.sendMessage(ChatColor.AQUA + "[Fastpass+]" + ChatColor.YELLOW + " Fastpass " + args[1] + " deleted.");

                                try {
                                    config.save(file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                player.sendMessage(ChatColor.GOLD + "[FastPass] " + ChatColor.RED + "There is no fastpass to this name");
                            }
                        } else {
                            player.sendMessage("§b==========[Fastpass+]==========");
                            player.sendMessage(ChatColor.AQUA + "");
                            player.sendMessage(ChatColor.AQUA + "/fastpass delete <name> §8» §eDelete a fastpass");
                        }
                    }
                    if (args[0].equalsIgnoreCase("setteleportation")) {
                        if (args.length > 1) {
                            if (config.getString("fastpass." + args[1]) != null) {

                                config.set("fastpass." + args[1] + ".x", player.getLocation().getX());
                                config.set("fastpass." + args[1] + ".y", player.getLocation().getY());
                                config.set("fastpass." + args[1] + ".z", player.getLocation().getZ());
                                config.set("fastpass." + args[1] + ".world", player.getLocation().getWorld().getName());
                                config.set("fastpass." + args[1] + ".pitch", player.getLocation().getPitch());
                                config.set("fastpass." + args[1] + ".yaw", player.getLocation().getYaw());
                                String prefix = Config.get().getString("Prefix");
                                player.sendMessage(prefix + ChatColor.YELLOW + "Teleportation point for the fastpass " + args[1] + " create.");

                                try {
                                    config.save(file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                String prefix = Config.get().getString("Prefix");
                                player.sendMessage(prefix + ChatColor.RED + "There is no fastpass to its name created in one with the command /fastpass create <name>.");
                            }
                        } else {
                            player.sendMessage("§b==========[Fastpass+]==========");
                            player.sendMessage(ChatColor.AQUA + "");
                            player.sendMessage(ChatColor.AQUA + "/fastpass setteleportation <name> §8» §eSet the teleportation point of a fastpass");
                        }
                    }
                    if (args[0].equalsIgnoreCase("state")) {
                        if (args.length > 2) {
                            if (args[2].equalsIgnoreCase("on")) {


                                if (config.getString("fastpass." + args[1]) == null) {
                                    String prefix = Config.get().getString("Prefix");

                                    player.sendMessage(prefix + ChatColor.RED + "There is no fastpass to its name created in one with the command /fastpass create <name>.");

                                } else {
                                    String prefix = Config.get().getString("Prefix");
                                    config.set("fastpass." + args[1] + ".state", "on");
                                    player.sendMessage(prefix + "fastpass " + args[1] + " open.");
                                    try {
                                        config.save(file);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            if (args[2].equalsIgnoreCase("off")) {
                                config.set("fastpass." + args[1] + ".state", "off");
                                String prefix = Config.get().getString("Prefix");
                                player.sendMessage(prefix + "fastpass " + args[1] + " closed.");
                                try {
                                    config.save(file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            player.sendMessage("§b==========[Fastpass+]==========");
                            player.sendMessage(ChatColor.AQUA + "");
                            player.sendMessage(ChatColor.AQUA + "/fastpass state <name> <on/off> §8» §eChange the status of an attraction");
                        }
                    }
                    if (args[0].equalsIgnoreCase("setticket")) {
                        if (args.length > 1) {
                            ItemStack ticket = player.getInventory().getItemInMainHand();
                            config.set("fastpass." + args[1] + ".ticket", ticket);
                            String prefix = Config.get().getString("Prefix");
                            player.sendMessage(prefix + "Ticket fot fastpass " + args[1] + " create.");
                            try {
                                config.save(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            player.sendMessage("§b==========[Fastpass+]==========");
                            player.sendMessage(ChatColor.AQUA + "");
                            player.sendMessage(ChatColor.AQUA + "/fastpass setticket <name> §8» §eSet a ticket for the fastpass");
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    Config.reload();
                    player.sendMessage(ChatColor.AQUA + "[Fastpass+]" + ChatColor.YELLOW + " Plugin reloaded.");
                }
            }
            else {
                player.sendMessage("§b==========[Fastpass+]==========");
                player.sendMessage(ChatColor.AQUA + "");
                player.sendMessage(ChatColor.AQUA + "fastpass help §8» §eSee all commands");
            }

        } return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("create");
            arguments.add("delete");
            arguments.add("state");
            arguments.add("setteleportation");
            arguments.add("setticket");
            arguments.add("reload");
            arguments.add("help");
            return arguments;
        }
        return null;

    }
}
