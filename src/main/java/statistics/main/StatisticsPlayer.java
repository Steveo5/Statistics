package statistics.main;

import org.bukkit.entity.Player;

import java.util.Date;

public class StatisticsPlayer {

    private Player base;
    private Session session;

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

}
