package me.mynqme.LevelTools;

import de.leonhard.storage.SimplixBuilder;
import de.leonhard.storage.Yaml;
import de.leonhard.storage.internal.settings.ConfigSettings;
import me.mynqme.LevelTools.database.Database;
import me.mynqme.LevelTools.events.Listeners;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class LevelTools extends JavaPlugin {
    private static LevelTools instance;

    public Yaml config;
    public Handler handler;
    public Database database;

    public HashMap<UUID, Integer> xpMap = new HashMap<UUID, Integer>();
    public HashMap<UUID, Integer> levelMap = new HashMap<UUID, Integer>();

    public HashMap<UUID, Integer> blockMap = new HashMap<UUID, Integer>();

    public static LevelTools getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        config = SimplixBuilder.fromFile(new File(getDataFolder(),"config")).addInputStream(getResource("config.yml")).setConfigSettings(ConfigSettings.PRESERVE_COMMENTS).createYaml();
        database = new Database(this);
        getServer().getPluginCommand("leveltools").setExecutor(new Command(this));
        getServer().getPluginManager().registerEvents(new Listeners(this), this);
        new Placeholders().register();
        handler = new Handler(this);
    }

    @Override
    public void onDisable() {
        saveAll();
    }

    public void saveAll() {
        for (UUID uuid : xpMap.keySet()) {
            database.savePlayer(uuid, xpMap.get(uuid), levelMap.get(uuid), blockMap.get(uuid));
        }
    }
}
