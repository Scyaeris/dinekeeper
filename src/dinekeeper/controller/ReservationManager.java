package dinekeeper.controller;

import dinekeeper.model.Reservation;
import dinekeeper.model.Table;
import dinekeeper.model.data.PastReservationData;
import dinekeeper.model.data.ReservationData;
import dinekeeper.model.data.RestaurantData;
import dinekeeper.util.InvalidReservationException;
import dinekeeper.util.InvalidTableAssignmentException;
import dinekeeper.view.CalendarView;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Predicate;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/** A controller that manages usage and storage in the list of upcoming reservations. */
public class ReservationManager {
    //modifies reservations in reservationdata
    ReservationData reservations;
    PastReservationData pastReservations;
    private RestaurantData restaurant;
    CalendarView view;
    DefaultTableModel dtm = new DefaultTableModel(null,
            new String[]{"Name", "Phone", "Guests", "Time", "Duration", "Accessibility", "Misc", "Table"});

    public ReservationManager(CalendarView v, RestaurantData restaurant) {
        reservations = new ReservationData(restaurant);
        pastReservations = new PastReservationData();
        this.restaurant = restaurant;
        this.view = v;
        initializeTableView();
        view.createTable(dtm);
        addListeners();
    }

    public void makeReservation(Reservation r) {
        //TODO prompt if user wants to manually reserve
        int manualAssign = JOptionPane.showOptionDialog(null, "Automatically assign table?", "Table Assignment"
                , JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Yes", "No"}, "Yes");
        if (manualAssign == 1) {
            int tableNo = Integer.parseInt(
                    JOptionPane.showInputDialog("Table Id to Assign: "));
            assignTable(r, tableNo);
        } else {
            autoAssign(r);
        }
    }

    /** Remove a reservation, either due to cancelling or completion. */
    public void removeReservation(Reservation r) {
        reservations.remove(r);
        //dtm.removeRow(); TODO
    }

    public void changeGuests(Reservation r, int newGuests) {
        removeReservation(r);
        r.changeGuests(newGuests);
        makeReservation(r);
    }

    public void changeTime(Reservation r, DateTime newTime, int newDuration) {
        removeReservation(r);
        r.changeTime(newTime, newDuration);
        makeReservation(r);
    }

    /** Automatically assigns a given reservation r to an available table in the restaurant.
     * Postcondition: r and a table with the lowest >= occupancy will be added to ReservationData.
     * */
    private void autoAssign(Reservation r) {
        // query table with the least seats larger than r.occupancy
        // add to reservation data
        TreeSet<Table> tables = restaurant.getAvailableTables();
        if (tables.isEmpty()) {
            try {
                throw new InvalidTableAssignmentException();
            } catch (InvalidTableAssignmentException e) {
                JOptionPane.showMessageDialog(null, "There are no available tables", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            Table temp = new Table(0, r.getGuests());
            Table t = tables.ceiling(temp);
            reservations.insert(r, t.getId());
            dtm.addRow(formatRow(r, t));
        }
    }

    /** Manually assign a given reservation r to a table.
     *  @param id id of the Table if wished to be manually assigned.
     *      *           Checks: id of table is valid and is available and not occupied,
     *                  has occupancy >= number of guests in reservation.
     * */
    private void assignTable(Reservation r, int id) {
        try {
            Table t = restaurant.getTable(id);
            if (t == null || !t.getAvailability() || t.getOccupancy() < r.getGuests() ||
                    reservations.isTableReserved(id, r.getReservationInterval())) {
                throw new InvalidTableAssignmentException();
            }
            reservations.insert(r, id);
            dtm.addRow(formatRow(r, t));
        } catch (InvalidTableAssignmentException e) {
            JOptionPane.showMessageDialog(null, "This table is reserved during this time.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void service(Reservation r, double bill) {
        r.service();
        pastReservations.insert(r, bill);
        reservations.remove(r);

    }

    public double calculateEarnings(DateTime start, DateTime end) {
        return pastReservations.calculateEarnings(start, end);
    }

    /** Formats reservation data into tabular form for GUI. */
    private void initializeTableView() {
        Map<Reservation, Table> map = reservations.getReservations();
        for (Map.Entry<Reservation, Table> set : map.entrySet()) {
            dtm.addRow(formatRow(set.getKey(), set.getValue()));
        }
    }

    private Object[] formatRow(Reservation r, Table t) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm MM/dd");
        String dt = r.getStartTime().toString(dtf);
        return new Object[] {r.getName(), r.getPhone(), r.getGuests(), dt,
                r.getDuration(), r.getAccessibility(), r.getMisc(), t.getId()};
    }

    //gui listeners
    public void addListeners() {
        view.addTableListener(e -> {
        if (e.getType() == TableModelEvent.UPDATE) {
            String name = (String) dtm.getValueAt(e.getFirstRow(), 0);
            String col = String.valueOf(e.getColumn());
            String update = String.valueOf(dtm.getValueAt(e.getFirstRow(), e.getColumn()));
            if (col.equals("Phone")) {
                try {
                    reservations.getByName(name).changePhone(update);
                } catch (InvalidReservationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        });
        view.addServiceListener(e -> {
            int row = view.selectedRow();
            if (row != -1) {
                try {
                    double bill = Double.parseDouble(JOptionPane.showInputDialog("Total bill: "));
                    service(reservations.getByName((String) dtm.getValueAt(row, 0)), bill);
                } catch (InvalidReservationException ex) {}
            }
        });

        view.addRemoveListener(e -> {
            String n = JOptionPane.showInputDialog("Name: ");
            try {
                removeReservation(reservations.getByName(n));
            } catch (InvalidReservationException ex) {
                JOptionPane.showMessageDialog(null, "Reservation not found. ", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        view.addAddListener(e -> {
            JTextField name = new JTextField();
            JTextField phone = new JTextField();
            JTextField guests = new JTextField();
            JTextField startTime = new JTextField();
            JTextField duration = new JTextField();
            JTextField accessibility = new JTextField();
            JTextField misc = new JTextField();
            Object[] message = {
                    "Name", name,
                    "Phone", phone,
                    "Number of Guests", guests,
                    "Time (HH:mm MM/dd/yyyy)", startTime,
                    "Duration (in minutes, 1 hour default)", duration,
                    "Accessibility/Allergies Information (Optional)", accessibility,
                    "Any additional info (Optional)", misc,
            };
            int option = JOptionPane.showConfirmDialog(null, message, "Enter all your values", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION)
            {
                String rName = name.getText();
                String rPhone = phone.getText();
                int rGuests = Integer.parseInt(guests.getText());
                DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm MM/dd/yyyy");
                DateTime rStart = dtf.parseDateTime(startTime.getText());
                Optional<String> dur = Optional.ofNullable(duration.getText()).filter(Predicate.not(String::isEmpty));
                int rDuration = Integer.parseInt((dur.orElse("60")));
                //Interval rInterval = new Interval(rStart, rStart.plusMinutes(rDuration.orElse(60)));
                String rAcc = accessibility.getText();
                String rMisc = misc.getText();

                Reservation r = new Reservation(rStart, rDuration, rName, rPhone, rGuests, rAcc, rMisc);
                makeReservation(r);
            }
        });
    }
}