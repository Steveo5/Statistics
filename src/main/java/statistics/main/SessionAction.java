package statistics.main;

import org.bukkit.World;

import java.util.Date;
import java.util.UUID;

public class SessionAction {

    private UUID id;
    private SessionActionType type;
    private World world;
    private Date startTime;
    private long endTime;

    protected SessionAction(SessionActionType type, World world, Date startTime, Date endTime) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.world = world;
        this.startTime = startTime;
    }

    public UUID getId() {
        return id;
    }

    public void update() {
        this.endTime = System.currentTimeMillis();
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

    /**
     * Return the date this session ended
     * @return can return null if the session isn't finished yet
     */
    public Date getEnded() {
        return endTime < 1 ? null : new Date(endTime);
    }

}
