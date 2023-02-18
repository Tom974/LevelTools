package dev.tom974.LevelTools.events;

import dev.tom974.LevelTools.LevelToolHandler;
import dev.tom974.LevelTools.objects.Handler;

import org.bukkit.Bukkit;
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

        // if (event.isCancelled()) {
        //     return;
        // }

        // TODO: use this? this is for checking if the region starts with mine-
        // boolean bool = manager.getApplicableRegions(event.getBlock().getLocation()).getRegions().stream().anyMatch(region->region.getId().startsWith("mine-"));
        RegionManager manager = container.get(event.getBlock().getWorld());
        if (manager == null) return; // no regions in this world (or worldguard is not installed
        boolean bool = manager.getApplicableRegions(event.getBlock().getLocation()).getRegions().stream().anyMatch(region->region.getFlag(DefaultFlag.BLOCK_BREAK) == State.ALLOW);
        if (!bool) return; // user is not in a region where block break is allowed, so dont add pickaxe xp

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
