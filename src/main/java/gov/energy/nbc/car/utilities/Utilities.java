package gov.energy.nbc.car.utilities;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Utilities {

    public static void putIfNotBlank(Document document, String name, String value) {

        if (StringUtils.isNotBlank(value)) {
            document.put(name, value);
        }
    }

    public static List<String> toListOrNull(String[] array) {

        if (array == null || array.length == 0) {
            return null;
        }

        return Arrays.asList(array);
    }

	public static File getFile(String filePath) throws URISyntaxException {

        URL fileURL = Utilities.class.getResource(filePath);
        return new File(fileURL.toURI());
    }

    public static void setHourAndMinutesAndSeconds(Calendar calendar, int hour, int minute, int seconds, int millisec) {

        System.out.println("before: " + toString(calendar));

        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                hour,
                minute,
                seconds);

        calendar.set(Calendar.MILLISECOND, millisec);

        System.out.println("after: " + toString(calendar));
    }

    private static String toString(Calendar calendar) {
        return "" +
                    calendar.get(Calendar.YEAR) + " " +
                    calendar.get(Calendar.MONTH) + " " +
                    calendar.get(Calendar.DAY_OF_MONTH) + " " +
                    calendar.get(Calendar.HOUR) + " " +
                    calendar.get(Calendar.MINUTE) + " " +
                    calendar.get(Calendar.SECOND) + " " +
                    calendar.get(Calendar.MILLISECOND);
    }
}
