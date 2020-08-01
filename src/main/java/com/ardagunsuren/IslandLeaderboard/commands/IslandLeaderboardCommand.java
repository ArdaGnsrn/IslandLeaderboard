package com.ardagunsuren.IslandLeaderboard.commands;

import com.ardagunsuren.IslandLeaderboard.IslandLeaderboard;
import com.ardagunsuren.IslandLeaderboard.enums.Depend;
import com.ardagunsuren.IslandLeaderboard.objects.DependsObject;
import com.ardagunsuren.IslandLeaderboard.utils.UpdateChecker;
import com.ardagunsuren.IslandLeaderboard.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IslandLeaderboardCommand implements CommandExecutor, TabCompleter {
    private IslandLeaderboard plugin;
    private final String[] subCommands = { "updateall", "checkupdate" };
    public IslandLeaderboardCommand(IslandLeaderboard plugin) {
        this.plugin = plugin;
        plugin.getCommand("islandleaderboard").setExecutor(this);
        plugin.getCommand("islandleaderboard").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used within the game.");
            return true;
        }
        Player p = ((Player) sender).getPlayer();
        if (args.length == 0) {
            sendMessage(p, "&9&lPowered By ArdaGnsrn");
            sendMessage(p, " ");
            sendMessage(p, "&cCommands:");
            sendMessage(p, "&6/islandleaderboard updateall: &eUpdates all statistics");
            sendMessage(p, "&6/islandleaderboard checkupdate: &eCheck for updates for the plugin!");
            sendMessage(p, " ");
            sendMessage(p, "&cPlugins: ");
            sendEnabledDepends(p);
            sendMessage(p, " ");
            return true;
        } else if (args[0].equalsIgnoreCase("updateall")) {
            if (!isHavePermission(p, "updateall")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to do this!");
                return true;
            }
            for (DependsObject object : plugin.getDependsMap().values()) {
                object.updateTopTen();
                sendMessage(p, "&c" + object.getDependName() + " updated!");
            }
            sendMessage(p, "&cAll statistics have been updated!");
            return true;
        } else if (args[0].equalsIgnoreCase("checkupdate")) {
            if (!isHavePermission(p, "checkupdate")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to do this!");
                return true;
            }
            new UpdateChecker(plugin, 81369).getVersion(version -> {
                sendMessage(p, plugin.getUpdateMessage(!plugin.getDescription().getVersion().equalsIgnoreCase(version)));
            });
            return true;
        } else {
            sendMessage(p, "&9&lPowered By ArdaGnsrn");
            sendMessage(p, " ");
            sendMessage(p, "&cCommands:");
            sendMessage(p, "&6/islandleaderboard updateall: &eUpdates all statistics");
            sendMessage(p, "&6/islandleaderboard checkupdate: &eCheck for updates for the plugin!");
            sendMessage(p, " ");
            sendMessage(p, "&cPlugins: ");
            sendEnabledDepends(p);
            sendMessage(p, " ");
            return true;
        }
    }
    private void sendMessage(Player p, String msg) {
        p.sendMessage(Utils.c(msg));
    }
    private void sendEnabledDepends(Player p) {
        for (Depend depend : Depend.values()) {
            if (plugin.getEnabledDepends().contains(depend)) {
                DependsObject object = plugin.getDependsMap().get(depend);
                TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(ChatColor.WHITE + depend.toString() + ": " + ChatColor.GREEN + "WORKING"));
                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.c("&fEnabled: &atrue\n&fLast Update: &e" + Utils.timestampToString(object.getLastUpdate()) + "\n&fUpdate Interval: &e "+ (object.getInterval() / 1200) + " minute\n\n\n&c" + "Click to update the statistics.")).create()));
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/islandleaderboard updateall"));
                p.spigot().sendMessage(textComponent);
            } else {
                TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(ChatColor.WHITE + depend.toString() + ": " + ChatColor.RED + "NOT WORKING"));
                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.c("&fEnabled: &cfalse\n&fLast Update: &e-\n&fUpdate Interval: &e- minute\n\n\n&c&m" + "Click to update the statistics.")).create()));
                p.spigot().sendMessage(textComponent);
            }
        }

    }

    private boolean isHavePermission(Player p, String perm) {
        return p.isOp() || p.hasPermission("islandleaderboard.*") || p.hasPermission("islandleaderboard." + perm);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], Arrays.asList(subCommands), completions);
        Collections.sort(completions);
        return completions;
    }
}
