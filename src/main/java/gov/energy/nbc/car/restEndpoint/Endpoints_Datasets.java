package gov.energy.nbc.car.restEndpoint;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.bo.IDatasetBO;
import gov.energy.nbc.car.bo.TestMode;
import gov.energy.nbc.car.bo.exception.DeletionFailure;
import gov.energy.nbc.car.dao.dto.FileAsRawBytes;
import gov.energy.nbc.car.utilities.fileReader.DatasetReader_AllFileTypes;
import gov.energy.nbc.car.utilities.fileReader.IDatasetReader_AllFileTypes;
import gov.energy.nbc.car.utilities.fileReader.exception.InvalidValueFoundInHeader;
import gov.energy.nbc.car.utilities.fileReader.exception.UnsupportedFileExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static gov.energy.nbc.car.utilities.HTTPResponseUtility.*;


@RestController
public class Endpoints_Datasets {

    private final IDatasetBO datasetBO;
    protected Logger log = Logger.getLogger(getClass());
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("mm/dd/yyyy");

    public Endpoints_Datasets() {

        datasetBO = Application.getBusinessObjects().getDatasetBO();
    }


//    @RequestMapping(value="/api/addDataset/", method = RequestMethod.PUT)
//    public ResponseEntity addDataset(
//            @RequestParam(value = "json") String json,
//            @RequestParam(value = "inTestMode", required = false) String testMode) {
//
//        String objectId = BusinessServices.datasetService.addDataset(TestMode.value(testMode), json);
//        return create_SUCCESS_response(objectId);
//    }

    @RequestMapping(value="/api/addDataset", method = RequestMethod.POST)
    public ResponseEntity addDataset(
            @RequestParam(value = "dataCategory", required = false) String dataCategory,
            @RequestParam(value = "submissionDate", required = false) String submissionDate,
            @RequestParam(value = "submitter", required = false) String submitter,
            @RequestParam(value = "projectName", required = false) String projectName,
            @RequestParam(value = "chargeNumber", required = false) String chargeNumber,
            @RequestParam(value = "comments", required = false) String comments,
            @RequestParam(value = "dataFile", required = false) MultipartFile dataFile,
            @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments,
            @RequestParam(value = "nameOfSheetContainingData", required = false) String nameOfSheetContainingData,
            @RequestParam(value = "testMode", required = false) String testMode) {

        if (StringUtils.isBlank(dataCategory)) { return create_BAD_REQUEST_missingRequiredParam_response("dataCategory");}
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

            objectId = datasetBO.addDataset(
                    TestMode.value(testMode),
                    dataCategory,
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

    private static final IDatasetReader_AllFileTypes GENERAL_FILE_READER = new DatasetReader_AllFileTypes();

    protected boolean isAnExcelFile(@RequestParam(value = "dataFile", required = false) MultipartFile dataFile) {

        return GENERAL_FILE_READER.isAnExcelFile(dataFile.getOriginalFilename());
    }

    @RequestMapping(value="/api/datasets/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getAllDatasets(
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String datasets = datasetBO.getAllDatasets(TestMode.value(testMode));

        if (datasets == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(datasets);
    }

    @RequestMapping(value="/api/dataset/{datasetId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getDataset(
            @PathVariable(value = "datasetId") String datasetId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String dataset = datasetBO.getDataset(TestMode.value(testMode), datasetId);

        if (dataset == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(dataset);
    }

    @RequestMapping(value="/api/dataset/{datasetId}/rows", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getRows(
            @PathVariable(value = "datasetId") String datasetId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String rowsForDataset = Application.getBusinessObjects().getRowBO().getRowAssociatedWithDataset(
                TestMode.value(testMode), datasetId);

        if (rowsForDataset == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(rowsForDataset);
    }


    @RequestMapping(value="/api/dataset/{datasetId}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity deleteDataset(
            @PathVariable(value = "datasetId") String datasetId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        long numberOfObjectsDeleted = 0;
        try {
            numberOfObjectsDeleted = datasetBO.deleteDataset(TestMode.value(testMode), datasetId);
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
