package courses.concordia.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

    public static Date today() {
        return new Date();
    }

    public static LocalDateTime getLocalDateTime() {
        // Define the timezone
        ZoneId zoneId = ZoneId.of("America/New_York");

        // Get the current time in the specified timezone
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);

        // Format the time to the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedTime = zonedDateTime.format(formatter);

        // Parse the formatted time to LocalDateTime
        return LocalDateTime.parse(formattedTime, formatter);
    }

}