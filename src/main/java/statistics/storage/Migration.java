package statistics.storage;

import statistics.main.Statistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Migration {
    private List<String> tableData = new ArrayList<>();
    private ConnectionPoolManager pool;

    public Migration(ConnectionPoolManager pool, String tableName) {
        tableData.add("CREATE TABLE IF NOT EXISTS `" + tableName + "` (");
        this.pool = pool;
    }

    public Migration index(String name) {
        tableData.add("`" + name + "` INT" + nullString(false) + " AUTO_INCREMENT");
        return this;
    }

    public Migration varchar(String name, int length, boolean allowsNull) {
        tableData.add("`" + name + "` VARCHAR(" + length + ")" + nullString(allowsNull));
        return this;
    }

    public Migration integer(String name, boolean allowsNull) {
        tableData.add("`" + name + "` INT" + nullString(allowsNull));
        return this;
    }

    public Migration doub(String name, boolean allowsNull) {
        tableData.add("`" + name + "` DOUBLE" + nullString(allowsNull));
        return this;
    }

    public Migration json(String name) {
        tableData.add("`" + name + "` JSON_TYPE NOT NULL");
        return this;
    }

    public Migration datetime(String name, boolean allowsNull) {
        tableData.add("`" + name + "` DATETIME" + nullString(allowsNull));
        return this;
    }

    public Migration primaryKey(String name) {
        tableData.add("PRIMARY KEY (`" + name + "`)");
        return this;
    }

    /**
     * The null string in MySql query, e.g. NOT NULL
     * @param allowsNull boolean whether to include the string or not
     * @return NOT NULL or empty
     */
    private String nullString(boolean allowsNull) {
        return allowsNull ? "" : " NOT NULL";
    }

    public void run() {
        StringBuilder strBuilder = new StringBuilder();

        for(int i = 0; i<tableData.size(); i++) {
            if(i > 0 && i < tableData.size() - 1) {
                strBuilder.append(tableData.get(i)).append(",");
            } else {
                strBuilder.append(tableData.get(i));
            }
        }

        String sql = strBuilder.toString() + ");";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, stmt, null);
        }
    }
}
