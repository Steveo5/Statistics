package statistics.main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PingTask extends BukkitRunnable {

    private int getPlayerPing(Player player)
    {
        try
        {
            String a = Bukkit.getServer().getClass().getPackage().getName().substring(23);
            Class<?> b = Class.forName("org.bukkit.craftbukkit." + a + ".entity.CraftPlayer");
            Object c = b.getMethod("getHandle", new Class[0]).invoke(player);
            int d = (Integer) c.getClass().getDeclaredField("ping").get(c);
            return d < 0 ? 0 : d;
        }
        catch (Exception e)
        {

        }

        return -1;
    }

    @Override
    public void run() {
        for(StatisticsPlayer player : Statistics.getStatisticsPlayers()) {
            Statistics.getMysqlConnector().savePing(player.getBase().getUniqueId().toString(),
                    getPlayerPing(player.getBase()));
        }
    }
}
