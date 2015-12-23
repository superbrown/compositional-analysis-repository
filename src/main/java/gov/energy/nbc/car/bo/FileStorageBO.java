package gov.energy.nbc.car.bo;

import gov.energy.nbc.car.dao.IFileStorageDAO;
import gov.energy.nbc.car.dao.FileStorageStorageDAO;
import gov.energy.nbc.car.utilities.FileAsRawBytes;
import gov.energy.nbc.car.dao.dto.StoredFile;
import gov.energy.nbc.car.dao.exception.UnableToDeleteFile;
import gov.energy.nbc.car.settings.ISettings;
import gov.energy.nbc.car.utilities.fileReader.DatasetReader_AllFileTypes;
import gov.energy.nbc.car.utilities.fileReader.IDatasetReader_AllFileTypes;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FileStorageBO implements IPhysicalFileBO {

    protected IFileStorageDAO fileStorageDAO;

    protected IDatasetReader_AllFileTypes generalFileReader;

    public FileStorageBO(ISettings settings) {

        fileStorageDAO = new FileStorageStorageDAO(settings);
        generalFileReader = new DatasetReader_AllFileTypes();
    }

//    @Override
//    public StoredFile saveFile(MultipartFile dataFile) {
//
//        StoredFile theDataFileThatWasStored = null;
//        try {
//            theDataFileThatWasStored = getDataFileDAO().saveFile(dataFile);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        return theDataFileThatWasStored;
//    }

    @Override
    public StoredFile saveFile(Date timestamp, String subdirectory, FileAsRawBytes file) {

        StoredFile theDataFileThatWasStored = null;
        try {
            theDataFileThatWasStored = getFileStorageDAO().saveFile(timestamp, subdirectory, file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return theDataFileThatWasStored;
    }

    @Override
    public void deletFile(String storageLocation)
            throws UnableToDeleteFile {

        getFileStorageDAO().deletFile(storageLocation);
    }

    @Override
    public void moveFilesToRemovedFilesLocation(String pathToFile)
            throws IOException {

        getFileStorageDAO().moveFilesToRemovedFilesLocation(pathToFile);
    }

    @Override
    public File getFile(String storageLocation) {

        return getFileStorageDAO().getFile(storageLocation);
    }

    @Override
    public IFileStorageDAO getFileStorageDAO() {
        return fileStorageDAO;
    }
}
