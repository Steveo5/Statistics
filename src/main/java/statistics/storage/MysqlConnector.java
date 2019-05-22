package statistics.storage;

import com.google.common.reflect.ClassPath;
import com.sun.rowset.CachedRowSetImpl;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import statistics.main.*;
import statistics.storage.queries.StoreQueries;
import statistics.util.DateUtil;

import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MysqlConnector {

    private ConnectionPoolManager pool;
    private java.text.SimpleDateFormat sdf =
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Statistics plugin;
    private StoreQueries storeQueries;

    @SuppressWarnings("UnstableApiUsage")
    public MysqlConnector(Statistics plugin) {
        this.plugin = plugin;
        pool = new ConnectionPoolManager(plugin);
        storeQueries = new StoreQueries();

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

    public ConnectionPoolManager getPool() {
        return pool;
    }

    public StoreQueries getStoreQueries() {
        return storeQueries;
    }

    public void onDisable() {
        pool.closePool();
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
        HashMap<String, HashMap<String, String>> ping = new HashMap<>();

        String minPing = createQuery().min("ping").from("ping").where("user_id", uuid, "LIKE").get();
        QueryBuilder minPingQ = createQuery().select("user_id, ping").from("ping").where("ping", minPing, "=");

        String maxPing = createQuery().max("ping").from("ping").where("user_id", uuid, "LIKE").get();
        QueryBuilder maxPingQ = createQuery().select("user_id, ping").from("ping").where("ping", maxPing, "=");

        QueryBuilder avgPingQ = createQuery().avg("ping").from("ping").where("user_id", uuid, "LIKE");

        ping.put("min", minPingQ.first());
        ping.put("max", maxPingQ.first());
        ping.put("avg", avgPingQ.first());

        return ping;
    }

    public void getSessions(NumberedListCallback callback, OfflinePlayer byPlayer) {

        final NumberedList<Session> sessions = new NumberedList<>();

        QueryBuilder queryBuilder = new QueryBuilder(pool).select("*").from("session");

        if(byPlayer != null) queryBuilder.where("user_id", byPlayer.getUniqueId().toString(), "LIKE");

        new Query(queryBuilder.get(), true) {

            @Override
            public void after(ResultSet rs) throws SQLException {
                while(rs.next()) {
                    Session session = new Session(Statistics.getOfflineStatisticsPlayer(UUID.fromString(rs.getString(2))));
                    session.setStarted(new Date(rs.getTimestamp(3).getTime()));
                    session.setFinished(new Date(rs.getTimestamp(4).getTime()));

                    sessions.add(DateUtil.getDateDiff(session.getStarted(), session.getFinished(), TimeUnit.SECONDS), session);

                    // Get the session actions
                    new Query(new QueryBuilder(pool)
                            .select("*")
                            .from("session_action")
                            .where("session_id", session.getSessionId().toString(), "LIKE").get()) {

                        @Override
                        public void after(ResultSet rs) throws SQLException {
                            List<SessionAction> sessionActions = new ArrayList<SessionAction>();

                            while(rs.next()) {
                                SessionAction action = new SessionAction(UUID.fromString(rs.getString(1)),
                                        SessionActionType.valueOf(rs.getString(6)),
                                        Bukkit.getWorld(UUID.fromString(rs.getString(5))),
                                        new Date(rs.getTimestamp(3).getTime()),
                                        new Date(rs.getTimestamp(4).getTime()));

                                sessionActions.add(action);
                            }

                            session.addHistory(sessionActions);
                        }
                    };
                }

                callback.call(sessions);
            }
        };

    }

    public HashMap<String, HashMap<String, String>> getHoursPlayed(String uuid) {

        String hoursSql = "SELECT SUM(TIME_TO_SEC(TIMEDIFF(logout_time, login_time))/3600) " +
                "total_hours from session " + getWhereClause(uuid, false);
        String afkSql = "SELECT SUM(TIME_TO_SEC(TIMEDIFF(logout_time, login_time))/3600) " +
                "total_hours from session WHERE type LIKE 'AFK' " +
                (uuid == null || uuid.isEmpty() ? "" : " AND user_id LIKE '" + uuid + "'");
        String minSql = "SELECT user_id, TIME_TO_SEC(TIMEDIFF(logout_time, login_time))/3600 " +
                "FROM session WHERE TIME_TO_SEC(TIMEDIFF(logout_time, login_time))/3600 = " +
                "( SELECT MIN(TIME_TO_SEC(TIMEDIFF(logout_time, login_time))/3600) AS min_hours FROM session " + getWhereClause(uuid, false) + ")";
        String maxSql = "SELECT user_id, TIME_TO_SEC(TIMEDIFF(logout_time, login_time))/3600 AS hours " +
                "FROM session WHERE TIME_TO_SEC(TIMEDIFF(logout_time, login_time))/3600 = " +
                "( SELECT MAX(TIME_TO_SEC(TIMEDIFF(logout_time, login_time))/3600) AS max_hours FROM session " + getWhereClause(uuid, false) + ")";
        String avgSql = "SELECT AVG(TIME_TO_SEC(TIMEDIFF(logout_time, login_time))/3600) FROM session " + getWhereClause(uuid, false) + ";";

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

    public HashMap<String, HashMap<String, String>> getBlocksPlaced(String uuid) {
        HashMap<String, HashMap<String, String>> blocks = new HashMap<>();

        QueryBuilder blocksPlacedCount = createQuery()
                .count("user_id")
                .from("block_place")
                .where("user_id", uuid, "LIKE");


//        int blocksPlacedCount = Integer.parseInt(blocksPlaced.first().get("count"));
//        int blocksBrokenCount = Integer.parseInt(blocksBroken.first().get("count"));


        return blocks;
    }

    private String getWhereClause(String uuid, boolean useAnd) {
        return uuid == null || uuid.isEmpty() ? "" : (useAnd ? "AND" : "WHERE") + " user_id LIKE '" + uuid + "'";
    }

    public QueryBuilder createQuery() {
        return new QueryBuilder(pool);
    }

}
