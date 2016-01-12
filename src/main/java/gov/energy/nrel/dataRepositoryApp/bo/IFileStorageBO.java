package gov.energy.nrel.dataRepositoryApp.bo;

import gov.energy.nrel.dataRepositoryApp.dao.IFileStorageDAO;
import gov.energy.nrel.dataRepositoryApp.model.common.IStoredFile;
import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public interface IFileStorageBO extends IBO {

    IStoredFile saveFile(Date timestamp, String subdirectory, FileAsRawBytes file);

    IFileStorageDAO getFileStorageDAO();

    void deleteFolder(String storageLocation)
            throws IOException;

    void moveFilesToRemovedFilesLocation(String pathToFile)
            throws IOException;

    File getFile(String storageLocation);
}
