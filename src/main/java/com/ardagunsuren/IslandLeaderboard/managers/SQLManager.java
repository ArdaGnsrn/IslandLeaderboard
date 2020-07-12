package com.ardagunsuren.IslandLeaderboard.managers;

import com.ardagunsuren.IslandLeaderboard.IslandLeaderboard;
import com.ardagunsuren.IslandLeaderboard.enums.Depend;
import com.ardagunsuren.IslandLeaderboard.objects.DependsObject;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.CoopPlay;
import com.wasteofplastic.askyblock.Island;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SQLManager {
    private IslandLeaderboard plugin;

    private HikariDataSource pointsDataSource;

    private String hostName;
    private String port;
    private String userName;
    private String password;

    public SQLManager(IslandLeaderboard plugin) {
        this.plugin = plugin;
        init();
        setupPool();
    }

    private void init() {
        FileConfiguration config = plugin.getConfig();
        hostName = config.getString("database.hostName");
        port = config.getString("database.port");
        userName = config.getString("database.userName");
        password = config.getString("database.password");
    }

    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(
                "jdbc:mysql://" +
                        hostName +
                        ":" +
                        port
        );
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(userName);
        config.setPassword(password);
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(5);
        config.setConnectionTimeout(15000);
        config.setLeakDetectionThreshold(10000);
        config.setPoolName("IslandLeaderboardPool");
        pointsDataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return pointsDataSource.getConnection();
    }

    private void closePool() {
        if (pointsDataSource != null && !pointsDataSource.isClosed()) {
            pointsDataSource.close();
        }
    }


    public boolean updateSQL(String QUERY) {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(QUERY);
            int count = statement.executeUpdate();
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean topTenEntryExist(DependsObject object, int i) {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(String.format("SELECT " + object.getIdColumn() + " FROM `" + object.getDatabase() + "`.`" + object.getTable() + "` WHERE `id` = %d;", i));
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateTopTen(DependsObject object, int i, String leader, String members, long level) {
        if (topTenEntryExist(object, i)) {
            updateSQL("UPDATE `" + object.getDatabase() + "`.`" + object.getTable() + "` SET `" + object.getLeaderNameColumn() + "` = '" + leader + "', `" + object.getTeamColumn() + "` = '" + members + "', `" + object.getLevelColumn() + "` = '" + level + "' WHERE `" + object.getDatabase() + "`.`" + object.getTable() + "`.`" + object.getIdColumn() + "` = " + i + ";");
        } else {
            updateSQL("INSERT INTO `" + object.getDatabase() + "`.`" + object.getTable() + "` (`" + object.getIdColumn() + "`, `" + object.getLeaderNameColumn() + "`, `" + object.getTeamColumn() + "`, `" + object.getLevelColumn() + "`) VALUES (" + i + ", '" + leader + "', '" + members + "', '" + level + "');");
        }
    }

    public void onDisable() {
        closePool();
    }
}
