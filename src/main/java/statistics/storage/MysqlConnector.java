package statistics.storage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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

            createDeathTable();
        }
    }

    public void createDeathTable() {
        Connection conn = null;
        PreparedStatement stmt = null;

        String deathTableSql = "CREATE TABLE IF NOT EXISTS `death` (" +
                "`id` INT NOT NULL AUTO_INCREMENT," +
                "`user_id` VARCHAR(255) NOT NULL," +
                "`world` VARCHAR(255) NOT NULL," +
                "`x` DOUBLE NOT NULL," +
                "`y` DOUBLE NOT NULL," +
                "`z` DOUBLE NOT NULL," +
                "`cause` VARCHAR(255) NOT NULL," +
                "`created_at` DATETIME NOT NULL," +
                "PRIMARY KEY (`id`)" +
                ");";

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(deathTableSql);
            stmt.execute(deathTableSql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, stmt, null);

            createKillTable();
        }
    }

    public void createKillTable() {
        Connection conn = null;
        PreparedStatement stmt = null;

        String killTableSql = "CREATE TABLE IF NOT EXISTS `kill` (" +
                "`id` INT NOT NULL AUTO_INCREMENT," +
                "`user_id` VARCHAR(255) NOT NULL," +
                "`world` VARCHAR(255) NOT NULL," +
                "`entity_id` VARCHAR(255) NOT NULL," +
                "`entity_type` VARCHAR(255) NOT NULL," +
                "`created_at` DATETIME NOT NULL," +
                "PRIMARY KEY (`id`)" +
                ");";

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(killTableSql);
            stmt.execute(killTableSql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, stmt, null);

            createBlockBreakTable();
        }
    }

    public void createBlockBreakTable() {
        Connection conn = null;
        PreparedStatement stmt = null;

        String blockTableSql = "CREATE TABLE IF NOT EXISTS `block_break` (" +
                "`id` INT NOT NULL AUTO_INCREMENT," +
                "`user_id` VARCHAR(255) NOT NULL," +
                "`world` VARCHAR(255) NOT NULL," +
                "`x` DOUBLE NOT NULL," +
                "`y` DOUBLE NOT NULL," +
                "`z` DOUBLE NOT NULL," +
                "`block_type` VARCHAR(255) NOT NULL," +
                "`created_at` DATETIME NOT NULL," +
                "PRIMARY KEY (`id`)" +
                ");";

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(blockTableSql);
            stmt.execute(blockTableSql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, stmt, null);

            createBlockPlaceTable();
        }
    }

    public void createBlockPlaceTable() {
        Connection conn = null;
        PreparedStatement stmt = null;

        String blockTableSql = "CREATE TABLE IF NOT EXISTS `block_place` (" +
                "`id` INT NOT NULL AUTO_INCREMENT," +
                "`user_id` VARCHAR(255) NOT NULL," +
                "`world` VARCHAR(255) NOT NULL," +
                "`x` DOUBLE NOT NULL," +
                "`y` DOUBLE NOT NULL," +
                "`z` DOUBLE NOT NULL," +
                "`block_type` VARCHAR(255) NOT NULL," +
                "`created_at` DATETIME NOT NULL," +
                "PRIMARY KEY (`id`)" +
                ");";

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(blockTableSql);
            stmt.execute(blockTableSql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, stmt, null);

            createMessagesTable();
        }
    }

    public void createMessagesTable() {
        Connection conn = null;
        PreparedStatement stmt = null;

        String messageTableSql = "CREATE TABLE IF NOT EXISTS `message` (" +
                "`id` INT NOT NULL AUTO_INCREMENT," +
                "`user_id` VARCHAR(255) NOT NULL," +
                "`world` VARCHAR(255) NOT NULL," +
                "`x` DOUBLE NOT NULL," +
                "`y` DOUBLE NOT NULL," +
                "`z` DOUBLE NOT NULL," +
                "`message` VARCHAR(255) NOT NULL," +
                "`created_at` DATETIME NOT NULL," +
                "PRIMARY KEY (`id`)" +
                ");";

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(messageTableSql);
            stmt.execute(messageTableSql);
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
            stmt.setString(5, sdf.format(new Date()));

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
            stmt.setString(5, sdf.format(new Date()));

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
        this.setupTables();
    }

}
