package statistics.storage;

import com.google.common.reflect.ClassPath;
import com.sun.rowset.CachedRowSetImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import statistics.main.Session;
import statistics.main.Statistics;
import statistics.main.StatisticsPlayer;

import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

public class MysqlConnector {

    private ConnectionPoolManager pool;
    private java.text.SimpleDateFormat sdf =
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Statistics plugin;

    @SuppressWarnings("UnstableApiUsage")
    public MysqlConnector(Statistics plugin) {
        this.plugin = plugin;
        pool = new ConnectionPoolManager(plugin);

        // Load all migrations via reflection
        try {
            ClassPath path = ClassPath.from(plugin.getClass().getClassLoader());
            for (ClassPath.ClassInfo info : path.getTopLevelClassesRecursive("statistics.storage.migrations")) {
                Class clazz = Class.forName(info.getName(), true, plugin.getClass().getClassLoader());
                clazz.getConstructors()[0].newInstance(pool);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void onDisable() {
        pool.closePool();
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
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
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
        });
    }

    public void saveDeath(Player player) {
        Location loc = player.getLocation();
        String uuid = player.getUniqueId().toString();
        String world = player.getWorld().getUID().toString();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        String damageCause = player.getLastDamageCause().getCause().name();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            saveDeath(uuid, world, damageCause, x, y, z);
        });
    }

    private void saveDeath(String uuid, String world, String damageCause, double x, double y, double z) {
        String sql = "INSERT INTO death (user_id, world, x, y, z, cause, created_at) VALUES (?, ?, ?, ?, ?, ?, ?);";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, uuid);
            stmt.setString(2, world);
            stmt.setDouble(3, x);
            stmt.setDouble(4, y);
            stmt.setDouble(5, z);
            stmt.setString(6, damageCause);
            stmt.setString(7, sdf.format(new Date()));

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, stmt, null);
        }
    }

    public void saveKill(Player player, Entity entity) {
        String uuid = player.getUniqueId().toString();
        String entityUuid = entity.getUniqueId().toString();
        String entityType = entity.getType().name();
        String world = player.getWorld().getUID().toString();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            saveKill(uuid, world, entityUuid, entityType);
        });
    }

    private void saveKill(String uuid, String world, String entityUuid, String entityType) {
        String sql = "INSERT INTO kill (user_id, world, entity_id, entity_type, created_at) VALUES (?, ?, ?, ?, ?);";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, uuid);
            stmt.setString(2, world);
            stmt.setString(3, entityUuid);
            stmt.setString(4, entityType);
            stmt.setString(5, sdf.format(new Date()));

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, stmt, null);
        }
    }

    public void saveBlockBreak(Player player, Block block) {
        String uuid = player.getUniqueId().toString();
        Location location = block.getLocation();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        String world = location.getWorld().getUID().toString();
        String blockType = block.getType().name();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            saveBlockBreak(uuid, world, x, y, z, blockType);
        });
    }

    private void saveBlockBreak(String uuid, String world, double x, double y, double z, String blockType) {
        String sql = "INSERT INTO block_break (user_id, world, x, y, z, block_type, created_at) VALUES (?, ?, ?, ?, ?, ?, ?);";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, uuid);
            stmt.setString(2, world);
            stmt.setDouble(3, x);
            stmt.setDouble(4, y);
            stmt.setDouble(5, z);
            stmt.setString(6, blockType);
            stmt.setString(7, sdf.format(new Date()));

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, stmt, null);
        }
    }

    public void saveBlockPlace(Player player, Block block) {
        String uuid = player.getUniqueId().toString();
        Location location = block.getLocation();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        String world = location.getWorld().getUID().toString();
        String blockType = block.getType().name();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            saveBlockPlace(uuid, world, x, y, z, blockType);
        });
    }

    private void saveBlockPlace(String uuid, String world, double x, double y, double z, String blockType) {
        String sql = "INSERT INTO block_place (user_id, world, x, y, z, block_type, created_at) VALUES (?, ?, ?, ?, ?, ?, ?);";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, uuid);
            stmt.setString(2, world);
            stmt.setDouble(3, x);
            stmt.setDouble(4, y);
            stmt.setDouble(5, z);
            stmt.setString(6, blockType);
            stmt.setString(7, sdf.format(new Date()));

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, stmt, null);
        }
    }

    public void saveMessage(Player player, String message) {
        Location loc = player.getLocation();
        String uuid = player.getUniqueId().toString();
        String world = player.getWorld().getUID().toString();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            saveMessage(uuid, world, x, y, z, message);
        });
    }

    private void saveMessage(String uuid, String world, double x, double y, double z, String message ) {
        String sql = "INSERT INTO message (user_id, world, x, y, z, message, created_at) VALUES (?, ?, ?, ?, ?, ?, ?);";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, uuid);
            stmt.setString(2, world);
            stmt.setDouble(3, x);
            stmt.setDouble(4, y);
            stmt.setDouble(5, z);
            stmt.setString(6, message);
            stmt.setString(7, sdf.format(new Date()));

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
    }

    private CachedRowSet query(String sql) {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(sql);
            CachedRowSet rowset = new CachedRowSetImpl();

            rs = stmt.executeQuery();
            rowset.populate(rs);

            return rowset;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, stmt, rs);
        }

        return null;
    }

    public HashMap<String, HashMap<String, String>> getPing(String uuid) {
        String minSql = "SELECT user_id, ping FROM ping WHERE ping = " +
                "( SELECT MIN(ping) FROM ping " + getWhereClause(uuid) + ")";
        String maxSql = "SELECT user_id, ping FROM ping WHERE ping = " +
                "( SELECT MAX(ping) FROM ping " + getWhereClause(uuid) + ")";
        String avgSql = "SELECT AVG(ping) FROM ping " + getWhereClause(uuid) + ";";

        CachedRowSet minResults = query(minSql);
        CachedRowSet maxResults = query(maxSql);
        CachedRowSet avgResults = query(avgSql);

        HashMap<String, HashMap<String, String>> ping = new HashMap<>();
        HashMap<String, String> min = new HashMap<>(), max = new HashMap<>(), avg = new HashMap<>();;

        try {
            if(minResults.next()) {
                min.put("userId", minResults.getString(1));
                min.put("ping", minResults.getString(2));
            }

            if(maxResults.next()) {
                max.put("userId", maxResults.getString(1));
                max.put("ping", maxResults.getString(2));
            }

            if(avgResults.next()) {
                avg.put("ping", avgResults.getString(1));
            }

            ping.put("min", min);
            ping.put("max", max);
            ping.put("avg", avg);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ping;
    }

    public HashMap<String, HashMap<String, String>> getHoursPlayed(String uuid) {
        String hoursSql = "SELECT SUM(TIME_TO_SEC(TIMEDIFF(logout_time, login_time))/3600) " +
                "total_hours from session " + getWhereClause(uuid);
        String afkSql = "SELECT SUM(TIME_TO_SEC(TIMEDIFF(logout_time, login_time))/3600) " +
                "total_hours from session WHERE type LIKE 'AFK' " +
                (uuid == null || uuid.isEmpty() ? "" : " AND user_id LIKE '" + uuid + "'");
        String minSql = "SELECT user_id, TIME_TO_SEC(TIMEDIFF(logout_time, login_time))/3600 " +
                "FROM session WHERE TIME_TO_SEC(TIMEDIFF(logout_time, login_time))/3600 = " +
                "( SELECT MIN(TIME_TO_SEC(TIMEDIFF(logout_time, login_time))/3600) AS min_hours FROM session " + getWhereClause(uuid) + ")";
        String maxSql = "SELECT user_id, TIME_TO_SEC(TIMEDIFF(logout_time, login_time))/3600 AS hours " +
                "FROM session WHERE TIME_TO_SEC(TIMEDIFF(logout_time, login_time))/3600 = " +
                "( SELECT MAX(TIME_TO_SEC(TIMEDIFF(logout_time, login_time))/3600) AS max_hours FROM session " + getWhereClause(uuid) + ")";
        String avgSql = "SELECT AVG(TIME_TO_SEC(TIMEDIFF(logout_time, login_time))/3600) FROM session " + getWhereClause(uuid) + ";";

        CachedRowSet minResults = query(minSql);
        CachedRowSet maxResults = query(maxSql);
        CachedRowSet avgResults = query(avgSql);

        HashMap<String, HashMap<String, String>> hours = new HashMap<>();
        HashMap<String, String> total = new HashMap<>();
        HashMap<String, String> min = new HashMap<>();
        HashMap<String, String> max = new HashMap<>();
        HashMap<String, String> avg = new HashMap<>();

        CachedRowSet hoursResult = query(hoursSql);
        CachedRowSet afkResult = query(afkSql);

        try {
            if(hoursResult.next()) {
                total.put("all_time", hoursResult.getString(1));
            }

            if(afkResult.next()) {
                total.put("afk", afkResult.getString(1));
            }

            if(minResults.next()) {
                min.put("user", minResults.getString(1));
                min.put("hours", minResults.getString(2));
            }

            if(maxResults.next()) {
                max.put("user", maxResults.getString(1));
                max.put("hours", maxResults.getString(2));
            }

            if(avgResults.next()) {
                avg.put("hours", avgResults.getString(1));
            }

            hours.put("total", total);
            hours.put("min", min);
            hours.put("max", max);
            hours.put("avg", avg);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hours;
    }

    private String getWhereClause(String uuid) {
        return uuid == null || uuid.isEmpty() ? "" : "WHERE user_id LIKE '" + uuid + "';";
    }

}
