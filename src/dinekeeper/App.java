package dinekeeper;

import dinekeeper.controller.ReservationManager;
import dinekeeper.controller.RestaurantManager;
import dinekeeper.model.data.RestaurantData;
import dinekeeper.view.CalendarView;
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
    /** CLI App. Maybe expand into Swing GUI later. */
    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            JFrame app = new JFrame();
            JTabbedPane pane = new JTabbedPane();
            RestaurantData r = new RestaurantData(); //change to calling fetch data later
            startRestaurant(pane,r);
            startCalendar(pane,r);
            //pane.setMnemonicAt(0, KeyEvent.VK_0);
            //pane.setMnemonicAt(0, KeyEvent.VK_1);
            app.add(pane);
            app.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            app.setTitle("Dinekeeper");
            app.setSize(new Dimension(800, 400)); //comment out if unneeded
            app.setVisible(true);
        });
    }

    public static void startCalendar(JTabbedPane pane, RestaurantData r) {
        CalendarView calendarView = new CalendarView();
        pane.addTab("Calendar", calendarView);

        ReservationManager reservationManager = new ReservationManager(calendarView, r);
    }

    public static void startRestaurant(JTabbedPane pane, RestaurantData r) {
        TableView tableView = new TableView();
        pane.addTab("Restaurant", tableView);
        RestaurantManager restaurantManager = new RestaurantManager(tableView, r);
    }

    //get data (serialise?)
}
