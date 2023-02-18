package dev.tom974.LevelTools;

import com.tchristofferson.configupdater.ConfigUpdater;
import dev.tom974.LevelTools.events.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        new Metrics(this, 11581);
        instance = this;
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        List<String> ignoredSections = Arrays.asList("pickaxe", "axe", "bow", "crossbow", "sword", "shovel");
        updateConfig(configFile, ignoredSections);
        reloadConfig();
        getServer().getPluginCommand("leveltools").setExecutor(new Command());
        getServer().getPluginManager().registerEvents(new BlockBreakEvent(), this);
    }

    private void updateConfig(File file, List<String> ignoredSections) {
        try {
            ConfigUpdater.update(this, file.getName(), file, ignoredSections);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
