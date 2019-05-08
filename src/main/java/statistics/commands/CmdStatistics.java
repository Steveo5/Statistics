package statistics.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import statistics.main.Statistics;
import statistics.main.StatisticsPlayer;

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
                return false;
            } else {
                showStatistics(Statistics.getStatisticsPlayer(((Player) sender).getUniqueId()));
            }
        }

        if(args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            Statistics.getMysqlConnector().reload();
            sender.sendMessage(ChatColor.GREEN + "Config was reloaded successfully");
        } else {
            Player p = Bukkit.getPlayer(args[0]);

            if(p == null) {
                sender.sendMessage(ChatColor.RED + "You have entered an invalid player name");
            } else {
                showStatistics(Statistics.getStatisticsPlayer(p.getUniqueId()));
            }
        }

        return true;
    }

    public void showStatistics(StatisticsPlayer player) {

    }

}
