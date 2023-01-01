package dinekeeper;

import dinekeeper.controller.ReservationManager;
import dinekeeper.controller.RestaurantManager;
import dinekeeper.model.data.PastReservationData;
import dinekeeper.model.data.ReservationData;
import dinekeeper.model.data.RestaurantData;
import dinekeeper.view.CalendarView;
import dinekeeper.view.LedgerView;
import dinekeeper.view.TableView;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class App {
    /**  */
    public static void main(String args[]) {
        makeApp();
    }

    public static void makeApp() {
        SwingUtilities.invokeLater(() -> {
            RestaurantData r = fetchSavedRestaurantData();
            ReservationData re = fetchSavedReservationData(r);
            PastReservationData pr = fetchSavedPastData();

            JFrame app = new JFrame();
            JTabbedPane pane = new JTabbedPane();
            startRestaurant(pane,r);
            startCalendarLedger(pane, r, re, pr);
            app.add(pane);
            app.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            app.setTitle("Dinekeeper");
            app.setSize(new Dimension(750, 400)); //comment out if unneeded
            app.setVisible(true);
        });
    }

    public static void startRestaurant(JTabbedPane pane, RestaurantData r) {
        TableView tableView = new TableView();
        pane.addTab("Restaurant", tableView);
        RestaurantManager restaurantManager = new RestaurantManager(tableView, r);
    }

    public static void startCalendarLedger(JTabbedPane pane, RestaurantData r, ReservationData re, PastReservationData pr) {
        CalendarView calendarView = new CalendarView();
        LedgerView ledgerView = new LedgerView();
        pane.addTab("Calendar", calendarView);
        pane.addTab("Ledger", ledgerView);
        ReservationManager reservationManager = new ReservationManager(calendarView, ledgerView, r, re, pr);
    }

    //get data
    public static RestaurantData fetchSavedRestaurantData() {
        try {
            if (! new File("bin/restaurant-data.bin").exists()) return new RestaurantData();
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("bin/restaurant-data.bin"));
            RestaurantData r = (RestaurantData) ois.readObject();
            return r;
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new RestaurantData();
    }

    public static ReservationData fetchSavedReservationData(RestaurantData r) {
        try {
            if (! new File("bin/reservation-data.bin").exists()) return new ReservationData(r);
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("bin/reservation-data.bin"));
            ReservationData res = (ReservationData) ois.readObject();
            return res;
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
        return new ReservationData(r);
    }

    public static PastReservationData fetchSavedPastData() {
        try {
            if (! new File("bin/past-reservation-data.bin").exists()) return new PastReservationData();
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("bin/past-reservation-data.bin"));
            PastReservationData r = (PastReservationData) ois.readObject();
            return r;
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
        return new PastReservationData();
    }
}
