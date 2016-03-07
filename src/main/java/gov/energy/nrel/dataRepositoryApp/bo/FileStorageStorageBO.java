package gov.energy.nrel.dataRepositoryApp.bo;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsBO;
import gov.energy.nrel.dataRepositoryApp.dao.FileStorageStorageDAO;
import gov.energy.nrel.dataRepositoryApp.dao.IFileStorageDAO;
import gov.energy.nrel.dataRepositoryApp.model.common.IStoredFile;
import gov.energy.nrel.dataRepositoryApp.utilities.valueSanitizer.IValueSanitizer;
import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.DatasetReader_AllFileTypes;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.IDatasetReader_AllFileTypes;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FileStorageStorageBO extends AbsBO implements IFileStorageBO {

    protected IFileStorageDAO fileStorageDAO;

    protected IDatasetReader_AllFileTypes generalFileReader;

    public FileStorageStorageBO(DataRepositoryApplication d) {

        super(d);
    }

    @Override
    protected void init() {
        fileStorageDAO = new FileStorageStorageDAO(getSettings());

        IValueSanitizer valueSanitizer = this.getDataRepositoryApplication().getValueSanitizer();
        generalFileReader = new DatasetReader_AllFileTypes(valueSanitizer);
    }

    @Override
    public IStoredFile saveFile(Date timestamp, String subdirectory, FileAsRawBytes file) {

        IStoredFile theDataFileThatWasStored = null;
        try {

            theDataFileThatWasStored = getFileStorageDAO().saveFile(timestamp, subdirectory, file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
