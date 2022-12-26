package dinekeeper.model.data;

import dinekeeper.model.Reservation;
import dinekeeper.model.Table;
import java.util.ArrayList;
import org.joda.time.Interval;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Mutable database containing all the confirmed upcoming reservations.
 * Each reservation is assigned with a table with an occupancy >= number of guests.
 * When a reservation is fulfilled (serviced), the entry is removed from the database.
 * Invariant: there can be no duplicate reservations. */
public class ReservationData {
    private static Map<Reservation, Table> reservations = new HashMap<>();

    /* observers */
    public static List<Table> getReservedTables() {
        return new ArrayList(reservations.values());
    }

    public static List<Reservation> getReservations() {
        return new ArrayList<>(reservations.keySet());
    }

    /* mutators */
    public static void insert(Reservation r, int tableId) {
        reservations.put(r, RestaurantData.getTable(tableId));
    }

    public static void remove(Reservation r) {
        reservations.remove(r);
    }

    public static Table getReservationTable(Reservation r) {
        return reservations.get(r);
    }

    /** Check if a table is reserved at a particular timeslot. Returns true if reserved.
     * Requires: t is a valid table
     * */
    public static boolean isTableReserved(int tableId, Interval timeInterval) {
        Table t = RestaurantData.getTable(tableId);
        //all reservations for the table
        ArrayList<Reservation> reservationsAtTable = new ArrayList<>();
        for (Map.Entry<Reservation, Table> set : reservations.entrySet()) {
            if (set.getValue().equals(t)) {
                reservationsAtTable.add(set.getKey());
            }
        }
        for (Reservation r : reservationsAtTable) {
            if (r.getReservationInterval().overlaps(timeInterval)) return true;
        }
        return false;
    }
}