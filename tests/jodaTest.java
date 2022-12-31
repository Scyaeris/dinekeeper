import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.joda.time.DateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

public class jodaTest {
    DateTime dt = new DateTime();

    public static void main(String args[]) {
//        DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm MM/dd/yyyy");
//        DateTime time = dtf.parseDateTime("15:36 11/28/2023");
//        System.out.println(time);

        JFrame frame = new JFrame();
// set up frame

        JTable table = new JTable();
// Set up table, add data

// Frame has a content pane with BorderLayout by default
        frame.getContentPane().add( new JScrollPane( table ), BorderLayout.CENTER );
    }
}