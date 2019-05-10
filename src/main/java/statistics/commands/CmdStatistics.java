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
                sender.sendMessage(getStatistics(null));
                sender.sendMessage(Statistics.getMysqlConnector().getHoursPlayed(null).toString());
            } else {
                sender.sendMessage(getStatistics(null));
                sender.sendMessage(Statistics.getMysqlConnector().getHoursPlayed(null).toString());
            }

            return true;
        }

        if(args[0].equalsIgnoreCase("reload")) {
            Statistics.reload();
            sender.sendMessage(ChatColor.GREEN + "Config was reloaded successfully");
        } else {
            Player p = Bukkit.getPlayer(args[0]);

            if(p == null) {
                sender.sendMessage(ChatColor.RED + "You have entered an invalid player name");
            } else {
                sender.sendMessage(getStatistics(p));
                sender.sendMessage(Statistics.getMysqlConnector().getHoursPlayed(null) + " hours played");
            }
        }

        return true;
    }

    public String getStatistics(Player player) {
        return Statistics.getStatisticsReport().getPing(player) + "";
    }

}
