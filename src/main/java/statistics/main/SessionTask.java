package statistics.main;

import org.bukkit.scheduler.BukkitRunnable;

public class SessionTask extends BukkitRunnable {

    @Override
    public void run() {
        for(StatisticsPlayer player : Statistics.getStatisticsPlayers()) {
            if(!player.getBase().isOnline()) {
                player.closeSession();
                Statistics.unloadStatisticsPlayer(player.getBase());
            } else {
                player.getSession().save(player);
            }
        }
    }

}
