package statistics.main;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

public class SessionTask extends BukkitRunnable {

    @Override
    public void run() {
        Iterator<StatisticsPlayer> pItr = Statistics.getStatisticsPlayers().iterator();
        while(pItr.hasNext()) {
            StatisticsPlayer player = pItr.next();

            if(player == null || player.getBase() == null) {
                try {
                    System.out.println("Player " + player.getId().toString() + " is null");
                    pItr.remove();
                } catch(Exception e) {

                }

                continue;
            } else {
                player.getSession().save(player);
                if(player.getSession().getAction() != null)
                    player.getSession().getAction().save(player.getSession().getSessionId());
            }
        }
    }

}
