package gov.energy.nbc.car.dao;

import gov.energy.nbc.car.utilities.FileAsRawBytes;
import gov.energy.nbc.car.dao.dto.StoredFile;
import gov.energy.nbc.car.dao.exception.CouldNotCreateDirectory;
import gov.energy.nbc.car.dao.exception.UnableToDeleteFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public interface IFileStorageDAO {

    StoredFile saveFile(Date timestamp, String subdirectory, FileAsRawBytes file)
                    throws CouldNotCreateDirectory, IOException;

    void deletFile(String file)
                            throws UnableToDeleteFile;

    void moveFilesToRemovedFilesLocation(String filePath)
            throws IOException;

    File getFile(String storageLocation);
}
