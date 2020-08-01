package com.ardagunsuren.IslandLeaderboard.objects;

import com.ardagunsuren.IslandLeaderboard.IslandLeaderboard;
import com.ardagunsuren.IslandLeaderboard.enums.Depend;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

public class DependsObject {
    @Getter
    private Depend depend;
    @Getter
    private IslandLeaderboard plugin;
    @Getter
    private String dependName;
    @Getter
    private Integer interval;
    @Getter
    private String table;
    @Getter
    private String idColumn;
    @Getter
    private String leaderNameColumn;
    @Getter
    private String teamColumn;
    @Getter
    private String levelColumn;
    @Getter
    private FileConfiguration config;
    @Getter
    private String database;
    @Getter
    private BukkitTask task;
    @Getter
    private long lastUpdate = 0;


    public DependsObject(IslandLeaderboard plugin, Depend depend) {
        this.plugin = plugin;
        this.depend = depend;
        this.dependName = depend.toString();
        this.config = plugin.getConfig();
        loadData();
        startTask();
    }

    private void loadData() {
        this.interval = config.getInt("depends." + dependName + ".interval") * (20*60);
        this.table = getConfigString("table");
        this.idColumn = getConfigString("idColumn");
        this.leaderNameColumn = getConfigString("leaderNameColumn");
        this.teamColumn = getConfigString("teamColumn");
        this.levelColumn = getConfigString("levelColumn");
        this.database = getConfigString("database");
        createTable();
    }

    private String getConfigString(String path) {
        return config.getString("depends." + dependName + "." + path);
    }

    public void startTask() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                updateTopTen();
            }
        }, (20*60),interval);
    }

    public void updateTopTen() {
        if (!Bukkit.getPluginManager().isPluginEnabled(dependName)) {
            plugin.getLogger().severe(dependName + " not found! Task cancelled!");
            plugin.getEnabledDepends().remove(depend);
            plugin.checkDependsAvaliable();
            cancelTask();
            return;
        }
        plugin.getLogger().info("Updating " + dependName + " leaderboard ..");
        if (depend == Depend.ASkyBlock) {
            plugin.getASkyBlockManager().updateTopTen(this);
        } else if (depend == Depend.IridiumSkyblock) {
            plugin.getIridiumSkyblockManager().updateTop(this);
        } else if (depend == Depend.FabledSkyBlock) {
            plugin.getFabledSkyblockManager().updateTop(this);
        }

    }

    public void cancelTask() {
        task.cancel();
    }

    private void createTable() {
        plugin.getSqlManager().updateSQL("CREATE TABLE IF NOT EXISTS `" + database + "`.`" + table + "` ( `" + idColumn + "` int(11) DEFAULT NULL, `" + leaderNameColumn + "` text, `" + teamColumn + "` text, `" + levelColumn + "` int(11) DEFAULT NULL);");
    }

    public void setLastUpdate() {
        this.lastUpdate = System.currentTimeMillis();
    }

}
