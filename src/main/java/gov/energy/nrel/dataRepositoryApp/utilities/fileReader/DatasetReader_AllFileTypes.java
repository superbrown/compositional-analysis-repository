package gov.energy.nrel.dataRepositoryApp.utilities.fileReader;

import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.dto.RowCollection;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.FileContainsInvalidColumnName;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class DatasetReader_AllFileTypes extends AbsDatasetReader implements IDatasetReader_AllFileTypes {

    protected static Logger log = Logger.getLogger(DatasetReader_AllFileTypes.class);

    public IDatasetReader_ExcelWorkbook excelWorkbookReader;
    public IDatasetReader_CSVFile csvFileReader;

    public DatasetReader_AllFileTypes() {

        this.excelWorkbookReader = new DatasetReader_ExcelWorkbook();
        this.csvFileReader = new DatasetReader_CSVFile();
    }


    @Override
    public RowCollection extractDataFromFile(File file, String nameOfSubdocumentContainingDataIfApplicable, int maxNumberOfValuesPerRow)
            throws UnsupportedFileExtension, FileContainsInvalidColumnName {

        RowCollection rowCollection = null;

        try {
            if (excelWorkbookReader.canReadFile(file)) {

                rowCollection = extractDataFromDataset(file, nameOfSubdocumentContainingDataIfApplicable);
            }
            else if (csvFileReader.canReadFile(file)) {

                rowCollection = extractDataFromCSVFile(file, maxNumberOfValuesPerRow);
            }
            else {
                new UnsupportedFileExtension(file.getName());
            }
        }
        catch (IOException e) {
            log.error(e, e);
            throw new RuntimeException(e);
        }

        return rowCollection;
    }

    @Override
    public RowCollection extractDataFromDataset(File file, String nameOfSubdocumentContainingDataIfApplicable)
            throws UnsupportedFileExtension, IOException, FileContainsInvalidColumnName {

        RowCollection dataUpload =
                excelWorkbookReader.extractDataFromFile(file, nameOfSubdocumentContainingDataIfApplicable);

        return dataUpload;
    }

    public RowCollection extractDataFromCSVFile(File file, int maxNumberOfValuesPerRow)
            throws UnsupportedFileExtension, IOException, FileContainsInvalidColumnName {

        RowCollection dataUpload = csvFileReader.extractDataFromFile(file, maxNumberOfValuesPerRow);

        return dataUpload;
    }

    @Override
    public boolean isAnExcelFile(String filename) {

        return excelWorkbookReader.canReadFileWithExtension(filename);
    }

    @Override
    public boolean canReadFile(File file) {

        return excelWorkbookReader.canReadFile(file) ||
                csvFileReader.canReadFile(file);
    }

    @Override
    public boolean canReadFileWithExtension(String filename) {

        return excelWorkbookReader.canReadFileWithExtension(filename) ||
                csvFileReader.canReadFileWithExtension(filename);
    }
}
