package statistics.storage.migrations;

import statistics.storage.ConnectionPoolManager;
import statistics.storage.Migration;

public class PingMigration {

    public static String INSERT = "INSERT INTO ping (user_id, ping, created_at) VALUES (?, ?, ?);";

    public PingMigration(ConnectionPoolManager pool) {
        Migration pingMigration = new Migration(pool, "ping");
        pingMigration.index("id");
        pingMigration.varchar("user_id", 255, false);
        pingMigration.integer("ping", false);
        pingMigration.datetime("created_at", false);
        pingMigration.primaryKey("id");
        pingMigration.run();
    }

}
