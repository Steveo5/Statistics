package statistics.storage;

import org.bukkit.Bukkit;
import statistics.main.Statistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Query {

    public Query(String sql) {
        this(sql, false);
    }

    public Query(String sql, boolean isRetrievingData) {
        Bukkit.getScheduler().runTaskAsynchronously(Statistics.getInstance(), () -> {
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                conn = Statistics.getMysqlConnector().getPool().getConnection();
                stmt = conn.prepareStatement(sql);

                if(isRetrievingData) {
                    run(stmt);
                    rs = stmt.executeQuery();
                    after(rs);
                } else {
                    run(stmt);
                    stmt.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Statistics.getMysqlConnector().getPool().close(conn, stmt, rs);
            }
        });
    }

    public void run(PreparedStatement statement) throws SQLException {}
    public void after(ResultSet rs) throws SQLException {}

}
