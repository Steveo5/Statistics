package statistics.main;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class StatisticsReport {

    /**
     * Get min, max, avg ping results for the server or a player
     * @param player which can be null in which case the global ping rates are used
     * @return
     */
    public HashMap<String, HashMap<String, String>> getPing(Player player) {
        return Statistics.getMysqlConnector().getPing(player == null ? null : player.getUniqueId().toString());
    }

}
