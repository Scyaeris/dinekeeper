import org.joda.time.DateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

public class jodaTest {
    DateTime dt = new DateTime();

    public static void main(String args[]) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm MM/dd/yyyy");
        DateTime time = dtf.parseDateTime("15:36 11/28/2023");
        System.out.println(time);
    }
}