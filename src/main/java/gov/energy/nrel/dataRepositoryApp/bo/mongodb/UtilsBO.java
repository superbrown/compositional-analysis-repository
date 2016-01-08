package gov.energy.nrel.dataRepositoryApp.bo.mongodb;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;
import gov.energy.nrel.dataRepositoryApp.utilities.Utilities;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.DatasetReader_ExcelWorkbook;
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

    Logger log = Logger.getLogger(this.getClass());

    protected DatasetReader_ExcelWorkbook datasetReader_excelWorkbook;
    private String tempDirectoryPath;

    public UtilsBO(DataRepositoryApplication dataRepositoryApplication) {
        super(dataRepositoryApplication);
    }

    @Override
    protected void init() {

        datasetReader_excelWorkbook = new DatasetReader_ExcelWorkbook();
        tempDirectoryPath = getSettings().getRootDirectoryForUploadedDataFiles() + "/temp";
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
                (new File(tempFilePath)).delete();
                (new File(tempFileDirectoryPath)).delete();
            }
            catch (Exception e) {
                log.warn(e);
            }
            finally {
                // Garbage collect because we're having problems with the worksheet
                // staying in memory, and it's often HUGE!
                System.gc();
            }
        }
    }
}
