package statistics.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import statistics.main.Statistics;
import statistics.main.StatisticsReport;

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
                StatisticsReport report = new StatisticsReport();
                sender.sendMessage(report.getPingStatistics(null));
                sender.sendMessage(report.getSessionStatistics(null));
            } else {
                sender.sendMessage(new StatisticsReport().getPingStatistics(((Player)sender).getUniqueId()));
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
                StatisticsReport report = new StatisticsReport();
                sender.sendMessage(report.getPingStatistics(p.getUniqueId()));
                sender.sendMessage(report.getSessionStatistics(p.getUniqueId()));
            }
        }

        return true;
    }

}
