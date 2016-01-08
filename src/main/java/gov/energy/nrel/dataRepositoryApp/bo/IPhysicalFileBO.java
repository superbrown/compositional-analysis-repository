package gov.energy.nrel.dataRepositoryApp.bo;

import gov.energy.nrel.dataRepositoryApp.dao.IFileStorageDAO;
import gov.energy.nrel.dataRepositoryApp.dao.exception.CouldNotCreateDirectory;
import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;
import gov.energy.nrel.dataRepositoryApp.dao.dto.StoredFile;
import gov.energy.nrel.dataRepositoryApp.dao.exception.UnableToDeleteFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public interface IPhysicalFileBO extends IBO {

    StoredFile saveFile(Date timestamp, String subdirectory, FileAsRawBytes file);

    IFileStorageDAO getFileStorageDAO();

    StoredFile storeFile(Date timestamp, String subdirectory, FileAsRawBytes file)
            throws CouldNotCreateDirectory, IOException;

    void deletFile(String storageLocation)
            throws UnableToDeleteFile;

    void moveFilesToRemovedFilesLocation(String pathToFile)
            throws IOException;

    File getFile(String storageLocation);
}
