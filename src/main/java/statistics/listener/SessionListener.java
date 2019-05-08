package statistics.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import statistics.main.SessionType;
import statistics.main.Statistics;
import statistics.main.StatisticsPlayer;

public class SessionListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent evt) {
        Statistics.createSession(Statistics.getStatisticsPlayer(evt.getPlayer()), SessionType.NORMAL);
    }

    @EventHandler
    public void onPlayerSwitchWorld(PlayerChangedWorldEvent evt) {
        StatisticsPlayer player = Statistics.getStatisticsPlayer(evt.getPlayer().getUniqueId());
        player.closeSession();
        Statistics.createSession(player, SessionType.NORMAL);
    }

}
