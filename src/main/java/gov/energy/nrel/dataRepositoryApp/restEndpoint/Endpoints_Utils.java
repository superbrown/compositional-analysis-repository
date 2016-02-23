package gov.energy.nrel.dataRepositoryApp.restEndpoint;

import com.mongodb.util.JSON;
import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IDatasetBO;
import gov.energy.nrel.dataRepositoryApp.bo.IUtilsBO;
import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;
import gov.energy.nrel.dataRepositoryApp.utilities.Utilities;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.NotAnExcelWorkbook;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static gov.energy.nrel.dataRepositoryApp.utilities.HTTPResponseUtility.create_BAD_REQUEST_missingRequiredParam_response;
import static gov.energy.nrel.dataRepositoryApp.utilities.HTTPResponseUtility.create_SUCCESS_response;


@RestController
public class Endpoints_Utils extends EndpointController {

    protected static Logger log = Logger.getLogger(Endpoints_Utils.class);

    @Autowired
    protected DataRepositoryApplication dataRepositoryApplication;


    @RequestMapping(value="/api/v01/getNamesOfSheetsWithinExcelWorkbook", method = RequestMethod.POST)
    public ResponseEntity addDataset(
            @RequestParam(value = "workbook", required = true) MultipartFile workbook)
            throws IOException, NotAnExcelWorkbook, CleanupOperationIsOccurring {

        throwExceptionIfCleanupOperationsIsOccurring();

        if (workbook == null) {
            return create_BAD_REQUEST_missingRequiredParam_response("workbook");
        }

        FileAsRawBytes fileAsRawBytes = Utilities.toFileAsRawBytes(workbook);

        IUtilsBO utilsBO = dataRepositoryApplication.getBusinessObjects().getUtilsBO();

        List<String> namesOfSheetsWithinExcelWorkbook =
                utilsBO.getNamesOfSheetsWithinWorkbook(
                        workbook.getOriginalFilename(),
                        fileAsRawBytes);

        String json = JSON.serialize(namesOfSheetsWithinExcelWorkbook);

        return create_SUCCESS_response(json);
    }

    @RequestMapping(value="/api/v01/dropDatabaseAndReIngestAllDataFromOriginallyUploadedFiles", method = RequestMethod.GET)
    public synchronized ResponseEntity repopulateTheDatabase()
            throws IOException, CleanupOperationIsOccurring {

        synchronized (DataRepositoryApplication.cleanupOperationIsOccurring) {

            throwExceptionIfCleanupOperationsIsOccurring();
            DataRepositoryApplication.cleanupOperationIsOccurring = true;
        }

        try {

            IUtilsBO utilsBO = dataRepositoryApplication.getBusinessObjects().getUtilsBO();
            List<String> errors = utilsBO.repopulateDatabaseUsingFilesStoredOnServer();

            return create_SUCCESS_response(JSON.serialize(errors));
        }
        finally {

            DataRepositoryApplication.cleanupOperationIsOccurring = false;
        }
    }

    @RequestMapping(value="/api/v01/attemptToCleanupDataFromAllPreviouslyIncompleteDatasetUploads", method = RequestMethod.GET)
    public synchronized ResponseEntity attemptToCleanupDataFromAllPreviouslyIncompleteDatasetUploads() throws CleanupOperationIsOccurring {

        synchronized (DataRepositoryApplication.cleanupOperationIsOccurring) {

            throwExceptionIfCleanupOperationsIsOccurring();
            DataRepositoryApplication.cleanupOperationIsOccurring = true;
        }

        try {

            IDatasetBO datasetBO = dataRepositoryApplication.getBusinessObjects().getDatasetBO();
            List<String> errors = datasetBO.attemptToCleanupDataFromAllPreviouslyIncompleteDatasetUploads();

            return create_SUCCESS_response(JSON.serialize(errors));
        }
        finally {

            DataRepositoryApplication.cleanupOperationIsOccurring = false;
        }
    }
}
