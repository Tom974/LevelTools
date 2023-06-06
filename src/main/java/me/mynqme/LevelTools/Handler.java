package me.mynqme.LevelTools;

import me.mynqme.LevelTools.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Handler {
    private final LevelTools instance;
    private List<String> oldLore = new ArrayList<>();

    public Handler(LevelTools plugin) {
        this.instance = plugin;
    }

    public void handleBlockBreak(Player player, ItemStack item) {
        int level = this.instance.levelMap.getOrDefault(player.getUniqueId(), 0);
        int xp = this.instance.xpMap.getOrDefault(player.getUniqueId(), 0);
        this.oldLore = item.getItemMeta().getLore();

        xp += 1; // always add 1 xp for every block breaks
        int maxlevel = this.instance.config.getInt("pickaxe.totalLevels");
        // check if user can level up
        if (this.checkLevelUp(xp, level)) {
            level += 1;
            xp = 0;

            if (level >= maxlevel) {
                xp = this.getXPNeeded(maxlevel);
                level = maxlevel;
            } else {
                for (String key : this.instance.config.getSection("pickaxe.rewards").keySet()) {
                    if (key.startsWith("#")) continue;
                    int from = Integer.parseInt(key.split("-")[0]);
                    int to = Integer.parseInt(key.split("-")[1]);
                    if (level >= from && level < to) {
                        for (String command : LevelTools.getInstance().config.getStringList("pickaxe.rewards" + key)) {
                            String[] cmdsplits = command.split(" ", 2);
                            String prefix = cmdsplits[0];
                            if (prefix.equalsIgnoreCase("[cmd]")) Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), cmdsplits[1].replace("%player%", player.getName()));
                            if (prefix.equalsIgnoreCase("[message]")) player.sendMessage(Util.translateColorCodes(cmdsplits[1].replace("%player%", player.getName())));
                        }
                    }
                }
                // You have leveled up! yay!
                player.setLevel(level);
                player.setExp(0);
            }
        } else {
            Bukkit.getLogger().info("User cannot level up yet. (XP: " + xp + " | Level: " + level);
        }

        if ((level + 1) >= maxlevel) {
            xp = this.getXPNeeded(maxlevel);
            level = maxlevel - 1;
        }
        int xpNeeded = this.getXPNeeded(level + 1);
        int percentage = (xp * 100 + (xpNeeded >> 1)) / xpNeeded;
        float actual = (float) percentage / 100;
        player.setExp(actual);

        this.instance.levelMap.put(player.getUniqueId(), level);
        this.instance.xpMap.put(player.getUniqueId(), xp);
        this.instance.blockMap.put(player.getUniqueId(), this.instance.blockMap.getOrDefault(player.getUniqueId(), 0) + 1);
        this.updateLore(item, xp, level);
        // apply the item in users hand
        player.getInventory().setItemInMainHand(item);
    }

    private boolean checkLevelUp(int xp, int level) {
        return xp >= this.getXPNeeded(level + 1);
    }

    private void updateLore(ItemStack item, int xp, int level) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        for (String oldLoreLine : oldLore) {
            if (oldLoreLine.contains("Level: ") || oldLoreLine.contains("XP: ") || oldLoreLine.contains("Progress: ") || oldLoreLine.contains("Statistics")) {
                if (oldLoreLine.contains("Statistics")) lore.remove(lore.indexOf(oldLoreLine) - 1);
                lore.remove(oldLoreLine);
            }
        }

        List<String> loreToAdd = this.processPlaceholders(this.instance.config.getStringList("pickaxe.lore"), xp, level);
        lore.addAll(loreToAdd);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private int getXPNeeded(int toLvl) {
        return this.instance.config.getInt("pickaxe.xp-base") * toLvl;
    }

    public void addXP(Player player, int amount) {
        this.instance.xpMap.put(player.getUniqueId(), this.instance.xpMap.getOrDefault(player.getUniqueId(), 0) + amount);
    }

    public void removeXP(Player player, int amount) {
        this.instance.xpMap.put(player.getUniqueId(), this.instance.xpMap.getOrDefault(player.getUniqueId(), 0) - amount);
    }

    public void addLevel(Player player, int amount) {
        this.instance.levelMap.put(player.getUniqueId(), this.instance.levelMap.getOrDefault(player.getUniqueId(), 0) + amount);
    }

    public void removeLevel(Player player, int amount) {
        this.instance.levelMap.put(player.getUniqueId(), this.instance.levelMap.getOrDefault(player.getUniqueId(), 0) - amount);
    }

    private List<String> processPlaceholders(List<String> list, int xp, int level) {
        //Replace list from the Util class is not used here to minimize for loops
        List<String> newList = new ArrayList<>();
        int xpneeded = this.getXPNeeded(level + 1);
        for (String s : list) {
            s = s.replace("%level%", String.valueOf(level + 1));
            s = s.replace("%xp%", String.valueOf(xp));
            s = s.replace("%xp_needed%", String.valueOf(xpneeded));
            s = s.replace("%progressbar%", Util.getProgressBar(xp, xpneeded));
            if (xpneeded == 0) {
                s = s.replace("%percentage%", Integer.toString(100));
            } else {
                s = s.replace("%xp_needed%", Integer.toString(xpneeded));
                int percentage = (xp * 100 + (xpneeded >> 1)) / xpneeded;
                s = s.replace("%percentage%", Integer.toString(percentage));
            }
            Bukkit.getLogger().info("Lore after replacing: " + Util.translateColorCodes(s));
            newList.add(Util.translateColorCodes(s));
        }
        return newList;
    }
}
