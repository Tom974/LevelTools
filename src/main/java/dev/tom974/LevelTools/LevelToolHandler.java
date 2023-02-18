package dev.tom974.LevelTools;

import dev.tom974.LevelTools.objects.Handler;
import dev.tom974.LevelTools.objects.tools.BlockBreakTool;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class LevelToolHandler {

    public static final List<String> damageItems = Arrays.asList("DIAMOND_SWORD", "NETHERITE_SWORD", "GOLD_SWORD", "IRON_SWORD", "STONE_SWORD", "WOODEN_SWORD",
            "BOW", "CROSSBOW");
    public static final List<String> blockBreakItems = Arrays.asList("DIAMOND_PICKAXE", "NETHERITE_PICKAXE", "GOLD_PICKAXE", "IRON_PICKAXE", "STONE_PICKAXE", "WOODEN_PICKAXE",
            "DIAMOND_AXE", "NETHERITE_AXE", "GOLD_AXE", "IRON_AXE", "STONE_AXE", "WOODEN_AXE",
            "DIAMOND_SHOVEL", "NETHERITE_SHOVEL", "GOLD_SHOVEL", "IRON_SHOVEL", "STONE_SHOVEL", "WOODEN_SHOVEL");


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
