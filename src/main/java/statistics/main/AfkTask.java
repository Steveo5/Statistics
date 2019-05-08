package statistics.main;

import org.bukkit.scheduler.BukkitRunnable;

public class AfkTask extends BukkitRunnable {

    @Override
    public void run() {
        for(StatisticsPlayer player : Statistics.getStatisticsPlayers()) {
            float sec = (System.currentTimeMillis() - player.getLastActiveTime()) / 1000F;

            if(!player.isAfk() && sec >= 30) {
                player.setAfk(true);
            }
        }
    }

}
