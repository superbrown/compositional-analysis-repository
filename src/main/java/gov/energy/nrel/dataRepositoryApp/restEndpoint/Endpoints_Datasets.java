package gov.energy.nrel.dataRepositoryApp.restEndpoint;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IDatasetBO;
import gov.energy.nrel.dataRepositoryApp.bo.IRowBO;
import gov.energy.nrel.dataRepositoryApp.bo.exception.DeletionFailure;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataset;
import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;
import gov.energy.nrel.dataRepositoryApp.model.IDatasetDocument;
import gov.energy.nrel.dataRepositoryApp.utilities.Utilities;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.DatasetReader_AllFileTypes;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.IDatasetReader_AllFileTypes;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.InvalidValueFoundInHeader;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

import static gov.energy.nrel.dataRepositoryApp.utilities.HTTPResponseUtility.*;


@RestController
public class Endpoints_Datasets {

    private static final int MS_IN_A_DAY = 24 * 60 * 60 * 1000;
    protected Logger log = Logger.getLogger(getClass());

    protected static final IDatasetReader_AllFileTypes GENERAL_FILE_READER = new DatasetReader_AllFileTypes();

    @Autowired
    protected DataRepositoryApplication dataRepositoryApplication;

    @RequestMapping(
            value="/api/addDataset",
            method = RequestMethod.POST,
            produces = "application/json")
    public ResponseEntity addDataset(
            HttpServletRequest request,
            @RequestParam(value = "dataCategory", required = false) String dataCategory,
            @RequestParam(value = "submissionDate", required = false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) Date submissionDate,
            @RequestParam(value = "submitter", required = false) String submitter,
            @RequestParam(value = "projectName", required = false) String projectName,
            @RequestParam(value = "chargeNumber", required = false) String chargeNumber,
            @RequestParam(value = "comments", required = false) String comments,
            @RequestParam(value = "sourceDocument", required = false) MultipartFile sourceDocument,
            @RequestParam(value = "nameOfSubdocumentContainingDataIfApplicable", required = false) String nameOfSubdocumentContainingDataIfApplicable) {

        if (StringUtils.isBlank(dataCategory)) { return create_BAD_REQUEST_missingRequiredParam_response("dataCategory");}
        if (submissionDate == null) { return create_BAD_REQUEST_missingRequiredParam_response("submissionDate");}
        if (sourceDocument == null) { return create_BAD_REQUEST_missingRequiredParam_response("sourceDocument");}
        if (isAnExcelFile(sourceDocument)) { if (StringUtils.isBlank(nameOfSubdocumentContainingDataIfApplicable)) { return create_BAD_REQUEST_missingRequiredParam_response("nameOfSubdocumentContainingDataIfApplicable");} }

        // This is a work-around due on not being able to figure out how to get Spring to inject a list of multipart
        // files.
        List<MultipartFile> attachments = extractAttachments((MultipartHttpServletRequest) request);

        // It appears there might be a bug in the Spring code, as the date is always unmarshalled to be a day behind
        // what the caller sent it.
        submissionDate.setTime(submissionDate.getTime() + MS_IN_A_DAY);

        String objectId = null;
        try {
            List<FileAsRawBytes> attachmentFilesAsRawBytes = Utilities.toFilesAsRawBytes(attachments);

            FileAsRawBytes dataFileAsRawBytes = Utilities.toFileAsRawBytes(sourceDocument);

            objectId = getDatasetBO().addDataset(
                    dataCategory,
                    submissionDate,
                    submitter,
                    projectName,
                    chargeNumber,
                    comments,
                    dataFileAsRawBytes,
                    nameOfSubdocumentContainingDataIfApplicable,
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
            value="/api/dataset/{datasetId}/sourceDocument",
            method = RequestMethod.GET)
    public  ResponseEntity<InputStreamResource> downloadDataset(
            @PathVariable(value = "datasetId") String datasetId) throws IOException {

        IDatasetBO datasetBO = getDatasetBO();

        File sourceDocument = datasetBO.getSourceDocument(datasetId);
        InputStream fileInputStream = new FileInputStream(sourceDocument.getAbsolutePath());
        InputStreamResource inputStreamResource = new InputStreamResource(fileInputStream);

        IDatasetDocument datasetDocument = datasetBO.getDatasetDAO().getDataset(datasetId);
        String originalFileName = datasetDocument.getMetadata().getSourceDocument().getOriginalFileName();

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .header("content-disposition", "attachment; filename=" + originalFileName)
                .body(inputStreamResource);
    }

    @RequestMapping(
            value="/api/dataset/{datasetId}/attachments",
            produces="application/zip",
            method = RequestMethod.GET)
    public  ResponseEntity<InputStreamResource> downloadAttachments(
            @PathVariable(value = "datasetId") String datasetId) throws IOException {

        IDatasetBO datasetBO = getDatasetBO();

        InputStream attachmentsInAZipFile = datasetBO.packageAttachmentsInAZipFile(datasetId);
        InputStreamResource inputStreamResource = new InputStreamResource(attachmentsInAZipFile);
        String zipFilename = "Attachments for dataset " + datasetId + ".zip";

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .header("content-disposition", "attachment; filename=" + zipFilename)
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

            try {
                numberOfObjectsDeleted = datasetBO.removeDataset(datasetId);
            }
            catch (UnknownDataset unknownDataset) {
                return create_NOT_FOUND_response();
            }
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


    private List<MultipartFile> extractAttachments(MultipartHttpServletRequest request) {

        // This is counting on the attachments being sent in with names that fit the following patter:
        //   attachment[0];
        //   attachment[1];
        //   attachment[2];
        //
        // The index values don't actually matter. What matters is that the name start with "attachment[".

        Map<String, MultipartFile> multipartFileMap = request.getFileMap();

        List<MultipartFile> attachments = new ArrayList<>();
        for (String key : multipartFileMap.keySet()) {

            if (key.startsWith("attachments[")) {
                MultipartFile attachment = multipartFileMap.get(key);
                attachments.add(attachment);
            }
        }

        return attachments;
    }



    protected IDatasetBO getDatasetBO() {

        return dataRepositoryApplication.getBusinessObjects().getDatasetBO();
    }

    protected IRowBO getRowBO() {

        return dataRepositoryApplication.getBusinessObjects().getRowBO();
    }

    protected boolean isAnExcelFile(MultipartFile sourceDocument) {

        return GENERAL_FILE_READER.isAnExcelFile(sourceDocument.getOriginalFilename());
    }
}
