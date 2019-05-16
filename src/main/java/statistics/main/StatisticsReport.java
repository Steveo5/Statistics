package statistics.main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class StatisticsReport {

    public String getPingStatistics(UUID player) {
        StringBuilder str = new StringBuilder();

        HashMap<String, HashMap<String, String>> ping = Statistics.getMysqlConnector().getPing(player == null ? null : player.toString());

        str.append("Max ping: ").append(ping.get("max").get("ping"));
        if(player == null) str.append(" (" + Bukkit.getOfflinePlayer(UUID.fromString(ping.get("max").get("user_id"))).getName() + ")");
        str.append("\nMin ping: ").append(ping.get("min").get("ping"));
        if(player == null) str.append(" (" + Bukkit.getOfflinePlayer(UUID.fromString(ping.get("min").get("user_id"))).getName() + ")");
        str.append("\nAvg ping: ").append(ping.get("avg").get("avg"));

        return str.toString();
    }

    public String getSessionStatistics(UUID player) {
        StringBuilder str = new StringBuilder();

        HashMap<String, HashMap<String, String>> hours = Statistics.getMysqlConnector().getHoursPlayed(player == null ? null : player.toString());

        str.append("Total: ").append("\nAll time: " + hours.get("total").get("all_time"));
        str.append("\nAfk: ").append(hours.get("total").get("afk"));
        str.append("\nAvg: ").append(hours.get("avg").get("hours"));
        str.append("\nSession:");
        str.append("\nMax: ").append(hours.get("max").get("hours"));
        if(player == null) str.append(" (" + Bukkit.getOfflinePlayer(UUID.fromString(hours.get("max").get("user"))).getName() + ")");
        str.append("\nMin: ").append(hours.get("min").get("hours"));
        if(player == null) str.append(" (" + Bukkit.getOfflinePlayer(UUID.fromString(hours.get("min").get("user"))).getName() + ")");

        return str.toString();
    }

}
