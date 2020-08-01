package com.ardagunsuren.IslandLeaderboard.managers;

import com.ardagunsuren.IslandLeaderboard.IslandLeaderboard;
import com.ardagunsuren.IslandLeaderboard.objects.DependsObject;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.api.SkyBlockAPI;
import com.songoda.skyblock.api.island.Island;
import com.songoda.skyblock.api.island.IslandRole;
import com.songoda.skyblock.leaderboard.Leaderboard;
import com.songoda.skyblock.visit.Visit;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class FabledSkyblockManager {
    private IslandLeaderboard plugin;
    public FabledSkyblockManager(IslandLeaderboard plugin) {
        this.plugin = plugin;
    }

    public void updateTop(DependsObject object) {
        int i = 0;
        for (Leaderboard leaderboard : SkyBlock.getInstance().getLeaderboardManager().getLeaderboard(Leaderboard.Type.Level)) {
            i++;
            if (i>10) { break; }
            Visit visit = leaderboard.getVisit();
            OfflinePlayer op = Bukkit.getOfflinePlayer(visit.getOwnerUUID());
            if (op == null) { continue; }

            String leader = op.getName();
            Island island = SkyBlockAPI.getIslandManager().getIsland(op);
            if (island == null) {
                island = SkyBlockAPI.getIslandManager().getIslandByUUID(visit.getOwnerUUID());
            }
            String team = getTeam(island, visit.getMembers());
            long level = visit.getLevel().getLevel();
            plugin.getSqlManager().updateTopTen(object, leaderboard.getPosition() + 1, leader, team, level);
        }
        object.setLastUpdate();
        plugin.getLogger().info(object.getDependName() + " leaderboard updated.");

    }

    private String getTeam(Island island, int teamSize) {
        if (teamSize <= 1) { return "-"; }
        List<UUID> teamUUID = new ArrayList<>();
        teamUUID.addAll(island.getPlayersWithRole(IslandRole.MEMBER));
        teamUUID.addAll(island.getPlayersWithRole(IslandRole.OPERATOR));
        if (teamUUID.size() <= 0) { return "-"; }
        List<String> team = new ArrayList<>();
        for (UUID uuid : teamUUID) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            if (op == null) { continue; }
            team.add(op.getName());
        }
        return String.join(", ", team);
    }
}
