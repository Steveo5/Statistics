package statistics.main;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

public class SessionSaveTask extends BukkitRunnable {

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
                    e.printStackTrace();
                }
            } else {
                player.getSession().save();
            }
        }
    }

}
