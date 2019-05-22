package statistics.storage.queries;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import statistics.main.Session;
import statistics.main.SessionAction;
import statistics.main.Statistics;
import statistics.main.StatisticsPlayer;
import statistics.storage.Query;
import statistics.storage.migrations.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;


public class StoreQueries {

    private java.text.SimpleDateFormat sdf =
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void savePing(String uuid, int ping) {
        new Query(PingMigration.INSERT) {

            @Override
            public void run(PreparedStatement stmt) throws SQLException {
                stmt.setString(1, uuid);
                stmt.setInt(2, ping);
                stmt.setString(3, sdf.format(new Date()));
            }
        };
    }

    public void saveSession(StatisticsPlayer player) {

        Session session = player.getSession();

        String started = session.getStarted() == null ? null : sdf.format(session.getStarted());
        String finished = session.getFinished() == null ? sdf.format(new Date()) : sdf.format(session.getFinished());
        String sessionId = session.getSessionId().toString();
        String userId = player.getId().toString();
        String ip = player.getOnlineBase().getAddress().getAddress().getHostAddress();
        String world =  player.getOnlineBase().getWorld().getUID().toString();

        this.saveSession(sessionId, userId, started, finished, ip, world);

    }

    public void saveSession(String id, String userId, String loginTime, String logoutTime, String ip, String world) {
        new Query(SessionMigration.INSERT) {
            @Override
            public void run(PreparedStatement stmt) throws SQLException {
                stmt.setString(1, id);
                stmt.setString(2, userId);
                stmt.setString(3, loginTime);
                stmt.setString(4, logoutTime);
                stmt.setString(5, ip);
                stmt.setString(6, world);
                stmt.setString(7, "NORMAL");
                stmt.setString(8, loginTime);
                stmt.setString(9, logoutTime);
            }
        };
    }

    public void saveSessionAction(String sessionId, SessionAction action) {
        if(action == null) return;
        String started = action.getStarted() == null ? null : sdf.format(action.getStarted());
        String finished = action.getEnded() == null ? sdf.format(new Date()) : sdf.format(action.getEnded());

        saveSessionAction(action.getId().toString(),
                sessionId,
                started,
                finished,
                action.getWorld().getUID().toString(),
                action.getType().name());
    }

    public void saveSessionAction(String id, String sessionId, String startTime, String endTime, String worldId, String type) {
        new Query(SessionActionMigration.INSERT) {
            @Override
            public void run(PreparedStatement stmt) throws SQLException {
                stmt.setString(1, id);
                stmt.setString(2, sessionId);
                stmt.setString(3, startTime);
                stmt.setString(4, endTime);
                stmt.setString(5, worldId);
                stmt.setString(6, type);
                stmt.setString(7, startTime);
                stmt.setString(8, endTime);

            }
        };
    }

    public void saveDeath(Player player) {
        Location loc = player.getLocation();
        String uuid = player.getUniqueId().toString();
        String world = player.getWorld().getUID().toString();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        String damageCause = player.getLastDamageCause().getCause().name();

        saveDeath(uuid, world, damageCause, x, y, z);
    }

    public void saveDeath(String uuid, String world, String damageCause, double x, double y, double z) {
        new Query(DeathMigration.INSERT) {
            @Override
            public void run(PreparedStatement stmt) throws SQLException {
                stmt.setString(1, uuid);
                stmt.setString(2, world);
                stmt.setDouble(3, x);
                stmt.setDouble(4, y);
                stmt.setDouble(5, z);
                stmt.setString(6, damageCause);
                stmt.setString(7, sdf.format(new Date()));
            }
        };
    }

    public void saveBlockBreak(Player player, Block block) {
        String uuid = player.getUniqueId().toString();
        Location location = block.getLocation();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        String world = location.getWorld().getUID().toString();
        String blockType = block.getType().name();

        saveBlockBreak(uuid, world, x, y, z, blockType);
    }

    public void saveBlockBreak(String uuid, String world, double x, double y, double z, String blockType) {
        new Query(BlockBreakMigration.INSERT) {
            @Override
            public void run(PreparedStatement stmt) throws SQLException {
                stmt.setString(1, uuid);
                stmt.setString(2, world);
                stmt.setDouble(3, x);
                stmt.setDouble(4, y);
                stmt.setDouble(5, z);
                stmt.setString(6, blockType);
                stmt.setString(7, sdf.format(new Date()));
            }
        };
    }

    public void saveBlockPlace(Player player, Block block) {
        String uuid = player.getUniqueId().toString();
        Location location = block.getLocation();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        String world = location.getWorld().getUID().toString();
        String blockType = block.getType().name();

        saveBlockPlace(uuid, world, x, y, z, blockType);
    }

    public void saveBlockPlace(String uuid, String world, double x, double y, double z, String blockType) {
        new Query(BlockPlaceMigration.INSERT) {
            @Override
            public void run(PreparedStatement stmt) throws SQLException {
                stmt.setString(1, uuid);
                stmt.setString(2, world);
                stmt.setDouble(3, x);
                stmt.setDouble(4, y);
                stmt.setDouble(5, z);
                stmt.setString(6, blockType);
                stmt.setString(7, sdf.format(new Date()));
            }
        };
    }

    public void saveMessage(Player player, String message) {
        Location loc = player.getLocation();
        String uuid = player.getUniqueId().toString();
        String world = player.getWorld().getUID().toString();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        saveMessage(uuid, world, x, y, z, message);
    }

    public void saveMessage(String uuid, String world, double x, double y, double z, String message ) {
        new Query(MessagesMigration.INSERT) {
            @Override
            public void run(PreparedStatement stmt) throws SQLException {
                stmt.setString(1, uuid);
                stmt.setString(2, world);
                stmt.setDouble(3, x);
                stmt.setDouble(4, y);
                stmt.setDouble(5, z);
                stmt.setString(6, message);
                stmt.setString(7, sdf.format(new Date()));
            }
        };
    }

    public void saveKill(Player player, Entity entity) {
        String uuid = player.getUniqueId().toString();
        String entityUuid = entity.getUniqueId().toString();
        String entityType = entity.getType().name();
        String world = player.getWorld().getUID().toString();

        Bukkit.getScheduler().runTaskAsynchronously(Statistics.getInstance(),
                () -> saveKill(uuid, world, entityUuid, entityType));
    }

    public void saveKill(String uuid, String world, String entityUuid, String entityType) {
        new Query(PlayerKillMigration.INSERT) {
            @Override
            public void run(PreparedStatement stmt) throws SQLException {
                stmt.setString(1, uuid);
                stmt.setString(2, world);
                stmt.setString(3, entityUuid);
                stmt.setString(4, entityType);
                stmt.setString(5, sdf.format(new Date()));
            }
        };
    }

}
