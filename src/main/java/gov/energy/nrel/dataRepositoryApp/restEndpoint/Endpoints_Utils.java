package gov.energy.nrel.dataRepositoryApp.restEndpoint;

import com.mongodb.util.JSON;
import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IDatasetBO;
import gov.energy.nrel.dataRepositoryApp.bo.IUtilsBO;
import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;
import gov.energy.nrel.dataRepositoryApp.utilities.Utilities;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;
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

import static gov.energy.nrel.dataRepositoryApp.utilities.HTTPResponseUtility.*;


@RestController
public class Endpoints_Utils {

    protected static Logger log = Logger.getLogger(Endpoints_Utils.class);

    @Autowired
    protected DataRepositoryApplication dataRepositoryApplication;


    @RequestMapping(value="/api/v01/getNamesOfSheetsWithinExcelWorkbook", method = RequestMethod.POST)
    public ResponseEntity addDataset(
            @RequestParam(value = "workbook", required = true) MultipartFile workbook)
            throws IOException {

        if (workbook == null) {
            return create_BAD_REQUEST_missingRequiredParam_response("workbook");
        }

        FileAsRawBytes fileAsRawBytes = Utilities.toFileAsRawBytes(workbook);


        List<String> namesOfSheetsWithinExcelWorkbook = null;
        try {
            IUtilsBO utilsBO = dataRepositoryApplication.getBusinessObjects().getUtilsBO();

            namesOfSheetsWithinExcelWorkbook = utilsBO.getNamesOfSheetsWithinWorkbook(
                    workbook.getOriginalFilename(),
                    fileAsRawBytes);
        }
        catch (UnsupportedFileExtension e) {
            log.info(e);
            return create_BAD_REQUEST_response(e.toString());
        }

        String json = JSON.serialize(namesOfSheetsWithinExcelWorkbook);

        return create_SUCCESS_response(json);
    }

    @RequestMapping(value="/api/v01/dropDatabaseAndReIngestAllDataFromOriginallyUploadedFiles", method = RequestMethod.GET)
    public ResponseEntity repolulateTheDatabase()
            throws IOException {

        IUtilsBO utilsBO = dataRepositoryApplication.getBusinessObjects().getUtilsBO();
        List<String> errors = utilsBO.repopulateDatabaseUsingFilesStoredOnServer();

        return create_SUCCESS_response(JSON.serialize(errors));
    }

    @RequestMapping(value="/api/v01/attemptToCleanupDataFromAllPreviouslyIncompleteDatasetUploads", method = RequestMethod.GET)
    public ResponseEntity attemptToCleanupDataFromAllPreviouslyIncompleteDatasetUploads()
    {
        IDatasetBO datasetBO = dataRepositoryApplication.getBusinessObjects().getDatasetBO();
        List<String> errors = datasetBO.attemptToCleanupDataFromAllPreviouslyIncompleteDatasetUploads();

        return create_SUCCESS_response(JSON.serialize(errors));
    }
}
