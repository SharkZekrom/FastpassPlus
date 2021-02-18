package fr.shark_zekrom.Fastpass.Commands;

import fr.shark_zekrom.Fastpass.Config;
import fr.shark_zekrom.Fastpass.Main;
import org.bukkit.Bukkit;
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
            if (args.length >= 1) {
                if (sender.hasPermission("fastpass.admin")) {
                    if (args[0].equalsIgnoreCase("help")) {
                        player.sendMessage(ChatColor.AQUA + "==========[Fastpass+]==========");
                        player.sendMessage(ChatColor.AQUA + "");
                        player.sendMessage(ChatColor.AQUA + "Commands:");
                        player.sendMessage(ChatColor.AQUA + "");
                        player.sendMessage(ChatColor.AQUA + "/fastpass help §8» §eSee all commands");
                        player.sendMessage(ChatColor.AQUA + "/fastpass create <name> §8» §eCreate a fastpass");
                        player.sendMessage(ChatColor.AQUA + "/fastpass delete <name> §8» §eDelete a fastpass");
                        player.sendMessage(ChatColor.AQUA + "/fastpass setteleportation <name> §8» §eSet the teleportation point of a fastpass");
                        player.sendMessage(ChatColor.AQUA + "/fastpass state <name> <on/off> §8» §eChange the status of an attraction");
                        player.sendMessage(ChatColor.AQUA + "/fastpass give <name> [player] §8» §eGive a fastpass fot you or for a player");

                        player.sendMessage(ChatColor.AQUA + "/fastpass reload §8» §eReload the config");
                    }

                    String key = ".";
                    File file = new File(Main.getInstance().getDataFolder(), "fastpass.yml");
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    ConfigurationSection configsection = config.getConfigurationSection(key);

                    if (args[0].equalsIgnoreCase("create")) {
                        if (config.getString("fastpass." + args[1]) == null) {
                            config.set("fastpass." + args[1], true);
                            config.set("fastpass." + args[1] + ".state", "off");
                            try {
                                config.save(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String prefix = Config.get().getString("Prefix");
                            String prefix2 =  prefix.replaceAll("&", "§");

                            player.sendMessage(prefix2 + ChatColor.RED + "There is already a fastpass to this name");
                        }
                    }
                    if (args[0].equalsIgnoreCase("delete")) {
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
                    }
                    if (args[0].equalsIgnoreCase("setteleportation")) {
                        if (config.getString("fastpass." + args[1]) != null) {

                            config.set("fastpass." + args[1] + ".x", player.getLocation().getX());
                            config.set("fastpass." + args[1] + ".y", player.getLocation().getY());
                            config.set("fastpass." + args[1] + ".z", player.getLocation().getZ());
                            config.set("fastpass." + args[1] + ".world", player.getLocation().getWorld().getName());
                            config.set("fastpass." + args[1] + ".pitch", player.getLocation().getPitch());
                            config.set("fastpass." + args[1] + ".yaw", player.getLocation().getYaw());
                            String prefix = Config.get().getString("Prefix");
                            String prefix2 =  prefix.replaceAll("&", "§");

                            player.sendMessage(prefix2 + ChatColor.YELLOW + "Teleportation point for the fastpass " + args[1] + " create.");

                            try {
                                config.save(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String prefix = Config.get().getString("Prefix");
                            String prefix2 =  prefix.replaceAll("&", "§");

                            player.sendMessage(prefix2 + ChatColor.RED + "There is no fastpass to its name created in one with the command /fastpass create <name>.");
                        }
                    }
                    if (args[0].equalsIgnoreCase("state")) {
                        if (args[2].equalsIgnoreCase("on")) {
                            config.set("fastpass." + args[1] + ".state", "on");
                            String prefix = Config.get().getString("Prefix");
                            String prefix2 =  prefix.replaceAll("&", "§");

                            player.sendMessage(prefix2 + "fastpass" + args[1] + " open.");
                            try {
                                config.save(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (args[2].equalsIgnoreCase("off")) {
                            config.set("fastpass." + args[1] + ".state", "off");
                            String prefix = Config.get().getString("Prefix");
                            String prefix2 =  prefix.replaceAll("&", "§");

                            player.sendMessage(prefix2 + "fastpass" + args[1] + " closed.");
                            try {
                                config.save(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    if (args[0].equalsIgnoreCase("ticket")) {
                        ItemStack ticket = player.getInventory().getItemInMainHand();
                        config.set("fastpass." + args[1] + ".ticket", ticket);
                        String prefix = Config.get().getString("Prefix");
                        String prefix2 =  prefix.replaceAll("&", "§");

                        player.sendMessage(prefix2 + "fastpass" + args[1] + " Create ticket for fastpass " + args[1]);
                        try {
                            config.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (args[0].equalsIgnoreCase("reload")) {
                        Config.reload();
                        player.sendMessage(ChatColor.AQUA + "[Fastpass+]" + ChatColor.YELLOW + " Plugin reloaded.");
                    }
                    if (args[0].equalsIgnoreCase("give")) {
                        if (args.length == 3) {
                            String name = args[1];
                            Player player1 = Bukkit.getPlayer(args[2]);

                            if (player1.isOnline()) {
                                ItemStack ticket = config.getItemStack("fastpass." + name + ".ticket");
                                player1.getInventory().addItem(ticket);

                                String prefix = Config.get().getString("Prefix");
                                String prefix2 =  prefix.replaceAll("&", "§");

                                String message1 = Config.get().getString("FastpassReceived");
                                String message2 = Config.get().getString("FastpassGive");

                                String message3 =  message1.replaceAll("&", "§");
                                String message4 =  message2.replaceAll("&", "§");


                                player1.sendMessage(prefix2 + message4 + " " + player1.getName());
                                player.sendMessage(prefix2 + message3);
                            }
                        }
                        else {
                            String prefix = Config.get().getString("Prefix");
                            String prefix2 =  prefix.replaceAll("&", "§");

                            String message = Config.get().getString("FastpassReceived");
                            String message2 =  message.replaceAll("&", "§");

                            player.sendMessage(prefix2 + message2);

                            String name = args[1];
                            ItemStack ticket = config.getItemStack("fastpass." + name + ".ticket");
                            player.getInventory().addItem(ticket);


                        }
                    }
                }
            }        } return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("create");
            arguments.add("delete");
            arguments.add("state");
            arguments.add("setteleportation");
            arguments.add("ticket");
            arguments.add("give");
            arguments.add("reload");
            arguments.add("help");
            return arguments;
        }

        return null;

    }
}
