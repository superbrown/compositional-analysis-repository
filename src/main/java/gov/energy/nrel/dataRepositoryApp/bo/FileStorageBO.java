package gov.energy.nrel.dataRepositoryApp.bo;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsBO;
import gov.energy.nrel.dataRepositoryApp.dao.IFileStorageDAO;
import gov.energy.nrel.dataRepositoryApp.dao.FileStorageStorageDAO;
import gov.energy.nrel.dataRepositoryApp.dao.exception.CouldNotCreateDirectory;
import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;
import gov.energy.nrel.dataRepositoryApp.dao.dto.StoredFile;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.DatasetReader_AllFileTypes;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.IDatasetReader_AllFileTypes;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FileStorageBO extends AbsBO implements IPhysicalFileBO {

    protected IFileStorageDAO fileStorageDAO;

    protected IDatasetReader_AllFileTypes generalFileReader;

    public FileStorageBO(DataRepositoryApplication d) {

        super(d);
    }

    @Override
    protected void init() {
        fileStorageDAO = new FileStorageStorageDAO(getSettings());
        generalFileReader = new DatasetReader_AllFileTypes();
    }

    @Override
    public StoredFile saveFile(Date timestamp, String subdirectory, FileAsRawBytes file) {

        StoredFile theDataFileThatWasStored = null;
        try {
            theDataFileThatWasStored = storeFile(timestamp, subdirectory, file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return theDataFileThatWasStored;
    }

    @Override
    public StoredFile storeFile(Date timestamp, String subdirectory, FileAsRawBytes file)
            throws CouldNotCreateDirectory, IOException {

        StoredFile theDataFileThatWasStored;
        theDataFileThatWasStored = getFileStorageDAO().saveFile(timestamp, subdirectory, file);

        return theDataFileThatWasStored;
    }

    @Override
    public void deleteFolder(String storageLocation)
            throws IOException {

        getFileStorageDAO().deleteFolder(storageLocation);
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
