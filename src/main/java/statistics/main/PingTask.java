package statistics.main;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PingTask extends BukkitRunnable {

    private int getPlayerPing(Player player)
    {
        return ((CraftPlayer) player).getHandle().ping;
    }

    @Override
    public void run() {
        for(StatisticsPlayer player : Statistics.getStatisticsPlayers()) {
            Statistics.getMysqlConnector().savePing(player.getBase().getUniqueId().toString(),
                    getPlayerPing(player.getBase()));
        }
    }
}
