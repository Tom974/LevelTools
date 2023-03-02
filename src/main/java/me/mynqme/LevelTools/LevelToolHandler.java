package me.mynqme.LevelTools;

import me.mynqme.LevelTools.objects.Handler;
import me.mynqme.LevelTools.objects.tools.BlockBreakTool;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class LevelToolHandler {
    public static final List<String> blockBreakItems = Arrays.asList("DIAMOND_PICKAXE");


    public static Handler getLevelTool(Player player, ItemStack item) {
        Handler handler = null;
        String type = null;
        String itemName = item.getType().toString();

        switch (itemName) {
            case "DIAMOND_PICKAXE":
                handler = new BlockBreakTool("pickaxe", item, player);
                type = "pickaxe";
                break;
        }

        if (Main.getInstance().getConfig().getBoolean(type + ".enabled")) {
            return handler;
        }

        return null;
    }

}
