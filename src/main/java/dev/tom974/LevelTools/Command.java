package dev.tom974.LevelTools;

import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.tom974.LevelTools.objects.Handler;
import dev.tom974.LevelTools.objects.LevelTool;
import dev.tom974.LevelTools.objects.tools.BlockBreakTool;
import dev.tom974.LevelTools.util.Util;
import me.fede1132.plasmaprisoncore.events.BlockBreak;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] args) {
        Player player = (Player) commandSender;

        if (args.length == 0) {
            player.sendMessage(Util.translateColorCodes("&aLevelTools " + Main.getInstance().getDescription().getVersion()));
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("leveltools.reload")) {
                player.sendMessage(ChatColor.RED + "No permission");
                return false;
            }
            Main.getInstance().reloadConfig();
            player.sendMessage(ChatColor.GREEN + "Config reloaded");
        } else if (args[0].equalsIgnoreCase("addxp")) {
            if (!player.hasPermission("leveltools.admin")) {
                player.sendMessage(ChatColor.RED + "No permission");
                return false;
            } else if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "Usage: /leveltools addxp <player> <amount>");
                return false;
            } else {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Player not found");
                    return false;
                }

                try {
                    int amount = Integer.parseInt(args[2]);
                    ItemStack item = target.getInventory().getItemInMainHand();
                    if (item == null) {
                        player.sendMessage(ChatColor.RED + "Player is not holding a tool");
                        return false;
                    }
                    Handler handler = new BlockBreakTool("pickaxe", item, player);
                    handler.addXP(amount, player);
                    handler.saveItem();
                    player.sendMessage(ChatColor.GREEN + "Added " + amount + " XP to " + target.getName());
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Invalid amount");
                    return false;
                }
            }
        }

        return false;
    }
}
