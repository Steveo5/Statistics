package statistics.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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
        Statistics.getMysqlConnector().getStoreQueries().saveMessage(evt.getPlayer(), evt.getMessage());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent evt) {
        Statistics.getMysqlConnector().getStoreQueries().saveDeath(evt.getEntity());
    }

    @EventHandler
    public void onPlayerKill(EntityDeathEvent evt) {
        Player killer = evt.getEntity().getKiller();

        if(killer != null) {
            Statistics.getMysqlConnector().getStoreQueries().saveKill(killer, evt.getEntity());
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent evt) {
        Statistics.getMysqlConnector().getStoreQueries().saveBlockBreak(evt.getPlayer(), evt.getBlock());
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent evt) {
        Statistics.getMysqlConnector().getStoreQueries().saveBlockPlace(evt.getPlayer(), evt.getBlock());
    }

}
