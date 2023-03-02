package me.mynqme.LevelTools.events;

import me.mynqme.LevelTools.LevelToolHandler;
import me.mynqme.LevelTools.objects.Handler;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.ArrayList;
import java.util.List;

public class BlockBreakEvent implements Listener {
    private final WorldGuardPlugin worldGuardPlugin = WorldGuardPlugin.inst();
    private final RegionContainer container = worldGuardPlugin.getRegionContainer();
    private final List<ProtectedRegion> blacklist = new ArrayList<>();

    @EventHandler
    public void blockBreakEvent(org.bukkit.event.block.BlockBreakEvent event) {

        RegionManager manager = container.get(event.getBlock().getWorld());
        if (manager == null) return;
        boolean bool = manager.getApplicableRegions(event.getBlock().getLocation()).getRegions().stream().anyMatch(region->region.getFlag(DefaultFlag.BLOCK_BREAK) == State.ALLOW);
        if (!bool) return;

        String material = event.getPlayer().getInventory().getItemInMainHand().getType().toString();
        if (!LevelToolHandler.blockBreakItems.contains(material)) {
            return;
        }

        Handler handler = LevelToolHandler.getLevelTool(event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand());
        if (handler == null) {
            return;
        }

        List<Block> blockList = new ArrayList<>();
        blockList.add(event.getBlock());
        handler.handle(blockList, event.getPlayer());

    }

    @EventHandler
    public void gainExperience(PlayerExpChangeEvent event) {
        event.setAmount(0);
    }

}
