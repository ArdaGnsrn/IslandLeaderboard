package com.ardagunsuren.IslandLeaderboard;

import com.ardagunsuren.IslandLeaderboard.commands.IslandLeaderboardCommand;
import com.ardagunsuren.IslandLeaderboard.enums.Depend;
import com.ardagunsuren.IslandLeaderboard.listeners.UpdateListener;
import com.ardagunsuren.IslandLeaderboard.managers.ASkyBlockManager;
import com.ardagunsuren.IslandLeaderboard.managers.IridiumSkyblockManager;
import com.ardagunsuren.IslandLeaderboard.managers.SQLManager;
import com.ardagunsuren.IslandLeaderboard.objects.DependsObject;
import com.ardagunsuren.IslandLeaderboard.utils.UpdateChecker;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class IslandLeaderboard extends JavaPlugin {
    @Getter
    private SQLManager sqlManager;
    @Getter
    private ASkyBlockManager aSkyBlockManager;
    @Getter
    private IridiumSkyblockManager iridiumSkyblockManager;
    @Getter
    private Map<Depend, DependsObject> dependsMap = new HashMap<>();
    @Getter
    private List<Depend> enabledDepends = new ArrayList<>();
    @Getter
    private boolean checkUpdate = true;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        for (Depend depend : Depend.values()) {
            if (Bukkit.getPluginManager().isPluginEnabled(depend.toString())) {
                enabledDepends.add(depend);
            }
        }

        checkDependsAvaliable();

        sqlManager = new SQLManager(this);
        aSkyBlockManager = new ASkyBlockManager(this);
        iridiumSkyblockManager = new IridiumSkyblockManager(this);
        new IslandLeaderboardCommand(this);
        loadDepends();
        new UpdateListener(this);

        checkUpdate = getConfig().getBoolean("settings.checkUpdate");

        if (checkUpdate) {
            Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    checkUpdate();
                }
            });
        }


    }

    private void loadDepends() {
        for (Depend depend : enabledDepends) {
            dependsMap.put(depend, new DependsObject(this, depend));
        }
    }

    @Override
    public void onDisable() {
        sqlManager.onDisable();
        for (DependsObject object : dependsMap.values()) {
            object.cancelTask();
        }
    }

    public void checkDependsAvaliable() {
        if (enabledDepends.size() <= 0) {
            getLogger().severe("No plugin to connect to was found!");
            getLogger().severe("Plugins that can be linked: " + Arrays.toString(Depend.values()));
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public void checkUpdate() {

        new UpdateChecker(this, 81369).getVersion(version -> {
            if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
                getLogger().info("A new update is available for download at https://www.spigotmc.org/resources/81369/");             }
        });
    }

    public String getUpdateMessage(boolean update) {
        if (update) {
            return "&9[IslandLeaderboard] &aA new update is available for download at https://www.spigotmc.org/resources/81369/";
        } else {
            return "&9[IslandLeaderboard] &aThere is not a new update available.";
        }
    }
}
