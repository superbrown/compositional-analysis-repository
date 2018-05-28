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
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static Logger log = Logger.getLogger(GlobalExceptionHandler.class);

    private static final String KEY__HTTP_STATUS_CODE = "status";
    private static final String KEY__REASON_PHRASE = "reasonPhrase";
    private static final String KEY__MESSAGE = "message";
    private static final String KEY__ROW_NUMBER = "rowNumber";
    private static final String KEY__COLUMN_NUMBER = "columnNumber";
    private static final String KEY__SANITIZED_VALUE = "sanitizedValue";
    private static final String KEY__FILE_NAME = "fileName";
    private static final String KEY__INVALID_NAME = "invalidName";
    private static final String KEY__PARAMETER_NAME = "parameterName";


    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String,Object> handleGenericException(Throwable e,
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
    @ResponseBody
    public Map<String,Object> handleFailedToSave(FailedToSave e,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {

        return handleGenericException(e, request, response);
    }

    @ExceptionHandler(FailedToDeleteFiles.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String,Object> handleFailedToDeleteFiles(FailedToDeleteFiles e,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {

        return handleGenericException(e, request, response);
    }

    @ExceptionHandler(FailedToExtractDataFromFile.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String,Object> handleFailedToDeleteFiles(FailedToExtractDataFromFile e,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {

        return handleGenericException(e, request, response);
    }

    @ExceptionHandler(UnknownDataset.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String,Object> handleUnknownDataset(UnknownDataset e,
                                            HttpServletRequest request, 
                                            HttpServletResponse response) {

        return createResultMapWithMessage(
                HttpStatus.NOT_FOUND,
                "The endpoint is valid, but the particular dataset is unknown: " + e.getId());
    }

    @ExceptionHandler(UnknownRow.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String,Object> handleUnknownDataCatogory(UnknownRow e,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {

        return createResultMapWithMessage(
                HttpStatus.NOT_FOUND,
                "The endpoint is valid, but the particular row is unknown: " + e.getId());
    }

    @ExceptionHandler(UnknownDataCatogory.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String,Object> handleUnknownDataCatogory(UnknownDataCatogory e,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {

        return createResultMapWithMessage(
                HttpStatus.NOT_FOUND,
                "The endpoint is valid, but the particular data category is unknown: " + e.getId());
    }

    @ExceptionHandler(DataCategoryAlreadyExists.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Map<String,Object> handleDataCategoryAlreadyExists(DataCategoryAlreadyExists e,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response) {

        return createResultMapWithMessage(
                HttpStatus.CONFLICT,
                "That data category, " + e.getId() + ", already exists.");
    }

    @ExceptionHandler(FileContainsInvalidColumnName.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public Map<String,Object> handleFileContainsInvalidColumnName(FileContainsInvalidColumnName e,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response) {

        Map<String, Object> result = createResultMapWithMessage(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "The file, " + e.fileName + ", contains an invalid column name. " +
                        "Column " + e.columnNumber + " has the name '" + e.columnName + "'.");

        result.put(KEY__FILE_NAME, e.fileName);
        result.put(KEY__COLUMN_NUMBER, e.columnNumber);
        result.put(KEY__INVALID_NAME, e.columnName);
        return result;
    }

    @ExceptionHandler(UnsanitaryRequestParameter.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public Map<String,Object> handleFileContainsInvalidColumnName(UnsanitaryRequestParameter e,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response) {

        Map<String, Object> result = createResultMapWithMessage(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "The parameter, " + e.paramaterName + ", contains an invalid value.");

        result.put(KEY__PARAMETER_NAME, e.paramaterName);
        return result;
    }

    @ExceptionHandler(UnsanitaryData.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public Map<String,Object> handleFileContainsInvalidColumnName(UnsanitaryData e,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response) {

        Map<String, Object> result = createResultMapWithMessage(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "The file contains data that could potentially be malicious. " +
                        "The first encounter of such data (there may " +
                        "be additional examples) is located in row " + e.rowNumber + ", " +
                        "column " + e.columnNumber + ". " +
                        "Its \"sanitized\" value is: " + e.sanitizedValue);

        result.put(KEY__ROW_NUMBER, e.rowNumber);
        result.put(KEY__COLUMN_NUMBER, e.columnNumber);
        result.put(KEY__SANITIZED_VALUE, e.sanitizedValue);
        return result;
    }

    @ExceptionHandler(UnsupportedFileExtension.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public Map<String,Object> handleFileContainsInvalidColumnName(UnsupportedFileExtension e,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response) {

        Map<String, Object> result = createResultMapWithMessage(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "The file, " + e.fileName + ", is a type that is not supported.");

        result.put(KEY__FILE_NAME, e.fileName);
        return result;
    }

    @ExceptionHandler(NotAnExcelWorkbook.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public Map<String,Object> handleNotAnExcelWorkbook(NotAnExcelWorkbook e,
                                                HttpServletRequest request,
                                                HttpServletResponse response) {

        Map<String, Object> result = createResultMapWithMessage(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "The file, " + e.fileName + ", is not an Excel workbook.");

        result.put(KEY__FILE_NAME, e.fileName);
        return result;
    }

    @ExceptionHandler(CleanupOperationIsOccurring.class)
    @ResponseStatus(HttpStatus.LOCKED)
    @ResponseBody
    public Map<String,Object> handleCleanupOperationIsOccurring(CleanupOperationIsOccurring e,
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
        result.put(KEY__HTTP_STATUS_CODE, httpStatus.value());
        result.put(KEY__REASON_PHRASE, httpStatus.getReasonPhrase());
        result.put(KEY__MESSAGE, message);
        return result;
    }

    private UUID logError(Throwable e) {
        
        UUID logEntryID = UUID.randomUUID();
        log.error("Log entry ID: " + logEntryID, e);
        return logEntryID;
    }
}
