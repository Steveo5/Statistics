package statistics.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import statistics.main.Statistics;

public class PlayerListener implements Listener {


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent evt) {
        if(evt.getFrom().getBlockX() != evt.getTo().getBlockX()
                || evt.getFrom().getBlockY() != evt.getTo().getBlockY()
                || evt.getFrom().getBlockZ() != evt.getTo().getBlockZ()) {
            Statistics.getStatisticsPlayer(evt.getPlayer().getUniqueId()).setAfk(false);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt) {
        Statistics.getStatisticsPlayer(evt.getPlayer().getUniqueId()).setAfk(false);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent evt) {
        Statistics.getStatisticsPlayer(evt.getPlayer()).setAfk(false);
    }

}
