package statistics.main;

import org.bukkit.entity.Player;

import java.util.Date;

public class StatisticsPlayer {

    private Player base;
    private Session session;
    private boolean isAfk;
    private long lastActiveTime = System.currentTimeMillis();

    protected StatisticsPlayer(Player base) {
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

    public boolean isAfk() {
        return this.isAfk;
    }

    public void setAfk(boolean isAfk) {

        // Start new session if the player was afk and now isn't
        if(this.isAfk && !isAfk) {
            this.closeSession();
            Statistics.createSession(this, SessionType.NORMAL);
            System.out.println(this.getBase().getName() + " is now active");
        }

        this.isAfk = isAfk;

        if(!isAfk) {
            this.lastActiveTime = System.currentTimeMillis();
        } else {
            // Start new session if the player is set to afk
            this.closeSession();
            Statistics.createSession(this, SessionType.AFK);
            System.out.println(this.getBase().getName() + " is now afk");
        }
    }

}
