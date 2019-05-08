package statistics.main;

import java.util.Date;
import java.util.UUID;

public class Session {

    private Date started, finished;
    private UUID sessionId;
    private SessionType sessionType;

    public Session(SessionType type) {
        sessionId = UUID.randomUUID();
        this.sessionType = type;
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

    public SessionType getType() {
        return this.sessionType;
    }

    /**
     * Save to database
     */
    public void save(StatisticsPlayer player) {
        Statistics.getMysqlConnector().saveSession(player);
    }

}
