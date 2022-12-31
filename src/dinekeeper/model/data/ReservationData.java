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
 * Invariant: there can be no duplicate reservations, and only one reservation can be made
 * under one name before it being serviced. */
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
    /** TODO Checks: name is not already with a reservation.*/
    public void insert(Reservation r, int tableId) throws InvalidReservationException {
        for (Reservation res : reservations.keySet()) {
            if (res.getName().equals(r.getName())) throw new InvalidReservationException();
        }
        reservations.put(r, restaurant.getTable(tableId));
    }

    /** TODO Checks: reservation exists. */
    public void remove(Reservation r) throws InvalidReservationException {
        if (reservations.get(r) == null) throw new InvalidReservationException();
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
