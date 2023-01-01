package dinekeeper.model;
import java.util.Optional;
import java.util.function.Predicate;
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

    private int duration = 60;

    /** Creates a custom reservation with custom information.
     * @param duration length of the reservation, in minutes.
     * */
    public Reservation(DateTime startTime, int duration, String name, String phone, int guests, String accessibility, String misc) {
        this.reservationInterval = new Interval(startTime, startTime.plusMinutes(duration));
        this.name = name;
        this.phone = phone;
        this.guests = guests;
        this.accessibility = Optional.ofNullable(accessibility).filter(Predicate.not(String::isEmpty));
        this.misc = Optional.ofNullable(misc).filter(Predicate.not(String::isEmpty));
        this.duration = duration;
    }

    /* Observers */
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public DateTime getStartTime() {
        //TODO Format
        return reservationInterval.getStart();
    }

    public int getDuration() {
        return duration;
    }
    public int getGuests() {
        return guests;
    }

    public Interval getReservationInterval() {
        return reservationInterval;
    }

    public String getAccessibility() {
        return accessibility.orElse("N/A");
    }

    public String getMisc() {
        return misc.orElse("N/A");
    }

    /* Mutators (to be used in controller.AvailabilityManager)*/

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

    public void changeAccessibility(String acc) {
        this.accessibility = Optional.of(acc);
    }

    public void changeMisc(String misc) {
        this.misc = Optional.of(misc);
    }
}
