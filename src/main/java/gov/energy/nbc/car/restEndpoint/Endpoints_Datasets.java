package gov.energy.nbc.car.restEndpoint;

import gov.energy.nbc.car.app.DataRepositoryApplication;
import gov.energy.nbc.car.bo.IDatasetBO;
import gov.energy.nbc.car.bo.IRowBO;
import gov.energy.nbc.car.bo.exception.DeletionFailure;
import gov.energy.nbc.car.dao.dto.FileAsRawBytes;
import gov.energy.nbc.car.model.IDatasetDocument;
import gov.energy.nbc.car.utilities.fileReader.DatasetReader_AllFileTypes;
import gov.energy.nbc.car.utilities.fileReader.IDatasetReader_AllFileTypes;
import gov.energy.nbc.car.utilities.fileReader.exception.InvalidValueFoundInHeader;
import gov.energy.nbc.car.utilities.fileReader.exception.UnsupportedFileExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static gov.energy.nbc.car.utilities.HTTPResponseUtility.*;


@RestController
public class Endpoints_Datasets {

    private static final int ONE_DAY_IN_MS = 24 * 60 * 60 * 1000;
    protected Logger log = Logger.getLogger(getClass());

    @Autowired
    protected DataRepositoryApplication dataRepositoryApplication;


