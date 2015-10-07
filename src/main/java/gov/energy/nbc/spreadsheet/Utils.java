package gov.energy.nbc.spreadsheet;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

public class Utils {

    public static void putIfNotBlank(Document document, String name, String value) {

        if (StringUtils.isNotBlank(value)) {
            document.put(name, value);
        }
    }


    public static List<String> toListOrNull(String[] array) {

        if (array == null) {
            return null;
        }

        return Arrays.asList(array);
    }
}
