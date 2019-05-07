package statistics.main;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class CommandHandler implements CommandExecutor {

    private Statistics plugin;

    public CommandHandler(Statistics plugin) {
        this.plugin = plugin;
    }

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) {
            return true;
        }

        if(args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            Statistics.getMysqlConnector().reload();
            sender.sendMessage(ChatColor.GREEN + "Config was reloaded successfully");
        }

        return true;
    }

}
