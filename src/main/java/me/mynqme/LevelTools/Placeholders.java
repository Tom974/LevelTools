package me.mynqme.LevelTools;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion {

    @Override
    public @NotNull String getAuthor() {
        return "MyNqme";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "leveltools";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer offlineplayer, @NotNull String param) {
        Player player = Bukkit.getPlayer(offlineplayer.getUniqueId());
        if (player == null) return null;
        if (!player.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_PICKAXE)) return null;
        if (param.equalsIgnoreCase("level")) {
            NBTItem nbtitem = new NBTItem(player.getInventory().getItemInMainHand());
            return String.valueOf(nbtitem.getInteger("level"));
        } else if (param.equalsIgnoreCase("xp")) {
            NBTItem nbtitem = new NBTItem(player.getInventory().getItemInMainHand());
            return String.valueOf(nbtitem.getInteger("xp"));
        }
        return "Invalid placeholder: leveltools_" + param;
    }
}