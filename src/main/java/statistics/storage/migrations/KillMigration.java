package statistics.storage.migrations;

import statistics.storage.ConnectionPoolManager;
import statistics.storage.Migration;

public class KillMigration {

    public KillMigration(ConnectionPoolManager pool) {
        Migration migration = new Migration(pool, "kill");
        migration.index("id");
        migration.varchar("user_id", 255, false);
        migration.varchar("world", 255, false);
        migration.varchar("entity_id", 255, false);
        migration.varchar("entity_type", 255, false);
        migration.datetime("created_at", false);
        migration.primaryKey("id");
        migration.run();
    }
}
