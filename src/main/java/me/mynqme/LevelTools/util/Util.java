package me.mynqme.LevelTools.util;

import com.google.common.base.Strings;
import me.mynqme.LevelTools.LevelTools;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.Configuration;

import java.util.List;
import java.util.regex.Pattern;

public class Util {

    public static final Pattern HEX_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-f])");

    public static String translateColorCodes(String textToTranslate) {

        return ChatColor.translateAlternateColorCodes('&', textToTranslate);

    }

    public static String getProgressBar(int current, int max) {
        Configuration config = LevelTools.getInstance().getConfig();
        char symbol = config.getString("settings.progressbar.filler").charAt(0);
        int totalBars = config.getInt("settings.progressbar.bars");
        ChatColor completedColor = ChatColor.getByChar(config.getString("settings.progressbar.complete_color").charAt(0));
        ChatColor notCompletedColor = ChatColor.getByChar(config.getString("settings.progressbar.incomplete_color").charAt(0));
        if (max == 0) {
            return Strings.repeat("" + completedColor + symbol, totalBars);
        }
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);
        return Strings.repeat("" + completedColor + symbol, progressBars)
                + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
    }


    public static List<String> replaceList(List<String> list, String from, String to) {
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            s = s.replace(from, to);
            list.set(i, Util.translateColorCodes(s));
        }
        return list;
    }


}
