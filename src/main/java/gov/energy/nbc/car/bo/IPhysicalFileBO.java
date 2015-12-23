package gov.energy.nbc.car.bo;

import gov.energy.nbc.car.dao.IFileStorageDAO;
import gov.energy.nbc.car.utilities.FileAsRawBytes;
import gov.energy.nbc.car.dao.dto.StoredFile;
import gov.energy.nbc.car.dao.exception.UnableToDeleteFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public interface IPhysicalFileBO {

    StoredFile saveFile(Date timestamp, String subdirectory, FileAsRawBytes file);

    IFileStorageDAO getFileStorageDAO();

    void deletFile(String storageLocation)
            throws UnableToDeleteFile;

    void moveFilesToRemovedFilesLocation(String pathToFile)
            throws IOException;

    File getFile(String storageLocation);
}
