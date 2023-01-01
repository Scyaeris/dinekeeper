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
import java.awt.event.KeyEvent;
import java.sql.SQLOutput;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.joda.time.DateTime;
import org.joda.time.Interval;

public class App {
    /**  */
    public static void main(String args[]) {
        makeApp();
    }

    public static void makeApp() {
        SwingUtilities.invokeLater(() -> {
            RestaurantData r = new RestaurantData(); //change to calling fetch data later
            ReservationData re = new ReservationData(r); //above
            PastReservationData pr = new PastReservationData(); //above

            JFrame app = new JFrame();
            JTabbedPane pane = new JTabbedPane();
            startRestaurant(pane,r);
            startCalendarLedger(pane, r, re, pr);
            app.add(pane);
            app.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            app.setTitle("Dinekeeper");
            app.setSize(new Dimension(800, 400)); //comment out if unneeded
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
    //get data (serialise?)
}
