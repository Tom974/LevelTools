package dev.tom974.LevelTools.objects.tools;

import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.tom974.LevelTools.Main;
import dev.tom974.LevelTools.objects.Handler;
import dev.tom974.LevelTools.objects.LevelTool;
import dev.tom974.LevelTools.util.Util;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockBreakTool extends LevelTool implements Handler {

    private final HashMap<Material, Integer> blockXP = new HashMap<>();
    private final Configuration config = Main.getInstance().getConfig();
    private final String type;
    private Integer blocksBroken;

    public BlockBreakTool(String type, ItemStack item, Player player) {
        super(type, item, player);
        this.type = type;
        NBTItem nbtItem = super.getNBTItem();
        this.blocksBroken = nbtItem.getInteger("blocks");
        //Load blocks and their respective XP values
        for (String s : config.getStringList(type + ".blocks")) {
            String[] split = s.split(":", 2);
            Material material = XMaterial.matchXMaterial(split[0]).get().parseMaterial();
            Integer worth = Integer.valueOf(split[1]);
            blockXP.put(material, worth);
        }
    }

    @Override
    public void handle(Object parameter, Player player) {
        List<Block> blockList = (List<Block>) parameter;
        for (Block block : blockList) {
            super.addXP(blockXP.getOrDefault(block.getType(), 0), player);
        }
        this.blocksBroken += 1;
        saveItem();
    }

    @Override
    protected void setCustomLore() {
        List<String> lore = new ArrayList<>(config.getStringList(type + ".lore"));
        Util.replaceList(lore, "%blocks%", this.blocksBroken.toString());
        super.setLore(lore);
    }


    @Override
    public void saveItem() {
        super.getNBTItem().setInteger("blocks", blocksBroken);
        super.saveItem();
    }
}
