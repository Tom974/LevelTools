package me.mynqme.LevelTools;

import me.mynqme.LevelTools.objects.Handler;
import me.mynqme.LevelTools.objects.tools.BlockBreakTool;
import me.mynqme.LevelTools.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(Util.translateColorCodes("&aLevelTools " + Main.getInstance().getDescription().getVersion()));
            sender.sendMessage(Util.translateColorCodes("&aAuthor: &7MyNqme"));
            sender.sendMessage(Util.translateColorCodes("&aCommands:"));
            sender.sendMessage(Util.translateColorCodes("&7/leveltools reload &8- &7Reloads the config"));
            sender.sendMessage(Util.translateColorCodes("&7/leveltools addxp <player> <amount> &8- &7Adds XP to a player's tool"));
            sender.sendMessage(Util.translateColorCodes("&7/leveltools addlevel <player> <amount> &8- &7Adds levels to a player's tool"));
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("leveltools.reload")) {
                sender.sendMessage(ChatColor.RED + "No permission");
                return false;
            }
            Main.getInstance().reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Config reloaded");
        } else if (args[0].equalsIgnoreCase("addxp")) {
            if (!sender.hasPermission("leveltools.admin")) {
                sender.sendMessage(ChatColor.RED + "No permission");
                return false;
            } else if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /leveltools addxp <player> <amount>");
                return false;
            } else {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found");
                    return false;
                }

                try {
                    int amount = Integer.parseInt(args[2]);
                    ItemStack item = target.getInventory().getItemInMainHand();
                    if (item == null) {
                        sender.sendMessage(ChatColor.RED + "Player is not holding a tool");
                        return false;
                    }
                    Handler handler = new BlockBreakTool("pickaxe", item, target);
                    handler.addXP(amount, target);
                    handler.saveItem();
                    if (sender instanceof Player) {
                        sender.sendMessage(ChatColor.GREEN + "Added " + amount + " XP to " + target.getName());
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid amount");
                    return false;
                }
            }
        } else if (args[0].equalsIgnoreCase("addlevel")) {
            if (!sender.hasPermission("leveltools.admin")) {
                sender.sendMessage(ChatColor.RED + "No permission");
                return false;
            } else if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /leveltools addxp <player> <amount>");
                return false;
            } else {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found");
                    return false;
                }

                try {
                    int amount = Integer.parseInt(args[2]);
                    ItemStack item = target.getInventory().getItemInMainHand();
                    if (item == null) {
                        sender.sendMessage(ChatColor.RED + "Player is not holding a tool");
                        return false;
                    }
                    Handler handler = new BlockBreakTool("pickaxe", item, target);
                    handler.addLevel(amount, target);
                    handler.saveItem();
                    if (sender instanceof Player) {
                        sender.sendMessage(ChatColor.GREEN + "Added " + amount + " levels to " + target.getName() + "'s tool");
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid amount");
                    return false;
                }
            }
        }

        return false;
    }
}
