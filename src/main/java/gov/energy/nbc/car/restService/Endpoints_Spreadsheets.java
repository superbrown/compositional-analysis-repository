package gov.energy.nbc.car.restService;

import gov.energy.nbc.car.businessObject.BusinessObjects;
import gov.energy.nbc.car.businessObject.DeletionFailure;
import gov.energy.nbc.car.businessObject.TestMode;
import gov.energy.nbc.car.businessObject.dto.FileAsRawBytes;
import gov.energy.nbc.car.fileReader.ExcelWorkbookReader;
import gov.energy.nbc.car.fileReader.InvalidValueFoundInHeader;
import gov.energy.nbc.car.fileReader.UnsupportedFileExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static gov.energy.nbc.car.utilities.HTTPResponseUtility.*;


@RestController
@MultipartConfig(
        location="/tmp",
        fileSizeThreshold=Endpoints_Spreadsheets.MEGABYTE,
        maxFileSize=Endpoints_Spreadsheets.MEGABYTE * 50,
        maxRequestSize=Endpoints_Spreadsheets.MEGABYTE * 5 * 50)
public class Endpoints_Spreadsheets {

    public static final int MEGABYTE = 1024 * 1024;

    protected Logger log = Logger.getLogger(getClass());
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("mm/dd/yyyy");

    public Endpoints_Spreadsheets() {

    }


//    @RequestMapping(value="/api/addSpreadsheet/", method = RequestMethod.PUT)
//    public ResponseEntity addSpreadsheet(
//            @RequestParam(value = "json") String json,
//            @RequestParam(value = "inTestMode", required = false) String testMode) {
//
//        String objectId = BusinessServices.spreadsheetService.addSpreadsheet(TestMode.value(testMode), json);
//        return create_SUCCESS_response(objectId);
//    }

    private static final ExcelWorkbookReader EXCEL_WORKBOOK_READER = new ExcelWorkbookReader();

    @RequestMapping(value="/api/addSpreadsheet", method = RequestMethod.POST)
    public ResponseEntity addSpreadsheet(
            @RequestParam(value = "sampleType", required = false) String sampleType,
            @RequestParam(value = "submissionDate", required = false) String submissionDate,
            @RequestParam(value = "submitter", required = false) String submitter,
            @RequestParam(value = "projectName", required = false) String projectName,
            @RequestParam(value = "chargeNumber", required = false) String chargeNumber,
            @RequestParam(value = "comments", required = false) String comments,
            @RequestParam(value = "dataFile", required = false) MultipartFile dataFile,
            @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments,
            @RequestParam(value = "nameOfSheetContainingData", required = false) String nameOfSheetContainingData,
            @RequestParam(value = "testMode", required = false) String testMode) {

        if (StringUtils.isBlank(sampleType)) { return create_BAD_REQUEST_missingRequiredParam_response("sampleType");}
        if (StringUtils.isBlank(submissionDate)) { return create_BAD_REQUEST_missingRequiredParam_response("submissionDate");}
        if (StringUtils.isBlank(submissionDate)) { return create_BAD_REQUEST_missingRequiredParam_response("submissionDate");}
        if (dataFile == null) { return create_BAD_REQUEST_missingRequiredParam_response("dataFile");}
        if (isAnExcelFile(dataFile)) { if (StringUtils.isBlank(nameOfSheetContainingData)) { return create_BAD_REQUEST_missingRequiredParam_response("nameOfSheetContainingData");} }

        Date submissionDate_date = null;
        try {
            submissionDate_date = DATE_FORMAT.parse(submissionDate);
        }
        catch (ParseException e) {
            return create_BAD_REQUEST_response("Invalid format for submissionDate. Must be conform to: " + DATE_FORMAT.toString() +
                    ". The value was " + submissionDate + ".");
        }

        String objectId = null;
        try {
            List<FileAsRawBytes> attachmentFiles = new ArrayList<>();
            for (MultipartFile attachment : attachments) {

                // DESIGN NOTE: I don't know why this is necessary, but for some reason
                //              attachment attributes sometimes are empty.
                if (StringUtils.isNotBlank(attachment.getOriginalFilename())) {
                    attachmentFiles.add(toFileAsRawBytes(attachment));
                }
            }

            objectId = BusinessObjects.spreadsheetBO.addSpreadsheet(
                    TestMode.value(testMode),
                    sampleType,
                    submissionDate_date,
                    submitter,
                    projectName,
                    chargeNumber,
                    comments,
                    toFileAsRawBytes(dataFile),
                    nameOfSheetContainingData,
                    attachmentFiles);
        }
        catch (UnsupportedFileExtension e) {
            log.info(e);
            return create_BAD_REQUEST_response(e.toString());
        }
        catch (InvalidValueFoundInHeader e) {
            log.info(e);
            return create_BAD_REQUEST_response(e.toString());
        }
        catch (IOException e) {
            log.error(e);
            return create_INTERNAL_SERVER_ERROR_response();
        }

        return create_SUCCESS_response(objectId);
    }

    protected boolean isAnExcelFile(@RequestParam(value = "dataFile", required = false) MultipartFile dataFile) {
        return EXCEL_WORKBOOK_READER.canReadFileWithExtension(dataFile.getOriginalFilename());
    }

    @RequestMapping(value="/api/spreadsheets/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getAllSpreadsheets(
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String spreadsheets = BusinessObjects.spreadsheetBO.getAllSpreadsheets(TestMode.value(testMode));

        if (spreadsheets == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(spreadsheets);
    }

    @RequestMapping(value="/api/spreadsheet/{spreadsheetId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSpreadsheet(
            @PathVariable(value = "spreadsheetId") String spreadsheetId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String spreadsheetMetadata = BusinessObjects.spreadsheetBO.getSpreadsheet(TestMode.value(testMode), spreadsheetId);

        if (spreadsheetMetadata == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(spreadsheetMetadata);
    }

    @RequestMapping(value="/api/spreadsheet/{spreadsheetId}/rows", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSpreadsheetRows(
            @PathVariable(value = "spreadsheetId") String spreadsheetId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String rowsForSpreadsheet = BusinessObjects.spreadsheetRowBO.getRowsForSpreadsheet(
                TestMode.value(testMode), spreadsheetId);

        if (rowsForSpreadsheet == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(rowsForSpreadsheet);
    }


    @RequestMapping(value="/api/spreadsheet/{spreadsheetId}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity deleteSpreadsheet(
            @PathVariable(value = "spreadsheetId") String spreadsheetId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        long numberOfObjectsDeleted = 0;
        try {
            numberOfObjectsDeleted = BusinessObjects.spreadsheetBO.deleteSpreadsheet(TestMode.value(testMode), spreadsheetId);
        }
        catch (DeletionFailure deletionFailure) {
            log.error(deletionFailure);
            return create_INTERNAL_SERVER_ERROR_response();
        }

        if (numberOfObjectsDeleted == 0) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response("{ message: " + numberOfObjectsDeleted + " objects deleted. }");
    }

    protected FileAsRawBytes toFileAsRawBytes(@RequestParam(value = "dataFile", required = false) MultipartFile dataFile)
            throws IOException {

        return new FileAsRawBytes(dataFile.getOriginalFilename(), dataFile.getBytes());
    }
}
