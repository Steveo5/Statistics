package statistics.main;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import statistics.listener.PlayerListener;
import statistics.listener.SessionListener;
import statistics.storage.MysqlConnector;

import java.util.*;

public final class Statistics extends JavaPlugin {

    private static MysqlConnector mysqlConnector;
    private static HashMap<UUID, Session> sessions = new HashMap<>();
    private static HashMap<UUID, StatisticsPlayer> statisticsPlayers = new HashMap<>();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        mysqlConnector = new MysqlConnector(this);
        this.getServer().getPluginManager().registerEvents(new SessionListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        new SessionTask().runTaskTimer(this, 0L, 20L * 60L);

        this.getCommand("statistics").setExecutor(new CommandHandler(this));
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
        sessions.put(player.getBase().getUniqueId(), session);

        player.setSession(session);

        return session;
    }

    public static void loadStatisticsPlayer(Player base) {
        if(!statisticsPlayers.containsKey(base.getUniqueId())) {
            statisticsPlayers.put(base.getUniqueId(), new StatisticsPlayer(base));
        }
    }

    public static void unloadStatisticsPlayer(Player base) {
        if(statisticsPlayers.containsKey(base.getUniqueId())) statisticsPlayers.remove(base.getUniqueId());
    }

    public static StatisticsPlayer getStatisticsPlayer(UUID uuid) {
        return statisticsPlayers.get(uuid);
    }

    public static Collection<StatisticsPlayer> getStatisticsPlayers() {
        return statisticsPlayers.values();
    }
}
