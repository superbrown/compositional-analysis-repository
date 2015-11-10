package gov.energy.nbc.car.fileReader;

import java.io.File;

public interface IDatasetReader {

    boolean canReadFile(File file);

    boolean canReadFileWithExtension(String fileName);
}
