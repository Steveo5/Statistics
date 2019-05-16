package statistics.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import statistics.main.SessionActionType;
import statistics.main.Statistics;
import statistics.main.StatisticsPlayer;

public class SessionListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent evt) {
        Statistics.createSession(Statistics.getStatisticsPlayer(evt.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        Statistics.getStatisticsPlayer(evt.getPlayer()).closeSession();
        Statistics.unloadStatisticsPlayer(evt.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerSwitchWorld(PlayerChangedWorldEvent evt) {
        StatisticsPlayer player = Statistics.getStatisticsPlayer(evt.getPlayer().getUniqueId());
        //player.closeSession();
        //Statistics.createSession(player, SessionType.NORMAL);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent evt) {
        StatisticsPlayer player = Statistics.getStatisticsPlayer(evt.getPlayer().getUniqueId());
        player.setLastActiveTime(SessionActionType.FISHING, System.currentTimeMillis());
    }

}
