package statistics.storage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import statistics.main.Session;
import statistics.main.Statistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class MysqlConnector {

    private ConnectionPoolManager pool;

    private Statistics plugin;

    public MysqlConnector(Statistics plugin) {
        this.plugin = plugin;
        pool = new ConnectionPoolManager(plugin);
        this.setupTables();
    }

    public void onDisable() {
        pool.closePool();
    }


    public void setupTables() {
        String sessionTableSql = "CREATE TABLE IF NOT EXISTS `session` (" +
                "`id` VARCHAR(255) NOT NULL," +
                "`user_id` VARCHAR(255) NOT NULL," +
                "`login_time` DATETIME NOT NULL," +
                "`logout_time` DATETIME," +
                "`ip` VARCHAR(255) NOT NULL," +
                "`world_id` VARCHAR(255) NOT NULL," +
                "`type` VARCHAR(255) NOT NULL," +
                "PRIMARY KEY (`id`)" +
                ");";

        try {
            pool.getConnection().createStatement().execute(sessionTableSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveSession(Session session) {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String started = session.getStarted() == null ? null : sdf.format(session.getStarted());
        String finished = session.getFinished() == null ? sdf.format(new Date()) : sdf.format(session.getFinished());
        String sessionId = session.getSessionId().toString();
        String userId = session.getPlayer().getUniqueId().toString();
        String ip = ((Player) session.getPlayer()).getAddress().getAddress().getHostAddress();
        String world = ((Player) session.getPlayer()).getWorld().getUID().toString();
        String type = session.getType().toString();

        Bukkit.getScheduler().runTaskAsynchronously(plugin,
                () -> Statistics.getMysqlConnector().saveSession(sessionId, userId, started, finished, ip, world, type));

    }

    public void saveSession(String id,
                            String userId,
                            String loginTime,
                            String logoutTime,
                            String ip,
                            String world,
                            String type) {

        String sql = "INSERT INTO session (id, user_id, login_time, logout_time, ip, world_id, type)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?)" +
                "ON DUPLICATE KEY UPDATE" +
                " login_time = ?, " +
                "logout_time = ?;";

        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = pool.getConnection();
            statement = conn.prepareStatement(sql);

            statement.setString(1, id);
            statement.setString(2, userId);
            statement.setString(3, loginTime);
            statement.setString(4, logoutTime);
            statement.setString(5, ip);
            statement.setString(6, world);
            statement.setString(7, type);
            statement.setString(8, loginTime);
            statement.setString(9, logoutTime);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, statement, null);
        }
    }

    public void reload() {
        pool.closePool();
        pool = new ConnectionPoolManager(plugin);
        this.setupTables();
    }

}
