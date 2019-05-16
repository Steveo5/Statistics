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
    private HashMap<SessionActionType, Long> lastActiveTimes = new HashMap<>();

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
        session.save(this);
    }

    public long getLastActiveTime() {
        return this.lastActiveTime;
    }

    public long getLastActiveTime(SessionActionType sessionType) {
        return lastActiveTimes.get(sessionType);
    }

    public void setLastActiveTime(SessionActionType sessionType, long time) {
        lastActiveTimes.put(sessionType, time);
    }

    public boolean isAfk() {
        return this.isAfk;
    }

    public void setAfk(boolean isAfk) {

        // Start new session action if the player was afk and now isn't
        if(this.isAfk && !isAfk) {
            getSession().getAction().end();
            System.out.println(this.getBase().getName() + " is now active");
        }

        this.isAfk = isAfk;

        if(!isAfk) {
            this.lastActiveTime = System.currentTimeMillis();
        } else {
            // Start new session if the player is set to afk
            getSession().beginAction(SessionActionType.AFK, getBase().getWorld());
            //Statistics.createSession(this, SessionType.AFK);
            System.out.println(this.getBase().getName() + " is now afk");
        }
    }

    public UUID getId() {
        return id;
    }

}
