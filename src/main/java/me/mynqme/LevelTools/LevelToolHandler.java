package me.mynqme.LevelTools;

import me.mynqme.LevelTools.objects.BlockBreakTool;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class LevelToolHandler {
    public static final List<String> blockBreakItems = Arrays.asList("DIAMOND_PICKAXE");


    public static BlockBreakTool getLevelTool(Player player, ItemStack item) {
        BlockBreakTool handler = null;
        String type = null;
        String itemName = item.getType().toString();

        switch (itemName) {
            case "DIAMOND_PICKAXE":
                handler = new BlockBreakTool("pickaxe", item, player);
                type = "pickaxe";
                break;
        }

        if (LevelTools.getInstance().getConfig().getBoolean(type + ".enabled")) {
            return handler;
        }

        return null;
    }

}
