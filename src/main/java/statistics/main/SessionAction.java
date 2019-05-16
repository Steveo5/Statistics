package statistics.main;

import org.bukkit.World;

import java.util.Date;
import java.util.UUID;

public class SessionAction {

    private UUID id;
    private SessionActionType type;
    private World world;
    private Date startTime, endTime;

    protected SessionAction(SessionActionType type, World world, Date startTime, Date endTime) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.world = world;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public UUID getId() {
        return id;
    }

    protected void end() {
        this.endTime = new Date();
    }

    public void save(UUID sessionId) {
        Statistics.getMysqlConnector().getStoreQueries().saveSessionAction(sessionId.toString(), this);
    }

    public SessionActionType getType() {
        return type;
    }

    public World getWorld() {
        return world;
    }

    public Date getStarted() {
        return startTime;
    }

    public Date getEnded() {
        return endTime;
    }

}
