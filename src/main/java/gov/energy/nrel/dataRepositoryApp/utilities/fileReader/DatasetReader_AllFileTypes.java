package gov.energy.nrel.dataRepositoryApp.utilities.fileReader;

import gov.energy.nrel.dataRepositoryApp.utilities.AbsValueSanitizer;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.dto.RowCollection;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.FailedToExtractDataFromFile;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.FileContainsInvalidColumnName;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.NotAnExcelWorkbook;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class DatasetReader_AllFileTypes extends AbsDatasetReader implements IDatasetReader_AllFileTypes {

    protected static Logger log = Logger.getLogger(DatasetReader_AllFileTypes.class);

    public IDatasetReader_ExcelWorkbook excelWorkbookReader;
    public IDatasetReader_CSVFile csvFileReader;

    public DatasetReader_AllFileTypes(AbsValueSanitizer valueSanitizer) {

        super(valueSanitizer);

        this.excelWorkbookReader = new DatasetReader_ExcelWorkbook(valueSanitizer);
        this.csvFileReader = new DatasetReader_CSVFile(valueSanitizer);
    }


    @Override
    public RowCollection extractDataFromFile(File file, String nameOfSubdocumentContainingDataIfApplicable, int maxNumberOfValuesPerRow)
            throws FileContainsInvalidColumnName, UnsupportedFileExtension, FailedToExtractDataFromFile, UnsanitaryData {

        RowCollection rowCollection = null;

        if (excelWorkbookReader.canReadFile(file)) {

            try {
                rowCollection = extractDataFromExcelFile(file, nameOfSubdocumentContainingDataIfApplicable);
            }
            catch (NotAnExcelWorkbook e) {
                throw new RuntimeException("This should never happen. Is likely a software defect.", e);
            }
            catch (IOException e) {
                throw new FailedToExtractDataFromFile(file.getName(), e);
            }
        }
        else if (csvFileReader.canReadFile(file)) {

            try {
                rowCollection = extractDataFromCSVFile(file, maxNumberOfValuesPerRow);
            }
            catch (UnsupportedFileExtension e) {
                throw new RuntimeException("This should never happen. Is likely a software defect.", e);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            throw new UnsupportedFileExtension(file.getName());
        }

        return rowCollection;
    }

    public RowCollection extractDataFromExcelFile(File file, String nameOfSubdocumentContainingDataIfApplicable)
            throws IOException, FileContainsInvalidColumnName, NotAnExcelWorkbook, UnsanitaryData {

        RowCollection dataUpload =
                excelWorkbookReader.extractDataFromFile(file, nameOfSubdocumentContainingDataIfApplicable);

        return dataUpload;
    }

    public RowCollection extractDataFromCSVFile(File file, int maxNumberOfValuesPerRow)
            throws UnsupportedFileExtension, IOException, FileContainsInvalidColumnName, UnsanitaryData {

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
