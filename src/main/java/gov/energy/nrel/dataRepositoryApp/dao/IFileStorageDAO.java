package gov.energy.nrel.dataRepositoryApp.dao;

import gov.energy.nrel.dataRepositoryApp.dao.exception.CouldNotCreateDirectory;
import gov.energy.nrel.dataRepositoryApp.dao.exception.UnableToDeleteFile;
import gov.energy.nrel.dataRepositoryApp.model.common.IStoredFile;
import gov.energy.nrel.dataRepositoryApp.model.common.mongodb.Metadata;
import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface IFileStorageDAO {

    IStoredFile saveFile(Date timestamp, String subdirectory, FileAsRawBytes file)
                    throws CouldNotCreateDirectory, IOException;

    void deletFile(String file)
                            throws UnableToDeleteFile;

    void moveFilesToRemovedFilesLocation(String filePath)
            throws IOException;

    void deleteFolder(String filePath)
            throws IOException;

    File getFile(String storageLocation);

    List<Metadata> getAllMetadata();
}
