package statistics.storage;

import org.bukkit.Bukkit;
import statistics.main.Session;
import statistics.main.Statistics;
import statistics.main.StatisticsPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class MysqlConnector {

    private ConnectionPoolManager pool;
    private java.text.SimpleDateFormat sdf =
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(sessionTableSql);
            stmt.execute(sessionTableSql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, stmt, null);

            createPingTable();
        }

    }

    private void createPingTable() {
        Connection conn = null;
        PreparedStatement stmt = null;

        String pingTableSql = "CREATE TABLE IF NOT EXISTS `ping` (" +
                "`id` INT NOT NULL AUTO_INCREMENT," +
                "`user_id` VARCHAR(255) NOT NULL," +
                "`ping` INT NOT NULL," +
                "`created_at` DATETIME NOT NULL," +
                "PRIMARY KEY (`id`)" +
                ");";

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(pingTableSql);
            stmt.execute(pingTableSql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, stmt, null);
        }
    }

    public void saveSession(StatisticsPlayer player) {

        Session session = player.getSession();

        String started = session.getStarted() == null ? null : sdf.format(session.getStarted());
        String finished = session.getFinished() == null ? sdf.format(new Date()) : sdf.format(session.getFinished());
        String sessionId = session.getSessionId().toString();
        String userId = player.getBase().getUniqueId().toString();
        String ip = player.getBase().getAddress().getAddress().getHostAddress();
        String world =  player.getBase().getWorld().getUID().toString();
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

    public void savePing(String uuid, int ping) {
        String sql = "INSERT INTO ping (user_id, ping, created_at) VALUES (?, ?, ?);";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, uuid);
            stmt.setInt(2, ping);
            stmt.setString(3, sdf.format(new Date()));

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, stmt, null);
        }
    }

    public void reload() {
        pool.closePool();
        pool = new ConnectionPoolManager(plugin);
        this.setupTables();
    }

}
