package statistics.storage.migrations;

import statistics.storage.ConnectionPoolManager;
import statistics.storage.Migration;

public class SessionActionMigration {

    public static final String INSERT = "INSERT INTO session_action (id, session_id, start_time, end_time, world_id, type)" +
            "VALUES (?, ?, ?, ?, ?, ?)" +
            "ON DUPLICATE KEY UPDATE" +
            " start_time = ?, " +
            "end_time = ?;";

    public SessionActionMigration(ConnectionPoolManager pool) {
        Migration sessionActionMigration = new Migration(pool, "session_action");
        sessionActionMigration.varchar("id", 255, false);
        sessionActionMigration.varchar("session_id", 255, false);
        sessionActionMigration.datetime("start_time", false);
        sessionActionMigration.datetime("end_time", true);
        sessionActionMigration.varchar("world_id", 255, false);
        sessionActionMigration.varchar("type", 255, false);
        sessionActionMigration.primaryKey("id");
        sessionActionMigration.run();
    }

}
