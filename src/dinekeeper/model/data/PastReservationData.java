package dinekeeper.model.data;

import dinekeeper.model.Reservation;
import java.util.HashMap;
import java.util.Map;
import org.joda.time.DateTime;

/** Mutable database containing all fulfilled reservations with their expenses.
 * Invariant: reservations are appended in sorted order based on chronological start time,
 * Fulfilled reservations in the legacy database cannot be removed.
 * */
public class PastReservationData {
    private Map<Reservation, Double> pastReservations;
    private int size;

    public PastReservationData() {
        pastReservations = new HashMap<>();
    }

    public Map<Reservation, Double> getPastReservations() {
        return pastReservations;
    }

    public int getSize() {
        return size;
    }
    public void insert(Reservation r, double bill) {
        pastReservations.put(r, bill);
        size++;
    }

    public double calculateEarnings(DateTime startDate, DateTime endDate) {
        //find first reservation with start time after startDate
        //find last reservation with end time before endDate
        double earnings = 0;
        for (Reservation r : pastReservations.keySet()) {
            if (r.getReservationInterval().getEnd().isAfter(startDate)) earnings += pastReservations.get(r);
            if (r.getReservationInterval().getEnd().isAfter(endDate)) break;
        }
        return earnings;
    }

    public double totalEarnings() {
        double sum = 0;
        for (Double d : pastReservations.values()) sum+= d;
        return sum;
    }
}