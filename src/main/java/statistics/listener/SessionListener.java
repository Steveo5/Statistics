package statistics.listener;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.player.*;
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
    public void onPlayerFish(PlayerFishEvent evt) {
        StatisticsPlayer player = Statistics.getStatisticsPlayer(evt.getPlayer().getUniqueId());
        player.setLastActiveTime(SessionActionType.FISHING, System.currentTimeMillis());

        if(!player.getSession().isDoing(SessionActionType.FISHING)) {
            player.getSession().beginAction(SessionActionType.FISHING, evt.getPlayer().getWorld());
        }
    }

    @EventHandler
    public void onPlayerMine(BlockBreakEvent evt) {
        StatisticsPlayer player = Statistics.getStatisticsPlayer(evt.getPlayer().getUniqueId());
        onPlayerMine(player, evt.getBlock().getWorld());
    }

    @EventHandler
    public void onPlayerMine(BlockPlaceEvent evt) {
        StatisticsPlayer player = Statistics.getStatisticsPlayer(evt.getPlayer().getUniqueId());
        onPlayerMine(player, evt.getBlock().getWorld());
    }

    private void onPlayerMine(StatisticsPlayer player, World w) {
        player.setLastActiveTime(SessionActionType.MINING, System.currentTimeMillis());

        if(!player.getSession().isDoing(SessionActionType.MINING)) {
            player.getSession().beginAction(SessionActionType.MINING, w);
        }
    }

    @EventHandler
    public void onPlayerSwim(PlayerMoveEvent evt) {
        if(evt.getFrom().getBlockX() != evt.getTo().getBlockX()
                || evt.getFrom().getBlockY() != evt.getTo().getBlockY()
                || evt.getFrom().getBlockZ() != evt.getTo().getBlockZ()) {
            Material to = evt.getTo().getBlock().getType();
            if(to == Material.WATER) {
                StatisticsPlayer player = Statistics.getStatisticsPlayer(evt.getPlayer().getUniqueId());
                player.setLastActiveTime(SessionActionType.SWIMMING, System.currentTimeMillis());

                if(!player.getSession().isDoing(SessionActionType.SWIMMING)) {
                    player.getSession().beginAction(SessionActionType.SWIMMING, evt.getPlayer().getWorld());
                }
            }
        }
    }

}
