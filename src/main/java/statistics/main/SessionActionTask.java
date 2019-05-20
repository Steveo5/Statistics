package statistics.main;

import org.bukkit.scheduler.BukkitRunnable;

public class SessionActionTask extends BukkitRunnable {

    private Statistics plugin;

    public SessionActionTask(Statistics plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for(StatisticsPlayer player : Statistics.getStatisticsPlayers()) {

            // Check afk timeout
            float sec = (System.currentTimeMillis() - player.getLastActiveTime()) / 1000F;

            if(!player.isAfk() && sec >= plugin.getConfig().getInt("afk-timeout")) {
                player.setAfk(true);
            }

            if(player.getSession().isDoing()) {

                if(!player.lastActiveTimes.containsKey(player.getSession().getAction().getType())) continue;

                if(player.isInactive(player.getSession().getAction().getType(), 30)) {
                    player.getSession().stopAction();
                }
            }
        }
    }

}
