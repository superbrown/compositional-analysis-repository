package gov.energy.nbc.car.utilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.springframework.core.io.InputStreamResource;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utilities {

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
}
