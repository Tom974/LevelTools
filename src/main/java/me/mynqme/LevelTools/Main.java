package me.mynqme.LevelTools;

import de.leonhard.storage.SimplixBuilder;
import de.leonhard.storage.Yaml;
import de.leonhard.storage.internal.settings.ConfigSettings;
import me.mynqme.LevelTools.events.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {
    private static Main instance;

    public Yaml config;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        config = SimplixBuilder.fromFile(new File(getDataFolder(),"config")).addInputStream(getResource("config.yml")).setConfigSettings(ConfigSettings.PRESERVE_COMMENTS).createYaml();
        getServer().getPluginCommand("leveltools").setExecutor(new Command());
        getServer().getPluginManager().registerEvents(new BlockBreakEvent(), this);
        new Placeholders().register();
    }
}
