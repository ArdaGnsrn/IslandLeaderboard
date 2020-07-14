package com.ardagunsuren.IslandLeaderboard.managers;

import com.ardagunsuren.IslandLeaderboard.IslandLeaderboard;
import com.ardagunsuren.IslandLeaderboard.objects.DependsObject;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IridiumSkyblockManager {
    private IslandLeaderboard plugin;
    public IridiumSkyblockManager (IslandLeaderboard plugin) {
        this.plugin = plugin;
    }

    public void updateTop(DependsObject object) {
        int i = 0;
        for (Island island : Utils.getTopIslands()) {
            i++;
            if (i>10) { break; }
            String leader = getLeader(island);
            String team = getTeam(island);
            Long level = (long) island.getValue();
            plugin.getSqlManager().updateTopTen(object, i, leader, team, level);
        }
        object.setLastUpdate();
        plugin.getLogger().info(object.getDependName() + " leaderboard updated.");
    }

    private String getLeader(Island island) {
        UUID uid = UUID.fromString(island.getOwner());
        return Bukkit.getOfflinePlayer(uid).getName();
    }
    private String getTeam(Island island) {
        List<String> members = new ArrayList<>();
        for (String member : island.getMembers()) {
            UUID uid = UUID.fromString(member);
            members.add(Bukkit.getOfflinePlayer(uid).getName());
        }
        String leader = getLeader(island);
        while (members.contains(leader)) {
            members.remove(leader);
        }
        return String.join(", ", members);
    }
}
