package me.mynqme.LevelTools.objects;

import de.tr7zw.nbtapi.NBTItem;
import me.mynqme.LevelTools.LevelTools;
import me.mynqme.LevelTools.objects.LevelTool;
import me.mynqme.LevelTools.util.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockBreakTool extends LevelTool {

    private final HashMap<Material, Integer> blockXP = new HashMap<>();
    private final String type;
    private Integer blocksBroken;

    public BlockBreakTool(String type, ItemStack item, Player player) {
        super(type, item, player);
        this.type = type;
        NBTItem nbtItem = super.getNBTItem();
        this.blocksBroken = nbtItem.getInteger("blocks");
        //Load blocks and their respective XP values
//        for (String s : LevelTools.getInstance().config.getStringList(type + ".blocks")) {
//            String[] split = s.split(":", 2);
        Material material = Material.getMaterial("STONE");
        Integer worth = Integer.valueOf("1");
        blockXP.put(material, worth);
//        }
    }

    public void handle(Object parameter, Player player) {
        super.addXP(1, player);
        this.blocksBroken += 1;
        saveItem();
    }

    protected void setCustomLore() {
        List<String> lore = new ArrayList<>(LevelTools.getInstance().config.getStringList(type + ".lore"));
        Util.replaceList(lore, "%blocks%", this.blocksBroken.toString());
        super.setLore(lore);
    }

    public void saveItem() {
        super.getNBTItem().setInteger("blocks", blocksBroken);
        super.saveItem();
    }
}
