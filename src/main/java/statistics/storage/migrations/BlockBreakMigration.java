package statistics.storage.migrations;

import statistics.storage.ConnectionPoolManager;
import statistics.storage.Migration;

public class BlockBreakMigration {

    public BlockBreakMigration(ConnectionPoolManager pool) {
        Migration migration = new Migration(pool, "block_break");
        migration.index("id");
        migration.varchar("user_id", 255, false);
        migration.varchar("world", 255, false);
        migration.doub("x", false);
        migration.doub("y", false);
        migration.doub("z", false);
        migration.varchar("block_type", 255, false);
        migration.datetime("created_at", false);
        migration.primaryKey("id");
        migration.run();
    }
}
