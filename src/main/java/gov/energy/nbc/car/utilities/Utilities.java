package gov.energy.nbc.car.utilities;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.io.*;
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

	public static void writeFileToOutputSream(String filePath, OutputStream outputStream)
			throws IOException {

		InputStream inputStream = null;
		try {
			inputStream = new BufferedInputStream(new FileInputStream(filePath));

			byte[] bytes = new byte[4 * 1024];
			int bytesRead;

			while ((bytesRead = inputStream.read(bytes)) != -1) {

				outputStream.write(bytes, 0, bytesRead);
			}
		}
		finally {

			if (inputStream != null)
				inputStream.close();
		}
	}
}
