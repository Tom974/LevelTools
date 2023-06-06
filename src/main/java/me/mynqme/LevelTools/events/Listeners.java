package me.mynqme.LevelTools.events;

import me.mynqme.LevelTools.LevelTools;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;

public class Listeners implements Listener {
    private final WorldGuardPlugin worldGuardPlugin = WorldGuardPlugin.inst();
    private final RegionContainer container = worldGuardPlugin.getRegionContainer();
    private final List<ProtectedRegion> blacklist = new ArrayList<>();
    private final LevelTools instance;

    public Listeners(LevelTools instance) {
        this.instance = instance;
    }

    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event) {

        RegionManager manager = container.get(event.getBlock().getWorld());
        if (manager == null) return;
        boolean bool = manager.getApplicableRegions(event.getBlock().getLocation()).getRegions().stream().anyMatch(region->region.getFlag(DefaultFlag.BLOCK_BREAK) == State.ALLOW);
        if (!bool) return;
        this.instance.handler.handleBlockBreak(event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand());
        Bukkit.getLogger().info("Handled block break for user");

//        String material = event.getPlayer().getInventory().getItemInMainHand().getType().toString();
//        if (!LevelToolHandler.blockBreakItems.contains(material)) {
//            return;
//        }
//
//        BlockBreakTool handler = LevelToolHandler.getLevelTool(event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand());
//        if (handler == null) {
//            return;
//        }
//
//        List<Block> blockList = new ArrayList<>();
//        blockList.add(event.getBlock());
//        handler.handle(blockList, event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.instance.database.fetchPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void gainExperience(PlayerExpChangeEvent event) {
        event.setAmount(0);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDroppedExp(0);
        // get old level
        int oldLevel = event.getEntity().getLevel();
        // set old level after respawn
        event.getEntity().setLevel(oldLevel);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        // get old level
        int oldLevel = event.getPlayer().getLevel();
        // set old level after respawn
        event.getPlayer().setLevel(oldLevel);
    }
}
