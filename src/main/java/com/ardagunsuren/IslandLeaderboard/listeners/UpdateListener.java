package com.ardagunsuren.IslandLeaderboard.listeners;

import com.ardagunsuren.IslandLeaderboard.IslandLeaderboard;
import com.ardagunsuren.IslandLeaderboard.utils.UpdateChecker;
import com.ardagunsuren.IslandLeaderboard.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateListener implements Listener {
    private IslandLeaderboard plugin;
    public UpdateListener(IslandLeaderboard plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    private void on(PlayerJoinEvent e) {
        if (!plugin.isCheckUpdate()) {
            return;
        }
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if (p.isOp()) {
                    new UpdateChecker(plugin, 81369).getVersion(version -> {
                        if (!plugin.getDescription().getVersion().equalsIgnoreCase(version)) {
                            p.sendMessage(Utils.c(plugin.getUpdateMessage(true)));
                        }
                    });
                }
            }
        });

    }
}
