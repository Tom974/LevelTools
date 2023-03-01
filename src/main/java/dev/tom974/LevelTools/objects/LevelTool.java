package dev.tom974.LevelTools.objects;

import com.cryptomorin.xseries.XEnchantment;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.tom974.LevelTools.Main;
import dev.tom974.LevelTools.util.Util;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class LevelTool {

    private final Configuration configuration = Main.getInstance().getConfig();
    private final NBTItem nbtItem;
    private final String toolType;
    private final Player player;
    //The old lore is the lore that was previously set by the plugin
    //and needs to be cleared before the new lore is added to the item again
    //This method is used to assure that the lore is preserved from the item
    //and not cleared which happened in LevelTools 1.0 and caused many issues
    private final List<String> oldLore;
    private ItemStack item;
    private int xp;
    private int level;

    public LevelTool(String toolType, ItemStack item, Player player) {
        this.nbtItem = new NBTItem(item, true);
        this.item = new ItemStack(item);
        this.toolType = toolType;
        this.xp = nbtItem.getInteger("xp");
        this.level = nbtItem.getInteger("level");
        this.player = player;
        this.oldLore = nbtItem.getStringList("lore");
    }


    public String getToolType() {
        return toolType;
    }

    public int getXp() {
        return xp;
    }

    public int getLevel() {
        return level;
    }

    protected abstract void setCustomLore();


    public NBTItem getNBTItem() {
        return this.nbtItem;
    }

    public void addXP(int amt, Player player) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getPlayerAdapter(Player.class).getUser(player);
        double multi = 1.0;
        for (Node node : user.getNodes()) {
            if (node.getKey().startsWith("leveltools.multiplier.")) {
                double val = Double.valueOf(node.getKey().split("leveltools.multiplier.")[1]);
                if (val > multi) {
                    multi = val;
                }
            }
        }

        Bukkit.getLogger().info("Adding xp:" + amt + " with multiplier:" + multi + " " + Math.round(amt * multi));
        this.xp += Math.round(amt * multi);
    }

    protected void setOldLore(List<String> list) {
        this.oldLore.clear();
        this.oldLore.addAll(list);
    }

    protected void removeOldLore() {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        for (String oldLoreLine : oldLore) {
            lore.removeIf(newLoreLine -> newLoreLine.equals(oldLoreLine));
        }
        meta.setLore(lore);
        this.item.setItemMeta(meta);
    }

    public void setLore(List<String> newLore) {
        this.processPlaceholders(newLore);
        this.removeOldLore();

        ItemMeta meta = item.getItemMeta();

        List<String> l;
        if (meta.getLore() == null) {
            l = new ArrayList<>();
        } else {
            l = meta.getLore();
        }

        l.addAll(newLore);
        meta.setLore(l);

        this.setOldLore(newLore);
        this.item.setItemMeta(meta);
    }

    private void processPlaceholders(List<String> list) {
        //Replace list from the Util class is not used here to minimize for loops
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            s = s.replace("%level%", String.valueOf(this.level));
            s = s.replace("%xp%", String.valueOf(this.xp));
            s = s.replace("%xp_needed%", String.valueOf(this.getXPNeeded()));
            s = s.replace("%progressbar%", Util.getProgressBar(this.xp, this.getXPNeeded()));
            if (this.getXPNeeded() == 0) {
                s = s.replace("%percentage%", Integer.toString(100));
            } else {
                s = s.replace("%xp_needed%", Integer.toString(this.getXPNeeded()));
                int percentage = (this.xp * 100 + (this.getXPNeeded() >> 1)) / this.getXPNeeded();
                s = s.replace("%percentage%", Integer.toString(percentage));
            }
            list.set(i, Util.translateColorCodes(s));
        }
    }


    protected void saveItem() {
        //Check for the next level first
        this.checkForNextLevel();
        //Set item NBT based on recent changes by the checkForNextLevel method
        this.nbtItem.setInteger("xp", this.xp);
        this.nbtItem.setInteger("level", this.level);
        setCustomLore();

        this.nbtItem.mergeCustomNBT(item);
        this.player.getInventory().getItemInMainHand().setItemMeta(item.getItemMeta());

        // get percentage needed for next level
        int xpNeeded = getXPNeeded();
        int percentage = (this.xp * 100 + (xpNeeded >> 1)) / xpNeeded;
        float actual = Float.valueOf(percentage) / 100;
        player.setExp(actual);
    }

    public int getXPNeeded() {
        return getXPNeeded(this.level + 1);
    }

    private int getXPNeeded(int level) {
        int xpneeded = 0;
        if (configuration.contains(toolType + ".levels." + level + ".xp-needed")) {
            xpneeded = configuration.getInt(toolType + ".levels." + level + ".xp-needed");
        } else {
            xpneeded = configuration.getInt(toolType + ".levels." + level + ".damage-needed");
        }
        return xpneeded;
    }

    public void checkForNextLevel() {
        int xpneeded = getXPNeeded();
        if (xp >= xpneeded) {
            int nextLevel = this.level + 1;
            if (level == nextLevel) return;
            if (level > nextLevel) return;
            this.level = nextLevel;
            this.xp = 0; // reset the xp xd
            for (String key : configuration.getConfigurationSection(toolType + ".rewards").getKeys(false)) {
                if (key.startsWith("#")) continue;
                int from = Integer.parseInt(key.split("-")[0]);
                int to = Integer.parseInt(key.split("-")[1]);
                if (level >= from && level < to) {
                    for (String command : configuration.getStringList(toolType + ".rewards" + key)) {
                        String[] cmdsplits = command.split(" ", 2);
                        String prefix = cmdsplits[0];
                        if (prefix.equalsIgnoreCase("[cmd]")) Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), cmdsplits[1].replace("%player%", player.getName()));
                        if (prefix.equalsIgnoreCase("[message]")) player.sendMessage(Util.translateColorCodes(cmdsplits[1].replace("%player%", player.getName())));
                    }
                }
            }
            // You have leveled up! yay!
            player.setLevel(level);
        }
    }
}
