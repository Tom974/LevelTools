package dev.tom974.LevelTools.objects;

import org.bukkit.entity.Player;

public interface Handler {
    void handle(Object parameter, Player player);

    void saveItem();

    void addXP(int amt, Player player);
}
