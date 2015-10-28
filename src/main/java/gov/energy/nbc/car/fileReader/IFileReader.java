package gov.energy.nbc.car.fileReader;

import java.io.File;

public interface IFileReader {

    boolean canReadFile(File file);

    boolean canReadFileWithExtension(String filename);
}
