package statistics.storage.migrations;

import statistics.storage.ConnectionPoolManager;
import statistics.storage.Migration;

public class SessionMigration {

    public SessionMigration(ConnectionPoolManager pool) {
        Migration sessionMigration = new Migration(pool, "session");
        sessionMigration.varchar("id", 255, false);
        sessionMigration.varchar("user_id", 255, false);
        sessionMigration.datetime("login_time", false);
        sessionMigration.datetime("logout_time", true);
        sessionMigration.varchar("ip", 255, false);
        sessionMigration.varchar("world_id", 255, false);
        sessionMigration.varchar("type", 255, false);
        sessionMigration.primaryKey("id");
        sessionMigration.run();
    }

}
