package dinekeeper.model.data;

import dinekeeper.model.Reservation;
import dinekeeper.model.Table;
import dinekeeper.util.InvalidReservationException;
import java.util.ArrayList;
import java.util.HashMap;
import org.joda.time.Interval;
import java.util.List;
import java.util.Map;

/** Mutable database containing all the confirmed upcoming reservations.
 * Each reservation is assigned with a table with an occupancy >= number of guests.
 * When a reservation is fulfilled (serviced), the entry is removed from the database.
 * Invariant: there can be no duplicate reservations. */
public class ReservationData {
    private Map<Reservation, Table> reservations = new HashMap<>();
    private RestaurantData restaurant;

    public ReservationData(RestaurantData r) {
        restaurant = r;
    }

    /* observers */
    public List<Table> getReservedTables() {
        return new ArrayList(reservations.values());
    }

    public Map<Reservation, Table> getReservations() {
        return reservations;
    }

    /* mutators */
    public void insert(Reservation r, int tableId) {
        reservations.put(r, restaurant.getTable(tableId));
    }

    public void remove(Reservation r) {
        reservations.remove(r);
    }

    public Table getReservationTable(Reservation r) {
        return reservations.get(r);
    }

    /** Check if a table is reserved at a particular timeslot. Returns true if reserved.
     * Requires: t is a valid table
     * */
    public boolean isTableReserved(int tableId, Interval timeInterval) {
        Table t = restaurant.getTable(tableId);
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

    //TODO change to use data structure for O(1) or O(lgn) complexity
    public Reservation getByName(String name) throws InvalidReservationException {
        for (Reservation r : reservations.keySet()) {
            if (r.getName().equals(name)) return r;
        }
        throw new InvalidReservationException();

    }
}
