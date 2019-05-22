package statistics.main;

import org.bukkit.World;
import statistics.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Session {

    private Date started, finished;
    private UUID sessionId;
    private SessionAction currentAction;
    private List<SessionAction> previousActions;
    private StatisticsPlayer player;

    public Session(StatisticsPlayer player) {
        previousActions = new ArrayList<>();
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

        // Save history as well
        for(SessionAction sessionAction : getHistory()) {
            sessionAction.save(this.getSessionId());
        }
    }

    public void beginAction(SessionActionType type, World world) {

        if(currentAction != null) {
            stopAction();
        }

        System.out.println("Player " + player.getBase().getName() + " started " + type.name());
        currentAction = new SessionAction(type, world, new Date(), null);
        previousActions.add(currentAction);
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

    /**
     * Get all previously performed session actions in this session
     * @return SessionAction arraylist
     */
    public List<SessionAction> getHistory() {
        return previousActions;
    }

    /**
     * Add session actions to the history
     * @param history
     */
    public void addHistory(List<SessionAction> history) {
        this.previousActions.addAll(history);
    }

    public long timeBetween() {
        return DateUtil.getDateDiff(getStarted(), getFinished(), TimeUnit.SECONDS);
    }

    /**
     * Get the time between start and finish excluding these session action types
     * @param filterType
     * @return
     */
    public long timeBetween(SessionActionType... filterType) {
        return 0;
    }

}
