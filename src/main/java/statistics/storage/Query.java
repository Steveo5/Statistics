package statistics.storage;

import org.bukkit.Bukkit;
import statistics.main.Statistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Query {

    public Query(String sql) {
        Bukkit.getScheduler().runTaskAsynchronously(Statistics.getInstance(), () -> {
            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                conn = Statistics.getMysqlConnector().getPool().getConnection();
                stmt = conn.prepareStatement(sql);

                run(stmt);

                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Statistics.getMysqlConnector().getPool().close(conn, stmt, null);
            }
        });
    }

    public void run(PreparedStatement statement) throws SQLException {}

}
