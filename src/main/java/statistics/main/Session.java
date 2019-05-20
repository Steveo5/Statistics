package statistics.main;

import org.bukkit.World;

import java.util.Date;
import java.util.UUID;

public class Session {

    private Date started, finished;
    private UUID sessionId;
    private SessionAction currentAction;
    private StatisticsPlayer player;

    public Session(StatisticsPlayer player) {
        sessionId = UUID.randomUUID();
        this.player = player;
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
    public void save() {
        Statistics.getMysqlConnector().getStoreQueries().saveSession(player);
    }

    public void beginAction(SessionActionType type, World world) {

        if(currentAction != null) {
            stopAction();
        }

        System.out.println("Player " + player.getBase().getName() + " started " + type.name());
        currentAction = new SessionAction(type, world, new Date(), null);
    }

    public void stopAction() {

        System.out.println("Player " + player.getBase().getName() + " stopped " + currentAction.getType().name());

        currentAction.update();
        currentAction.save(player.getId());
        currentAction = null;
    }

    /**
     * Return the current action the player is performing
     * @return may return null if the user isn't performing an action (such as just walking)
     */
    public SessionAction getAction() {
        return currentAction;
    }

    /**
     * Check if the player is doing a specific session action
     * @param type the session action type enum
     * @return boolean
     */
    public boolean isDoing(SessionActionType type) {
        return player.getSession().getAction() != null && type == getAction().getType();
    }

    /**
     * Check if the player is performing any known action
     * @return boolean
     */
    public boolean isDoing() {
        return player.getSession().getAction() != null;
    }

}
