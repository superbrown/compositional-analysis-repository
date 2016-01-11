package gov.energy.nrel.dataRepositoryApp.dao;

import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;
import gov.energy.nrel.dataRepositoryApp.dao.dto.StoredFile;
import gov.energy.nrel.dataRepositoryApp.dao.exception.CouldNotCreateDirectory;
import gov.energy.nrel.dataRepositoryApp.dao.exception.UnableToDeleteFile;

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

    void deleteFolder(String filePath)
            throws IOException;

    File getFile(String storageLocation);
}
