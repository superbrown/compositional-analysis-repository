package gov.energy.nrel.dataRepositoryApp.bo.mongodb;

import com.mongodb.client.MongoDatabase;
import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IBusinessObjects;
import gov.energy.nrel.dataRepositoryApp.bo.IDatasetBO;
import gov.energy.nrel.dataRepositoryApp.bo.IFileStorageBO;
import gov.energy.nrel.dataRepositoryApp.bo.exception.FailedToSave;
import gov.energy.nrel.dataRepositoryApp.dao.FileStorageStorageDAO;
import gov.energy.nrel.dataRepositoryApp.model.common.mongodb.Metadata;
import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;
import gov.energy.nrel.dataRepositoryApp.utilities.Utilities;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.DatasetReader_ExcelWorkbook;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.FileContainsInvalidColumnName;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UtilsBO extends AbsBO implements gov.energy.nrel.dataRepositoryApp.bo.IUtilsBO {

    protected static Logger log = Logger.getLogger(UtilsBO.class);

    protected DatasetReader_ExcelWorkbook datasetReader_excelWorkbook;
    private String tempDirectoryPath;
    private FileStorageStorageDAO fileStorageDAO;


    public UtilsBO(DataRepositoryApplication dataRepositoryApplication) {
        super(dataRepositoryApplication);
    }

    @Override
    protected void init() {

        datasetReader_excelWorkbook = new DatasetReader_ExcelWorkbook();
        tempDirectoryPath = getSettings().getRootDirectoryForUploadedDataFiles() + "/temp";
        fileStorageDAO = new FileStorageStorageDAO(getSettings());
    }

    @Override
    public List<String> getNamesOfSheetsWithinWorkbook(String fileName, FileAsRawBytes fileAsRawBytes)
            throws IOException, UnsupportedFileExtension {

        String tempFileDirectoryPath = this.tempDirectoryPath + "/" + UUID.randomUUID() + "/";
        Utilities.assureTheDirectoryExists(tempFileDirectoryPath);

        String tempFilePath = tempFileDirectoryPath + "/" + fileName;
        File tempFile = Utilities.saveFile(fileAsRawBytes.bytes, tempFilePath);

        try {
            FileInputStream fileInputStream = new FileInputStream(tempFile);

            Workbook workbook = datasetReader_excelWorkbook.createWorkbookObject(fileInputStream, fileName);

            List<String> namesOfSheetsWithinWorkbook = new ArrayList<>();

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                String sheetName = workbook.getSheetAt(i).getSheetName();
                namesOfSheetsWithinWorkbook.add(sheetName);
            }

            return namesOfSheetsWithinWorkbook;
        }
        finally {

            try {
                IFileStorageBO fileSotrageBO = getDataRepositoryApplication().getBusinessObjects().getFileSotrageBO();
                fileSotrageBO.deleteFolder(tempFileDirectoryPath);
            }
            catch (Exception e) {
                log.warn(e, e);
            }
        }
    }

    @Override
    public void dropDatabase() {

        MongoDatabase mongoDatabase =
                getDataRepositoryApplication().getBusinessObjects().getDatasetBO().getDatasetDAO().getDatabase();
        mongoDatabase.drop();
    }

    @Override
    public List<String> repopulateDatabaseUsingFilesStoredOnServer() {

        List<Metadata> metadataList = fileStorageDAO.getAllMetadataForActiveData();

        dropDatabase();

        DataRepositoryApplication dataRepositoryApplication = getDataRepositoryApplication();
        dataRepositoryApplication.initializeBusinessObjects();

        IBusinessObjects businessObjects = dataRepositoryApplication.getBusinessObjects();

        String[] defaultSetOfDataCategories = getSettings().getDefaultSetOfDataCategories();
        businessObjects.getDataCategoryBO().assureCategoriesAreInTheDatabase(defaultSetOfDataCategories);

        IDatasetBO datasetBO = businessObjects.getDatasetBO();

        List<String> errors = new ArrayList<>();

        for (Metadata metadata : metadataList) {

            try {
                datasetBO.addDataset(metadata);
            }
            catch (FailedToSave e) {

                log.error(metadata, e);
                errors.add("Failed to save: " + metadata);
                throw new RuntimeException(e);
            }
            catch (FileContainsInvalidColumnName e) {
                log.error(metadata, e);
                errors.add("Failed to save (file contains invalid column name): " + metadata);
                throw new RuntimeException(e);
            }
            catch (UnsupportedFileExtension e) {
                errors.add("Failed to save (unsupported file extension): " + metadata);
                throw new RuntimeException(e);
            }
        }

        return errors;
    }
}
