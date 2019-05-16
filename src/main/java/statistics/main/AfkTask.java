package statistics.main;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

public class AfkTask extends BukkitRunnable {

    private Statistics plugin;

    public AfkTask(Statistics plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for(StatisticsPlayer player : Statistics.getStatisticsPlayers()) {
            float sec = (System.currentTimeMillis() - player.getLastActiveTime()) / 1000F;

            if(!player.isAfk() && sec >= plugin.getConfig().getInt("afk-timeout")) {
                player.setAfk(true);
            }
        }
    }

}
