package statistics.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import statistics.main.*;
import statistics.util.DateUtil;

import java.util.HashMap;
import java.util.UUID;

public class CmdStatistics implements CommandExecutor {

    private Statistics plugin;

    public CmdStatistics(Statistics plugin) {
        this.plugin = plugin;
    }

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) {
            if(!(sender instanceof Player)) {
                sendSessionData(sender, null);
            } else {
                sendSessionData(sender, (Player)sender);
            }

            return true;
        }

        if(args[0].equalsIgnoreCase("reload")) {
            Statistics.reload();
            sender.sendMessage(ChatColor.GREEN + "Config was reloaded successfully");
        } else {
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);

            if(p == null || (p  != null && !p.hasPlayedBefore())) {
                sender.sendMessage(ChatColor.RED + "You have entered an invalid player name");
            } else {
                sendSessionData(sender, p);
            }
        }

        return true;
    }

    public void sendSessionData(CommandSender sender, OfflinePlayer byPlayer) {
        Statistics.getMysqlConnector().getSessions(new NumberedListCallback() {

            @Override
            public void call(NumberedList<Session> sessions) {
                sender.sendMessage("Average session time: " + DateUtil.formatSeconds(sessions.average()));
                sender.sendMessage("Maximum session time: " + DateUtil.formatSeconds(sessions.getMax().timeBetween()));
                sender.sendMessage("Minimum session time: " + DateUtil.formatSeconds(sessions.getMin().timeBetween()));
                sender.sendMessage("Total time played: " + DateUtil.formatSeconds(sessions.sum()));
            }
        }, byPlayer);
    }

}
