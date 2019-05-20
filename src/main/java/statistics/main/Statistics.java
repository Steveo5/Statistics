package statistics.main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import statistics.commands.CmdStatistics;
import statistics.listener.PlayerListener;
import statistics.listener.SessionListener;
import statistics.storage.MysqlConnector;

import java.util.*;

public final class Statistics extends JavaPlugin {

    private static MysqlConnector mysqlConnector;
    private static HashMap<UUID, StatisticsPlayer> statisticsPlayers = new HashMap<>();
    private static Statistics statistics;
    private BukkitTask pingTask;
    private static StatisticsReport report;

    @Override
    public void onEnable() {
        statistics = this;
        this.saveDefaultConfig();
        mysqlConnector = new MysqlConnector(this);
        this.getServer().getPluginManager().registerEvents(new SessionListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        new SessionTask().runTaskTimer(this, 20L * 25L, 20L * 25L);
        new SessionActionTask(this).runTaskTimer(this, 20L * 10L, 10L);
        pingTask = new PingTask().runTaskTimer(this, (20L * 60L) * getConfig().getInt("ping-interval"),
                (20L * 60L) * getConfig().getInt("ping-interval"));

        this.getCommand("statistics").setExecutor(new CmdStatistics(this));

        report = new StatisticsReport();
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
    public static Session createSession(StatisticsPlayer player) {
        Session session = new Session(player);
        session.setStarted(new Date());

        player.setSession(session);

        return session;
    }

    public static void unloadStatisticsPlayer(UUID id) {
        statisticsPlayers.remove(id);
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

    // Reload the entire plugin, including timers, config etc
    public static void reload() {
        statistics.reloadConfig();
        getMysqlConnector().reload();
        statistics.pingTask.cancel();
        statistics.pingTask = new PingTask().runTaskTimer(statistics,
                (20L * 60L) * statistics.getConfig().getInt("ping-interval"),
                (20L * 60L) * statistics.getConfig().getInt("ping-interval"));
    }

    public static StatisticsReport getStatisticsReport() {
        return report;
    }

    public static Statistics getInstance() {
        return statistics;
    }
}
