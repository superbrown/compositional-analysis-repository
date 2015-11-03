package gov.energy.nbc.car.fileReader;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public abstract class AbsFileReader {

    abstract boolean canReadFile(File file);

    abstract boolean canReadFileWithExtension(String filename);

    public boolean containsData(List<Object> data) {

        for (Object dataElement : data) {

            if (dataElement instanceof String) {

                if (StringUtils.isNotBlank((String) dataElement)) {
                    return true;
                }
            }
            else if (dataElement != null) {
                return true;
            }
        }

        return false;
    }
}
