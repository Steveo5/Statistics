package statistics.storage;


import com.sun.rowset.CachedRowSetImpl;

import javax.sql.RowSetMetaData;
import javax.sql.rowset.CachedRowSet;
import java.sql.*;
import java.util.HashMap;

public class QueryBuilder {

    private String sql = "";
    private ConnectionPoolManager pool;

    protected QueryBuilder(ConnectionPoolManager pool) {
        this.pool = pool;
    }

    public QueryBuilder raw(String sql) {
        this.sql = sql;
        return this;
    }

    /**
     * Select from a table
     * @param selectValue without the SELECT
     * @return
     */
    public QueryBuilder select(String selectValue) {
        this.sql += ("(SELECT " + selectValue);
        return this;
    }

    public QueryBuilder timediff(String col1, String col2) {
        select(timeToSec(col1, col2));
        return this;
    }

    public QueryBuilder count(String col) {
        select("COUNT(" + col + ") count");
        return this;
    }

    public QueryBuilder sum(String col) {
        select("SUM(" + col + ")");
        return this;
    }

    public QueryBuilder avg(String col) {
        select("AVG(" + col + ") avg");
        return this;
    }

    public QueryBuilder min(String col) {
        select("MIN(" + col + ")");
        return this;
    }

    public QueryBuilder max(String col) {
        select("MAX(" + col + ")");
        return this;
    }

    public QueryBuilder from(String from) {
        this.sql += " FROM " + from;
        return this;
    }

    public QueryBuilder where(String col1, String col2, String sign) {
        if(col1 != null && !col1.isEmpty() && col2 != null && !col2.isEmpty())
            this.sql += " WHERE " + col1 + " " + sign + " " +
                    (sign.equalsIgnoreCase("like") ? "'" + col2 + "'" : col2);
        return this;
    }

    public String timeToSec(String col1, String col2) {
        return "TIME_TO_SEC(TIMEDIFF(" + col1 + ", " + col2 + "))/3600";
    }

    public String get() {
        return sql + ")";
    }

    public QueryBuilder limit(int count) {
        sql += " LIMIT " + count;
        return this;
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

    /**
     * Get the first row that this query retrieves
     * @return as column name and value
     */
    public HashMap<String, String> first() {
        CachedRowSet rs = query(get());
        HashMap<String, String> values = new HashMap<>();

        try {
            if(rs.next()) {
                RowSetMetaData rsmd =  (RowSetMetaData) rs.getMetaData();
                int columnCount = rsmd.getColumnCount();

                for (int i = 1; i <= columnCount; i++ ) {
                    String name = rsmd.getColumnName(i);
                    values.put(name, rs.getString(name));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return values;
    }

}
