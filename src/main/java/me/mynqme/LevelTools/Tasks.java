package me.mynqme.LevelTools;

import org.bukkit.Bukkit;

public class Tasks {
    public LevelTools instance;
    public Tasks(LevelTools plugin) {
        this.instance = plugin;
    }

    public void autosavetask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            instance.saveAll();
        }, 0, 20 * 60 * 5); // run every 5 minutes
    }
}