    @RequestMapping(
            value="/api/addDataset",
            method = RequestMethod.POST,
            produces = "application/json")
    public ResponseEntity addDataset(
            @RequestParam(value = "dataCategory", required = false) String dataCategory,
            @RequestParam(value = "submissionDate", required = false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) Date submissionDate,
            @RequestParam(value = "submitter", required = false) String submitter,
            @RequestParam(value = "projectName", required = false) String projectName,
            @RequestParam(value = "chargeNumber", required = false) String chargeNumber,
            @RequestParam(value = "comments", required = false) String comments,
            @RequestParam(value = "dataFile", required = false) MultipartFile dataFile,
            @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments,
            @RequestParam(value = "nameOfSheetContainingData", required = false) String nameOfSheetContainingData) {

        if (StringUtils.isBlank(dataCategory)) { return create_BAD_REQUEST_missingRequiredParam_response("dataCategory");}
        if (submissionDate == null) { return create_BAD_REQUEST_missingRequiredParam_response("submissionDate");}
        if (dataFile == null) { return create_BAD_REQUEST_missingRequiredParam_response("dataFile");}
        if (isAnExcelFile(dataFile)) { if (StringUtils.isBlank(nameOfSheetContainingData)) { return create_BAD_REQUEST_missingRequiredParam_response("nameOfSheetContainingData");} }

        // It appears there might be a bug in the Spring code, as the date is always unmarshalled to be a day behind
        // what the caller sent it.
        submissionDate.setTime(submissionDate.getTime() + ONE_DAY_IN_MS);

                String objectId = null;
        try {
            List<FileAsRawBytes> attachmentFilesAsRawBytes = new ArrayList<>();
            for (MultipartFile attachment : attachments) {

                // DESIGN NOTE: I don't know why this is necessary, but for some reason
                //              attachment attributes sometimes are empty.
                if (StringUtils.isNotBlank(attachment.getOriginalFilename())) {
                    attachmentFilesAsRawBytes.add(toFileAsRawBytes(attachment));
                }
            }

            FileAsRawBytes dataFileAsRawBytes = toFileAsRawBytes(dataFile);

            objectId = getDatasetBO().addDataset(
                    dataCategory,
                    submissionDate,
                    submitter,
                    projectName,
                    chargeNumber,
                    comments,
                    dataFileAsRawBytes,
                    nameOfSheetContainingData,
                    attachmentFilesAsRawBytes);
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

//    @RequestMapping(
//            value="/api/seedBigData",
//            method = RequestMethod.POST)
//    public ResponseEntity seedBigData(
//            @RequestParam(value = "dataCategory", required = false) String dataCategory,
//            @RequestParam(value = "submissionDate", required = false) String submissionDate,
//            @RequestParam(value = "submitter", required = false) String submitter,
//            @RequestParam(value = "projectName", required = false) String projectName,
//            @RequestParam(value = "chargeNumber", required = false) String chargeNumber,
//            @RequestParam(value = "comments", required = false) String comments,
//            @RequestParam(value = "dataFile", required = false) MultipartFile dataFile,
//            @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments,
//            @RequestParam(value = "nameOfSheetContainingData", required = false) String nameOfSheetContainingData) {
//
//        if (StringUtils.isBlank(dataCategory)) { return create_BAD_REQUEST_missingRequiredParam_response("dataCategory");}
//        if (StringUtils.isBlank(submissionDate)) { return create_BAD_REQUEST_missingRequiredParam_response("submissionDate");}
//        if (StringUtils.isBlank(submissionDate)) { return create_BAD_REQUEST_missingRequiredParam_response("submissionDate");}
//        if (dataFile == null) { return create_BAD_REQUEST_missingRequiredParam_response("dataFile");}
//        if (isAnExcelFile(dataFile)) { if (StringUtils.isBlank(nameOfSheetContainingData)) { return create_BAD_REQUEST_missingRequiredParam_response("nameOfSheetContainingData");} }
//
//        Date submissionDate_date = null;
//        try {
//            submissionDate_date = DATE_FORMAT.parse(submissionDate);
//        }
//        catch (ParseException e) {
//            return create_BAD_REQUEST_response("Invalid format for submissionDate. Must be conform to: " + DATE_FORMAT.toString() +
//                    ". The value was " + submissionDate + ".");
//        }
//
//        String objectId = null;
//        try {
//
//            IRowDAO rowDAO = getDatasetBO().getDatasetDAO().getRowDAO();
//
//            while (rowDAO.getCellDAO(null).getCount() <= 1000000) {
//
//                List<FileAsRawBytes> attachmentFilesAsRawBytes = new ArrayList<>();
//                for (MultipartFile attachment : attachments) {
//
//                    // DESIGN NOTE: I don't know why this is necessary, but for some reason
//                    //              attachment attributes sometimes are empty.
//                    if (StringUtils.isNotBlank(attachment.getOriginalFilename())) {
//                        attachmentFilesAsRawBytes.add(toFileAsRawBytes(attachment));
//                    }
//                }
//
//                FileAsRawBytes dataFileAsRawBytes = toFileAsRawBytes(dataFile);
//
//                objectId = getDatasetBO().addDataset(
//                        dataCategory,
//                        submissionDate_date,
//                        submitter,
//                        projectName,
//                        chargeNumber,
//                        comments,
//                        dataFileAsRawBytes,
//                        nameOfSheetContainingData,
//                        attachmentFilesAsRawBytes);
//            }
//        }
//        catch (UnsupportedFileExtension e) {
//            log.info(e);
//            return create_BAD_REQUEST_response(e.toString());
//        }
//        catch (InvalidValueFoundInHeader e) {
//            log.info(e);
//            return create_BAD_REQUEST_response(e.toString());
//        }
//        catch (IOException e) {
//            log.error(e);
//            return create_INTERNAL_SERVER_ERROR_response();
//        }
//
//        return create_SUCCESS_response(objectId);
//    }

    @RequestMapping(
            value="/api/datasets/all",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity getAllDatasets() {

        IDatasetBO datasetBO = getDatasetBO();

        String datasets = datasetBO.getAllDatasets();

        if (datasets == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(datasets);
    }

    @RequestMapping(
            value="/api/dataset/{datasetId}",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity getDataset(
            @PathVariable(value = "datasetId") String datasetId) {

        IDatasetBO datasetBO = getDatasetBO();

        String dataset = datasetBO.getDataset(datasetId);

        if (dataset == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(dataset);
    }

    @RequestMapping(
            value="/api/dataset/{datasetId}/uploadedFile",
            method = RequestMethod.GET)
    public  ResponseEntity<InputStreamResource> downloadDataset(
            @PathVariable(value = "datasetId") String datasetId) throws IOException {

        IDatasetBO datasetBO = getDatasetBO();

        IDatasetDocument originallydatasetDocument = datasetBO.getDatasetDAO().getDataset(datasetId);
        String originalFileName = originallydatasetDocument.getMetadata().getUploadedFile().getOriginalFileName();

        File uploadedFile = datasetBO.getUploadedFile(datasetId);
        InputStream fileInputStream = new FileInputStream(uploadedFile.getAbsolutePath());
        InputStreamResource inputStreamResource = new InputStreamResource(fileInputStream);

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .header("content-disposition", "attachment; filename=" + originalFileName)
                .body(inputStreamResource);
    }

    @RequestMapping(
            value="/api/dataset/{datasetId}/rows",
            method = RequestMethod.GET,
            produces = "application/binary")
    public ResponseEntity getRows(
            @PathVariable(value = "datasetId") String datasetId) {

        String rowsForDataset = getRowBO().getRowsAssociatedWithDataset(datasetId);

        if (rowsForDataset == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(rowsForDataset);
    }

    @RequestMapping(
            value="/api/dataset/{datasetId}",
            method = RequestMethod.DELETE,
            produces = "application/json")
    public ResponseEntity deleteDataset(
            @PathVariable(value = "datasetId") String datasetId) {

        long numberOfObjectsDeleted = 0;
        try {
            IDatasetBO datasetBO = getDatasetBO();
            numberOfObjectsDeleted = datasetBO.removeDataset(datasetId);
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


    protected IDatasetBO getDatasetBO() {
        return dataRepositoryApplication.getBusinessObjects().getDatasetBO();
    }

    protected IRowBO getRowBO() {
        return dataRepositoryApplication.getBusinessObjects().getRowBO();
    }

    protected FileAsRawBytes toFileAsRawBytes(MultipartFile dataFile)
            throws IOException {

        return new FileAsRawBytes(dataFile.getOriginalFilename(), dataFile.getBytes());
    }

    protected static final IDatasetReader_AllFileTypes GENERAL_FILE_READER = new DatasetReader_AllFileTypes();

    protected boolean isAnExcelFile(MultipartFile dataFile) {

        return GENERAL_FILE_READER.isAnExcelFile(dataFile.getOriginalFilename());
    }
}
