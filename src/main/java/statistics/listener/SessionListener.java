package statistics.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import statistics.main.SessionType;
import statistics.main.Statistics;
import statistics.main.StatisticsPlayer;

public class SessionListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerLogin(PlayerLoginEvent evt) {
        Statistics.createSession(Statistics.getStatisticsPlayer(evt.getPlayer().getUniqueId()), SessionType.NORMAL);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogout(PlayerQuitEvent evt) {
        Statistics.getStatisticsPlayer(evt.getPlayer().getUniqueId()).closeSession();
    }

    @EventHandler
    public void onPlayerSwitchWorld(PlayerChangedWorldEvent evt) {
        StatisticsPlayer player = Statistics.getStatisticsPlayer(evt.getPlayer().getUniqueId());
        player.closeSession();
        Statistics.createSession(player, SessionType.NORMAL);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent evt) {
        if(evt.getFrom().getBlockX() != evt.getTo().getBlockX()
                || evt.getFrom().getBlockY() != evt.getTo().getBlockY()
                || evt.getFrom().getBlockZ() != evt.getTo().getBlockZ()) {

        }
    }

}
