package statistics.storage.migrations;

import statistics.storage.ConnectionPoolManager;
import statistics.storage.Migration;

public class PlayerKillMigration {

    public static final String INSERT = "INSERT INTO player_kill (user_id, world, entity_id, entity_type, created_at) VALUES (?, ?, ?, ?, ?);";

    public PlayerKillMigration(ConnectionPoolManager pool) {
        Migration migration = new Migration(pool, "player_kill");
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
