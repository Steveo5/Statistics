package statistics.storage.migrations;

import statistics.storage.ConnectionPoolManager;
import statistics.storage.Migration;

public class MessagesMigration {

    public static final String INSERT = "INSERT INTO message (user_id, world, x, y, z, message, created_at) VALUES (?, ?, ?, ?, ?, ?, ?);";

    public MessagesMigration(ConnectionPoolManager pool) {
        Migration migration = new Migration(pool, "message");
        migration.index("id");
        migration.varchar("user_id", 255, false);
        migration.varchar("world", 255, false);
        migration.doub("x", false);
        migration.doub("y", false);
        migration.doub("z", false);
        migration.varchar("message", 255, false);
        migration.datetime("created_at", false);
        migration.primaryKey("id");
        migration.run();
    }

}
