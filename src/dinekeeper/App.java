package dinekeeper;

import java.sql.SQLOutput;
import org.joda.time.DateTime;
import org.joda.time.Interval;

public class App {
    /** CLI App. Maybe expand into Swing GUI later. */
    public static void main(String args[]) {
        //initialising mvc

        DateTime dt = new DateTime();
        System.out.println(dt.getHourOfDay());
        DateTime endtime = dt.plusHours(1);
        Interval reservation = new Interval(dt, endtime);
        System.out.println(reservation);
        System.out.println(reservation.getEnd());
    }
}
