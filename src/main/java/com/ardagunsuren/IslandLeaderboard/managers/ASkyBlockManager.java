package com.ardagunsuren.IslandLeaderboard.managers;

import com.ardagunsuren.IslandLeaderboard.IslandLeaderboard;
import com.ardagunsuren.IslandLeaderboard.objects.DependsObject;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.CoopPlay;
import com.wasteofplastic.askyblock.Island;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Collectors;

public class ASkyBlockManager {
    private IslandLeaderboard plugin;
    public ASkyBlockManager(IslandLeaderboard plugin) {
        this.plugin = plugin;
    }

    public void updateTopTen(DependsObject object) {
        Map<UUID, Long> uuidTopTen = new HashMap<>(ASkyBlockAPI.getInstance().getLongTopTen());
        Map<Island, Long> isTop = new HashMap<>();
        for (UUID uuid : uuidTopTen.keySet()) {
            isTop.put(ASkyBlockAPI.getInstance().getIslandOwnedBy(uuid), uuidTopTen.get(uuid));
        }
        isTop = isTop.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        int i = 0;
        for (Island island : isTop.keySet()) {
            i++;
            if (island == null) continue;
            String leader = Bukkit.getOfflinePlayer(island.getOwner()).getName();
            List<String> members = new ArrayList<>();
            Long level = isTop.get(island);

            List<UUID> islandMembersUUID = new ArrayList<>(getMembersWithoutCoops(island));
            while (islandMembersUUID.contains(island.getOwner())) {
                islandMembersUUID.remove(island.getOwner());
            }
            for (UUID uuid : islandMembersUUID) {
                if (Bukkit.getOfflinePlayer(uuid) != null) {
                    members.add(Bukkit.getOfflinePlayer(uuid).getName());
                }
            }
            plugin.getSqlManager().updateTopTen(object, i, leader, String.join(", ", members), level);
        }
        object.setLastUpdate();
        plugin.getLogger().info(object.getDependName() + " leaderboard updated.");
    }

    public List<UUID> getMembersWithoutCoops(Island island) {
        List<UUID> members = island.getMembers();
        for (UUID uuid : CoopPlay.getInstance().getCoopPlayers(island.getCenter())) {
            members.remove(uuid);
        }
        return members;

    }


}
