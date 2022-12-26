package dinekeeper.model;
import java.util.Optional;
import org.joda.time.DateTime;
import org.joda.time.Interval;
public class Reservation {
    /** The time interval of the reservation. */
    private Interval reservationInterval;
    /** The name of the reserver. */
    private String name;
    /** The phone number of the reserver, without parentheses/dashes. E.g. 1234567890*/
    private String phone;
    /** The number of guests for the reservation, including the reservee. */
    private int guests;
    /** Optional information on disability accessibility, dietary restrictions, and/or allergies. */
    private Optional<String> accessibility;
    /** Optional information regarding any additional requests. */
    private Optional<String> misc;
    /** Used to be checked off if reservation has been fulfilled by the restaurant. */
    private boolean isServiced = false;

    /** Creates a 1-hour reservation request at startTime with no additional information. */
    public Reservation(DateTime startTime, String name, String phone, int guests) {
        this.reservationInterval = new Interval(startTime, startTime.plusMinutes(60));
        //this.endTime = startTime;
        this.name = name;
        this.phone = phone;
        this.guests = guests;
    }

    /** Creates a custom reservation with custom information.
     * @param duration length of the reservation, in minutes.
     * */
    public Reservation(DateTime startTime, int duration, String name, String phone, int guests, String accessibility, String misc) {
        this.reservationInterval = new Interval(startTime, startTime.plusMinutes(duration));
        this.name = name;
        this.phone = phone;
        this.guests = guests;
        this.accessibility = Optional.of(accessibility);
        this.misc = Optional.of(misc);
    }

    /* Observers */
    public int getGuests() {
        return guests;
    }

    public Interval getReservationInterval() {
        return reservationInterval;
    }

    /* Mutators (to be used in controller.AvailabilityManager)*/
    public void changeName(String name) {
        this.name = name;
    }

    public void changePhone(String phone) {
        this.phone = phone;
    }

    public void changeGuests(int guests) {
        this.guests = guests;
    }

    public void changeTime(DateTime time, int duration) {
        reservationInterval = new Interval(time, time.plusMinutes(duration));
    }

    public void service() {
        isServiced = true;
    }

}
