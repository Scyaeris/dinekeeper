package dinekeeper.controller;

import dinekeeper.model.Reservation;
import dinekeeper.model.Table;
import dinekeeper.model.data.PastReservationData;
import dinekeeper.model.data.ReservationData;
import dinekeeper.model.data.RestaurantData;
import dinekeeper.util.InvalidReservationException;
import dinekeeper.util.InvalidTableAssignmentException;
import dinekeeper.view.CalendarView;
import dinekeeper.view.LedgerView;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
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
    private ReservationData reservations;
    private PastReservationData pastReservations;
    private RestaurantData restaurant;
    private CalendarView view;
    private LedgerView ledgerView;
    DefaultTableModel dtm = new DefaultTableModel(null,
            new String[]{"Name", "Phone", "Guests", "Time", "Duration", "Accessibility", "Misc", "Table"}) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return (column != 0 && column != 7);
        }
    };

    DefaultTableModel dtmLedger = new DefaultTableModel(null, new String[] {"Name", "Time", "Bill"}) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    /** Stores the names of each reservation for faster lookup/table updates. Indices are rows. */
    private LinkedList<String> names = new LinkedList<>();
    private static DateTimeFormatter dtfNoYear = DateTimeFormat.forPattern("HH:mm MM/dd");
    private static DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm MM/dd/YYYY");
    private static DateTimeFormatter dtfNoHour = DateTimeFormat.forPattern("MM/dd/YYYY");

    public ReservationManager(CalendarView v, LedgerView lv, RestaurantData restaurant, ReservationData r, PastReservationData pr) {
        reservations = r;
        pastReservations = pr;
        this.restaurant = restaurant;
        this.view = v;
        this.ledgerView = lv;
        initializeTableView();
        view.createTable(dtm);
        initializeLedgerTableView();
        ledgerView.createTable(dtmLedger);
        addCalendarListeners();
        addLedgerListeners();
    }

    /** Add a new reservation to the dataset. Handles both model and view actions. */
    private void makeReservation(Reservation r) {
        int manualAssign = JOptionPane.showOptionDialog(null, "Automatically assign table?", "Table Assignment"
                , JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Yes", "No"}, "Yes");
        try {
            if (manualAssign == 1) {
                int tableNo = Integer.parseInt(
                        JOptionPane.showInputDialog("Table Id to Assign: "));
                assignTable(r, tableNo);
            } else {
                autoAssign(r);
            }
        } catch (InvalidReservationException e) {
            JOptionPane.showMessageDialog(null, "Reservation under name already exists.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        names.add(r.getName());
        view.updateSize(reservations.size());
    }

    /** Remove a reservation under the name n, either due to cancelling or completion.
     *  Handles both model and view actions.*/
    private void removeReservation(String n) throws InvalidReservationException {
        reservations.remove(reservations.getByName(n));
        dtm.removeRow(names.indexOf(n));
        names.remove(n);
        view.updateSize(reservations.size());
    }

    private void changeGuests(String n, int newGuests) throws InvalidReservationException {
        Reservation newR = reservations.getByName(n);
        newR.changeGuests(newGuests);
        removeReservation(n);
        makeReservation(newR);
    }

    private void changeTime(String n, DateTime newTime, int newDuration)
            throws InvalidReservationException {
        Reservation newR = reservations.getByName(n);
        newR.changeTime(newTime, newDuration);
        removeReservation(n);
        makeReservation(newR);
    }

    /** Automatically assigns a given reservation r to an available table in the restaurant.
     * Postcondition: r and a table with the lowest >= occupancy will be added to ReservationData.
     * */
    private void autoAssign(Reservation r) throws InvalidReservationException {
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
    private void assignTable(Reservation r, int id) throws InvalidReservationException {
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

    /** Handles a reservation that is finished. Adds to legacy dataset and
     * removes from dataset of upcoming reservations. Handles model + view. */
    private void service(Reservation r, double bill) throws InvalidReservationException {
        r.service();
        pastReservations.insert(r, bill);
        removeReservation(r.getName());
        double total = pastReservations.totalEarnings();
        ledgerView.updateTotalLabel(total);
        dtmLedger.addRow(new Object[]{r.getName(), r.getStartTime().toString(dtfNoHour), bill});
    }

    /** Formats reservation data into tabular form for GUI. */
    private void initializeTableView() {
        Map<Reservation, Table> map = reservations.getReservations();
        for (Map.Entry<Reservation, Table> set : map.entrySet()) {
            dtm.addRow(formatRow(set.getKey(), set.getValue()));
        }
        view.updateSize(reservations.size());
    }

    private void initializeLedgerTableView() {
        Map<Reservation, Double> map = pastReservations.getPastReservations();
        for (Map.Entry<Reservation, Double> set : map.entrySet()) {
            dtmLedger.addRow(new Object[]{set.getKey().getName(), set.getKey().getStartTime()
                    .toString(dtfNoHour), set.getValue()});
        }
    }

    /** Helper method to format a tabled reservation for the view. */
    private Object[] formatRow(Reservation r, Table t) {
        String dt = r.getStartTime().toString(dtfNoYear);
        return new Object[] {r.getName(), r.getPhone(), r.getGuests(), dt,
                r.getDuration(), r.getAccessibility(), r.getMisc(), t.getId()};
    }

    /* CalendarView GUI listeners */
    public void addCalendarListeners() {
        view.addTableListener(e -> {
        if (e.getType() == TableModelEvent.UPDATE) {
            String name = (String) dtm.getValueAt(e.getFirstRow(), 0);
            int col = e.getColumn();
            String update = String.valueOf(dtm.getValueAt(e.getFirstRow(), e.getColumn()));
            try {
                Reservation r = reservations.getByName(name);
                switch (col) {
                    case 1: //phone
                        r.changePhone(update);
                        break;
                    case 2: //guests: reassign reservation
                        changeGuests(name, Integer.parseInt(update));
                        break;
                    case 3: //time: reassign reservation
                        String d = String.valueOf(dtm.getValueAt(e.getFirstRow(), col + 1));
                        int dur = Integer.parseInt(d);
                        changeTime(name, dtf.parseDateTime(update), dur);
                        break;
                    case 4: //duration: reassign reservation
                        changeTime(name, r.getStartTime(), Integer.parseInt(update));
                        break;
                    case 5: //accessibility
                        r.changeAccessibility(update);
                        break;
                    case 6: //misc
                        r.changeMisc(update);
                        break;
                }
            } catch (InvalidReservationException ex) {
                JOptionPane.showMessageDialog(null, "Update error: Reservation not found. ", "Error", JOptionPane.ERROR_MESSAGE);
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
                removeReservation(n);
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
            if (option == JOptionPane.OK_OPTION) {
                try {
                    String rName = name.getText();
                    String rPhone = phone.getText();
                    int rGuests = Integer.parseInt(guests.getText());
                    DateTime rStart = dtf.parseDateTime(startTime.getText());
                    Optional<String> dur = Optional.ofNullable(duration.getText()).filter(Predicate.not(String::isEmpty));
                    int rDuration = Integer.parseInt((dur.orElse("60")));
                    String rAcc = accessibility.getText();
                    String rMisc = misc.getText();

                    Reservation r = new Reservation(rStart, rDuration, rName, rPhone, rGuests, rAcc, rMisc);
                    makeReservation(r);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null, "Date/time processing error. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);

                }
            }
        });

        view.addSaveListener(e -> {
            //serialise
            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("bin/reservation-data.bin"));
                oos.writeObject(reservations);
                ObjectOutputStream oos2 = new ObjectOutputStream(new FileOutputStream("bin/past-reservation-data.bin"));
                oos2.writeObject(pastReservations);
                JOptionPane.showMessageDialog(null, "Reservations & Ledger Data Saved!");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }


    /* LedgerView listeners */
    public void addLedgerListeners() {
        ledgerView.addCalculateListener(e -> {
            JTextField start = new JTextField();
            JTextField end = new JTextField();
            Object[] message = {
                    "Start Time", start,
                    "End Time", end,
            };
            int option = JOptionPane.showConfirmDialog(null, message, "Enter in (MM/dd/YYYY)", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                DateTime startTime = dtfNoHour.parseDateTime(start.getText());
                DateTime endTime = dtfNoHour.parseDateTime(end.getText());
                ledgerView.updateCalculateLabel(pastReservations.calculateEarnings(startTime, endTime));
            }
        });
    }

}