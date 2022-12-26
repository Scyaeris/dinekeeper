package dinekeeper.controller;

import dinekeeper.model.Reservation;
import dinekeeper.model.Table;
import dinekeeper.model.data.PastReservationsData;
import dinekeeper.model.data.ReservationData;
import dinekeeper.model.data.RestaurantData;
import dinekeeper.util.InvalidTableAssignmentException;
import java.util.TreeSet;
import org.joda.time.DateTime;

public class ReservationManager {
    //modifies reservations in reservationdata

    public void makeReservation(Reservation r) {
        assignTable(r);
        //TODO prompt if user wants to manually reserve
    }

    /** Cancel an upcoming reservation (r cannot be ) */
    public void cancelReservation(Reservation r) {
        ReservationData.remove(r);
    }

    public void changeGuests(Reservation r, int newGuests) {
        cancelReservation(r);
        r.changeGuests(newGuests);
        makeReservation(r);
    }

    public void changeTime(Reservation r, DateTime newTime, int newDuration) {
        cancelReservation(r);
        r.changeTime(newTime, newDuration);
        makeReservation(r);
    }

    /** Automatically assigns a given reservation r to an available table in the restaurant.
     * Postcondition: r and a table with the lowest >= occupancy will be added to ReservationData.
     * */
    public void assignTable(Reservation r) {
        autoAssign(r);
    }

    /** Manually assign a given reservation r to a table.
     *  @param id id of the Table if wished to be manually assigned.
     *      *           Checks: id of table is valid and is available and not occupied,
     *                  has occupancy >= number of guests in reservation.
     * */
    public void assignTable(Reservation r, int id) {
        try {
            Table t = RestaurantData.getTable(id);
            if (t == null || !t.getAvailability() || t.getOccupancy() < r.getGuests() ||
                    ReservationData.isTableReserved(id, r.getReservationInterval())) {
                throw new InvalidTableAssignmentException();
            }
            ReservationData.insert(r, id);
        } catch (Exception e) {
            //TODO HANDLE
        }
    }

    private void autoAssign(Reservation r) {
        // query table with the least seats larger than r.occupancy
        // add to reservation data
        TreeSet<Table> tables = RestaurantData.getAvailableTables();
        Table temp = new Table(0, r.getGuests());
        ReservationData.insert(r, tables.ceiling(temp).getId());
    }

    public void service(Reservation r, double bill) {
        r.service();
        PastReservationsData.insert(r, bill);
        ReservationData.remove(r);

    }
}