package gov.energy.nrel.dataRepositoryApp.restEndpoint;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.exception.*;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.UnsanitaryData;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.UnsanitaryRequestParameter;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.FailedToExtractDataFromFile;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.FileContainsInvalidColumnName;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.NotAnExcelWorkbook;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EndpointController {

    private static Logger log = Logger.getLogger(EndpointController.class);

    private static final String HTTP_STATUS_CODE_KEY = "status";
    private static final String REASON_PHRASE_KEY = "reasonPhrase";
    private static final String MESSAGE_KEY = "message";
    private static final String ROW_NUMBER = "rowNumber";
    private static final String COLUMN_NUMBER = "columnNumber";
    private static final String SANITIZED_VALUE = "sanitizedValue";
    private static final String FILE_NAME = "fileName";
    private static final String INVALID_NAME = "invalidName";
    private static final String PARAMETER_NAME = "parameterName";

    protected void throwExceptionIfCleanupOperationsIsOccurring() throws CleanupOperationIsOccurring {

        if (DataRepositoryApplication.cleanupOperationIsOccurring == true) {
            throw new CleanupOperationIsOccurring();
        }
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    Map<String,Object> handleGenericException(Throwable e,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {

        UUID logEntryID = logError(e);
        return createResultMapWithMessage(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An error occurred. The server's error log contains further detail " +
                "(log entry ID: " + logEntryID + ").");
    }

    @ExceptionHandler(FailedToSave.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    Map<String,Object> handleFailedToSave(FailedToSave e,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {

        return handleGenericException(e, request, response);
    }

    @ExceptionHandler(FailedToDeleteFiles.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    Map<String,Object> handleFailedToDeleteFiles(FailedToDeleteFiles e,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {

        return handleGenericException(e, request, response);
    }

    @ExceptionHandler(FailedToExtractDataFromFile.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    Map<String,Object> handleFailedToDeleteFiles(FailedToExtractDataFromFile e,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {

        return handleGenericException(e, request, response);
    }

    @ExceptionHandler(UnknownDataset.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody
    Map<String,Object> handleUnknownDataset(UnknownDataset e,
                                            HttpServletRequest request, 
                                            HttpServletResponse response) {

        return createResultMapWithMessage(
                HttpStatus.NOT_FOUND,
                "The endpoint is valid, but the particular dataset is unknown: " + e.getId());
    }

    @ExceptionHandler(UnknownRow.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody
    Map<String,Object> handleUnknownDataCatogory(UnknownRow e,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {

        return createResultMapWithMessage(
                HttpStatus.NOT_FOUND,
                "The endpoint is valid, but the particular row is unknown: " + e.getId());
    }

    @ExceptionHandler(UnknownDataCatogory.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody
    Map<String,Object> handleUnknownDataCatogory(UnknownDataCatogory e,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {

        return createResultMapWithMessage(
                HttpStatus.NOT_FOUND,
                "The endpoint is valid, but the particular data category is unknown: " + e.getId());
    }

    @ExceptionHandler(DataCategoryAlreadyExists.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody
    Map<String,Object> handleDataCategoryAlreadyExists(DataCategoryAlreadyExists e,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response) {

        return createResultMapWithMessage(
                HttpStatus.CONFLICT,
                "That data category, " + e.getId() + ", already exists.");
    }

    @ExceptionHandler(FileContainsInvalidColumnName.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public @ResponseBody
    Map<String,Object> handleFileContainsInvalidColumnName(FileContainsInvalidColumnName e,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response) {

        Map<String, Object> result = createResultMapWithMessage(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "The file, " + e.fileName + ", contains an invalid column name. " +
                        "Column " + e.columnNumber + " has the name '" + e.columnName + "'.");

        result.put(FILE_NAME, e.fileName);
        result.put(COLUMN_NUMBER, e.columnNumber);
        result.put(INVALID_NAME, e.columnName);
        return result;
    }

    @ExceptionHandler(UnsanitaryRequestParameter.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public @ResponseBody
    Map<String,Object> handleFileContainsInvalidColumnName(UnsanitaryRequestParameter e,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response) {

        Map<String, Object> result = createResultMapWithMessage(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "The parameter, " + e.paramaterName + ", contains an invalid value.");

        result.put(PARAMETER_NAME, e.paramaterName);
        return result;
    }

    @ExceptionHandler(UnsanitaryData.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public @ResponseBody
    Map<String,Object> handleFileContainsInvalidColumnName(UnsanitaryData e,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response) {

        Map<String, Object> result = createResultMapWithMessage(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "The file contains data that could potentially be malicious. " +
                        "The first encounter of such data (there may " +
                        "be additional examples) is located in row " + e.rowNumber + ", " +
                        "column " + e.columnNumber + ". " +
                        "Its \"sanitized\" value is: " + e.sanitizedValue);

        result.put(ROW_NUMBER, e.rowNumber);
        result.put(COLUMN_NUMBER, e.columnNumber);
        result.put(SANITIZED_VALUE, e.sanitizedValue);
        return result;
    }

    @ExceptionHandler(UnsupportedFileExtension.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public @ResponseBody
    Map<String,Object> handleFileContainsInvalidColumnName(UnsupportedFileExtension e,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response) {

        Map<String, Object> result = createResultMapWithMessage(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "The file, " + e.fileName + ", is a type that is not supported.");

        result.put(FILE_NAME, e.fileName);
        return result;
    }

    @ExceptionHandler(NotAnExcelWorkbook.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public @ResponseBody
    Map<String,Object> handleNotAnExcelWorkbook(NotAnExcelWorkbook e,
                                                HttpServletRequest request,
                                                HttpServletResponse response) {

        Map<String, Object> result = createResultMapWithMessage(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "The file, " + e.fileName + ", is not an Excel workbook.");

        result.put(FILE_NAME, e.fileName);
        return result;
    }

    @ExceptionHandler(CleanupOperationIsOccurring.class)
    @ResponseStatus(HttpStatus.LOCKED)
    public @ResponseBody
    Map<String,Object> handleCleanupOperationIsOccurring(CleanupOperationIsOccurring e,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) {

        Map<String, Object> result = createResultMapWithMessage(
                HttpStatus.LOCKED,
                "The system is locked due to the execution of cleanup operations. Please try again later.");

        return result;
    }

    protected Map<String, Object> createResultMapWithMessage(
            HttpStatus httpStatus, String message) {

        Map<String, Object> result = new HashMap<>();
        result.put(HTTP_STATUS_CODE_KEY, httpStatus.value());
        result.put(REASON_PHRASE_KEY, httpStatus.getReasonPhrase());
        result.put(MESSAGE_KEY, message);
        return result;
    }

    private UUID logError(Throwable e) {
        
        UUID logEntryID = UUID.randomUUID();
        log.error("Log entry ID: " + logEntryID, e);
        return logEntryID;
    }
}
