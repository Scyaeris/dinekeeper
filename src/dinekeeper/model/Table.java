package dinekeeper.model;
public class Table implements Comparable<Table> {
    /** Unique identifier for the table. */
    private final int id;
    private int occupancy;
    /** Indicates whether this table is available for customers. Should only be false if table is damaged/removed/being cleaned. */
    private boolean availability = true;

    /** Creates a new table with custom occupancy. The table is set to available and not yet serviced by default. */
    public Table(int id, int occupancy) {
        this.id = id;
        this.occupancy = occupancy;
    }

    /*observers*/
    public int getId() {
        return id;
    }

    public int getOccupancy() {
        return occupancy;
    }

    public boolean getAvailability() {
        return availability;
    }

    /*mutators*/

    public void setAvailability(boolean a) {
        availability = a;
    }

    /** Returns the difference between the occupancy of this and o.
     * > 0 if this has greater occupancy.
     * */
    @Override
    public int compareTo(Table o) {
        return this.occupancy - o.occupancy;
    }
}
