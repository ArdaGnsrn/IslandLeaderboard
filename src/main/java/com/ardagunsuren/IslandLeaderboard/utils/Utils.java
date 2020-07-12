package com.ardagunsuren.IslandLeaderboard.utils;

import org.bukkit.ChatColor;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static String c(String t) {
        return ChatColor.translateAlternateColorCodes('&', t);
    }

    public static String timestampToString(long timestamp) {
        if (timestamp == 0) {
            return "Unknown";
        }
        Date date = new Date(timestamp);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return format.format(date);
    }
}
