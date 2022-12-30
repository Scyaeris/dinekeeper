package dinekeeper.model.data;

import dinekeeper.model.Table;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/** Mutable database containing all tables in the restaurant.
 * Each reservation is assigned with a table with an occupancy >= number of guests.
 * When a reservation is fulfilled (serviced), the entry is removed from the database.
 * Invariant: there can be no duplicate reservations per table or vice versa. */
public class RestaurantData {
    private Map<Integer, Table> tables = new HashMap<>();

    //return sorted tables based on ascending occupancy
    /** Returns a set of available tables in sorted order based on
     * ascending occupancy. E.g. Table with 1 seat, then 2 seats, then 3, etc. */
    public TreeSet<Table> getAvailableTables() {
        TreeSet<Table> availableTables = new TreeSet<>();
        for (Table t : tables.values()) {
            if (t.getAvailability()) availableTables.add(t);
        }
        return availableTables;
    }

    public Map<Integer, Table> getTables() {
        return tables;
    }

    public void insert(Table t) {
        tables.put(t.getId(), t);
    }

    public  void remove(int id) {
        tables.remove(id);
    }

    public Table getTable(int id) {
        return tables.get(id);
    }

    public boolean checkAvailability(int id) {
        return getTable(id).getAvailability();
    }
}
