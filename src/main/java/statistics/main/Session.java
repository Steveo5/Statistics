package statistics.main;

import org.bukkit.World;

import java.util.Date;
import java.util.UUID;

public class Session {

    private Date started, finished;
    private UUID sessionId;
    private SessionAction currentAction;

    public Session() {
        sessionId = UUID.randomUUID();
    }

    public Date getStarted() {
        return this.started;
    }

    public Date getFinished() {
        return this.finished;
    }

    public void setStarted(Date date) {
        this.started = date;
    }

    public void setFinished(Date date) {
        this.finished = date;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    /**
     * Save to database
     */
    public void save(StatisticsPlayer player) {
        Statistics.getMysqlConnector().getStoreQueries().saveSession(player);
    }

    public void beginAction(SessionActionType type, World world) {

        if(currentAction != null) {
            currentAction.end();
            currentAction.save(this.getSessionId());
        }

        currentAction = new SessionAction(type, world, new Date(), null);
    }

    /**
     * Return the current action the player is performing
     * @return may return null if the user isn't performing an action (such as just walking)
     */
    public SessionAction getAction() {
        return currentAction;
    }

}
