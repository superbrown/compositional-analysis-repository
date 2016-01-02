package gov.energy.nrel.dataRepositoryApp.bo;

import gov.energy.nrel.dataRepositoryApp.dao.IFileStorageDAO;
import gov.energy.nrel.dataRepositoryApp.dao.FileStorageStorageDAO;
import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;
import gov.energy.nrel.dataRepositoryApp.dao.dto.StoredFile;
import gov.energy.nrel.dataRepositoryApp.dao.exception.UnableToDeleteFile;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.DatasetReader_AllFileTypes;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.IDatasetReader_AllFileTypes;

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
