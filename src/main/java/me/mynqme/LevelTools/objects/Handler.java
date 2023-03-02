package me.mynqme.LevelTools.objects;

import org.bukkit.entity.Player;

public interface Handler {
    void handle(Object parameter, Player player);

    void saveItem();

    void addXP(int amt, Player player);

    void addLevel(int amt, Player player);
}
