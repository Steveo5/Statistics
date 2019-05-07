package statistics.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import statistics.main.Statistics;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        Statistics.loadStatisticsPlayer(evt.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent evt) {
        Statistics.unloadStatisticsPlayer(evt.getPlayer());
    }

}
