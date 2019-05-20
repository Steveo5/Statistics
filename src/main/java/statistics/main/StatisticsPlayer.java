package statistics.main;

import org.bukkit.entity.Player;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class StatisticsPlayer {

    private Player base;
    private UUID id;
    private Session session;
    private boolean isAfk;
    private long lastActiveTime = System.currentTimeMillis();
    protected HashMap<SessionActionType, Long> lastActiveTimes = new HashMap<>();

    protected StatisticsPlayer(Player base) {
        this.id = base.getUniqueId();
        this.base = base;
    }

    public Player getBase() {
        return this.base;
    }

    protected void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return this.session;
    }

    public void closeSession() {
        session.setFinished(new Date());
        session.save();
    }

    public long getLastActiveTime() {
        return this.lastActiveTime;
    }

    // Get the time the player last did an action for this session actiont ype
    public long getLastActiveTime(SessionActionType sessionType) {
        return lastActiveTimes.get(sessionType);
    }

    public void setLastActiveTime(SessionActionType sessionType, long time) {
        lastActiveTimes.put(sessionType, time);
    }

    // Check whether the user is no longer performing this action based on a timeout
    public boolean isInactive(SessionActionType sessionType, int timeout) {
        return ((System.currentTimeMillis() - lastActiveTimes.get(sessionType)) / 1000) >= timeout;
    }

    public boolean isAfk() {
        return this.isAfk;
    }

    public void setAfk(boolean isAfk) {

        // Start new session action if the player was afk and now isn't
        if(this.isAfk && !isAfk) {
            getSession().stopAction();
        }

        this.isAfk = isAfk;

        if(!isAfk) {
            this.lastActiveTime = System.currentTimeMillis();
        } else {
            // Start new session if the player is set to afk
            getSession().beginAction(SessionActionType.AFK, getBase().getWorld());
        }
    }

    public UUID getId() {
        return id;
    }

}
