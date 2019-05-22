package statistics.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static String formatSeconds(long totalSecs) {
        long hours = totalSecs / 3600;
        long minutes = (totalSecs % 3600) / 60;
        long seconds = totalSecs % 60;

        if(minutes < 1) {
            return String.format("%02d seconds", seconds);
        } else if(hours < 1) {
            return String.format("%02d minutes %02d seconds", minutes, seconds);
        } else {
            return String.format("%02d hours %02d minutes %02d seconds", hours, minutes, seconds);
        }
    }

}
