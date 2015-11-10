package gov.energy.nbc.car.utilities.fileReader;

import java.io.File;

public interface IDatasetReader {

    String ATTR_KEY__ROW_NUMBER = "_origDocRowNum";

    boolean canReadFile(File file);

    boolean canReadFileWithExtension(String fileName);
}
