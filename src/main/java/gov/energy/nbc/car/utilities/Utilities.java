package gov.energy.nbc.car.utilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utilities {

    protected static Logger log = Logger.getLogger(Utilities.class);

    public static final SimpleDateFormat ISO_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

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

        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                hour,
                minute,
                seconds);

        calendar.set(Calendar.MILLISECOND, millisec);
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

    public static void setTimeToTheEndOfTheDay(Calendar calendar) {
        setHourAndMinutesAndSeconds(calendar, 23, 59, 59, 999);
    }

    public static void setTimeToTheBeginningOfTheDay(Calendar calendar) {
        setHourAndMinutesAndSeconds(calendar, 0, 0, 0, 0);
    }

    public static Calendar clone(Calendar beginningOfTheDay) {
        Calendar endOfTheDay = new GregorianCalendar();
        endOfTheDay.setTime(beginningOfTheDay.getTime());
        return endOfTheDay;
    }

    public static Calendar toCalendar(String string) {
        Date date = null;
        try {
            date = ISO_FORMAT.parse(string);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return toCalendar(date);
    }

    public static Calendar toCalendar(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }

    public static InputStream toInputStream(XSSFWorkbook workbook)
            throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public static final Comparator ALPHANUMERIC_COMPARATOR = new Comparator() {

        @Override
        public int compare(Object o1, Object o2) {

            String string_1 = (String) o1;
            String string_2 = (String) o2;

            Double number_1 = null;
            try {
                number_1 = Double.parseDouble(string_1);
            } catch (NumberFormatException e1) {
            }

            Double number_2 = null;
            try {
                number_2 = Double.parseDouble(string_2);
            } catch (NumberFormatException e) {
            }

            if (number_1 != null) {

                if (number_2 != null) {
                    // both are numbers
                    return number_1.compareTo(number_2);
                }
                else {
                    // first is a number, second is not
                    return 1;
                }
            }
            else {

                if (number_2 != null) {
                    // first is not a number, second is
                    return -1;
                }
                else {
                    // neither are numbers
                    return string_1.compareTo(string_2);
                }
            }
        }

    };

    public static void sortAlphaNumerically(List<String> elements) {

        PerformanceLogger performanceLogger =
                new PerformanceLogger(log, "Sorting a set with " + elements.size() + ".");

        Collections.sort(elements, ALPHANUMERIC_COMPARATOR);

        performanceLogger.done();
    }

    public static SortedSet<String> toSortedSet(Set<String> elements) {

        SortedSet<String> sortedSet = new TreeSet(ALPHANUMERIC_COMPARATOR);
        sortedSet.addAll(elements);

        return sortedSet;
    }
}
