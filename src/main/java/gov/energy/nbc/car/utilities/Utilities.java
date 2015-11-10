package gov.energy.nbc.car.utilities;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
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
}
