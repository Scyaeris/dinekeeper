package dinekeeper.model.data;

import dinekeeper.model.Reservation;
import java.util.Map;
import org.joda.time.DateTime;

/** Mutable database containing all fulfilled reservations with their expenses.
 * Invariant: reservations are appended in sorted order based on chronological start time,
 * Fulfilled reservations in the legacy database cannot be removed.
 * */
public class PastReservationsData {
    private static Map<Reservation, Double> pastReservations;
    private static int size;

    public static int getSize() {
        return size;
    }
    public static void insert(Reservation r, double bill) {
        pastReservations.put(r, bill);
        size++;
    }

    public static int calculateEarnings(DateTime startDate, DateTime endDate) {
        //find first reservation with start time after startDate
        //find last reservation with end time before endDate
        int earnings = 0;
        for (Reservation r : pastReservations.keySet()) {
            if (r.getReservationInterval().getEnd().isAfter(startDate)) earnings += pastReservations.get(r);
            if (r.getReservationInterval().getEnd().isAfter(endDate)) break;
        }
        return earnings;
    }
}