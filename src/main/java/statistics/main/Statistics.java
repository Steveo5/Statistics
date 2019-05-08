package statistics.main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import statistics.commands.CmdStatistics;
import statistics.listener.PlayerListener;
import statistics.listener.SessionListener;
import statistics.storage.MysqlConnector;

import java.util.*;

public final class Statistics extends JavaPlugin {

    private static MysqlConnector mysqlConnector;
    private static HashMap<UUID, StatisticsPlayer> statisticsPlayers = new HashMap<>();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        mysqlConnector = new MysqlConnector(this);
        this.getServer().getPluginManager().registerEvents(new SessionListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        new SessionTask().runTaskTimer(this, 20L * 25L, 20L * 25L);
        new AfkTask().runTaskTimer(this, 20L * 20L, 20L * 20L);
        new PingTask().runTaskTimer(this, (20L * 60L) * 10L, (20L * 60L) * 10L);

        this.getCommand("statistics").setExecutor(new CmdStatistics(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static MysqlConnector getMysqlConnector() {
        return mysqlConnector;
    }

    /**
     * Create a new session for the player
     * @param player
     * @return
     */
    public static Session createSession(StatisticsPlayer player, SessionType type) {
        Session session = new Session(type);
        session.setStarted(new Date());

        player.setSession(session);

        return session;
    }

    protected static void unloadStatisticsPlayer(Player base) {
        if(statisticsPlayers.containsKey(base.getUniqueId())) statisticsPlayers.remove(base.getUniqueId());
    }

    public static StatisticsPlayer getStatisticsPlayer(UUID uuid) {
        return getStatisticsPlayer(Bukkit.getPlayer(uuid));
    }

    public static StatisticsPlayer getStatisticsPlayer(Player player) {
        if(!statisticsPlayers.containsKey(player.getUniqueId())) {
            statisticsPlayers.put(player.getUniqueId(), new StatisticsPlayer(player));
        }

        return statisticsPlayers.get(player.getUniqueId());
    }

    public static Collection<StatisticsPlayer> getStatisticsPlayers() {
        return statisticsPlayers.values();
    }
}
