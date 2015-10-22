package gov.energy.nbc.car.restService;

import gov.energy.nbc.car.businessService.BusinessServices;
import gov.energy.nbc.car.businessService.DeletionFailure;
import gov.energy.nbc.car.businessService.TestMode;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class Endpoints {

    protected Logger log = Logger.getLogger(getClass());

    public Endpoints() {

    }


    // S P R E A D S H E E T S

    @RequestMapping(value="/api/addSpreadsheet/", method = RequestMethod.POST)
    public ResponseEntity addSpreadsheet(
            @RequestParam(value = "json") String json,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String objectId = BusinessServices.spreadsheetService.addSpreadsheet(TestMode.value(testMode), json);
        return create_SUCCESS_response(objectId);
    }

    @RequestMapping(value="/api/spreadsheets/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getAllSpreadsheets(
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String spreadsheets = BusinessServices.spreadsheetService.getAllSpreadsheets(TestMode.value(testMode));

        if (spreadsheets == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(spreadsheets);
    }

    @RequestMapping(value="/api/spreadsheet/{spreadsheetId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSpreadsheet(
            @PathVariable(value = "spreadsheetId") String spreadsheetId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String spreadsheet = BusinessServices.spreadsheetService.getSpreadsheet(TestMode.value(testMode), spreadsheetId);

        if (spreadsheet == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(spreadsheet);
    }

    @RequestMapping(value="/api/spreadsheet/metadata/{spreadsheetId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSpreadsheetMetadata(
            @PathVariable(value = "spreadsheetId") String spreadsheetId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String spreadsheetMetadata = BusinessServices.spreadsheetService.getSpreadsheetMetadata(TestMode.value(testMode), spreadsheetId);

        if (spreadsheetMetadata == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(spreadsheetMetadata);
    }

    @RequestMapping(value="/api/spreadsheet/data/{spreadsheetId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSpreadsheetData(
            @PathVariable(value = "spreadsheetId") String spreadsheetId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String spreadsheetData = BusinessServices.spreadsheetService.getSpreadsheetData(TestMode.value(testMode), spreadsheetId);

        if (spreadsheetData == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(spreadsheetData);
    }

    @RequestMapping(value="/api/spreadsheet/{spreadsheetId}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity deleteSpreadsheetData(
            @PathVariable(value = "spreadsheetId") String spreadsheetId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        long numberOfObjectsDeleted = 0;
        try {
            numberOfObjectsDeleted = BusinessServices.spreadsheetService.deleteSpreadsheet(TestMode.value(testMode), spreadsheetId);
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

    // S P R E A D S H E E T   R O W S

    @RequestMapping(value="/api/spreadsheetRows", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity getSpreadsheetRows(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String spreadsheetRows = BusinessServices.spreadsheetRowsService.getSpreadsheetRows(TestMode.value(testMode), query);

        if (spreadsheetRows == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(spreadsheetRows);
    }

    @RequestMapping(value="/api/spreadsheetRows/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getAllSpreadsheetRows(
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String spreadsheetRows = BusinessServices.spreadsheetRowsService.getAllSpreadsheetRows(TestMode.value(testMode));

        if (spreadsheetRows == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(spreadsheetRows);
    }

    @RequestMapping(value="/api/spreadsheetRow/{spreadsheetRowId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSpreadsheetRow(
            @PathVariable(value = "spreadsheetRowId") String spreadsheetRowId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String spreadsheetRow = BusinessServices.spreadsheetRowsService.getSpreadsheetRow(
                TestMode.value(testMode),
                spreadsheetRowId);

        if (spreadsheetRow == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(spreadsheetRow);
    }

    // S A M P L E   T Y P E

    @RequestMapping(value="/api/sampleType/{sampleTypeId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSampleType(
            @PathVariable(value = "sampleTypeId") String sampleTypeId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String sampleType = BusinessServices.sampleTypeService.getSampleType(
                TestMode.value(testMode),
                sampleTypeId);

        if (sampleType == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(sampleType);
    }

    @RequestMapping(value="/api/sampleType/name/{sampleName}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSampleTypeByName(
            @PathVariable(value = "sampleName") String sampleName,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String sampleType = BusinessServices.sampleTypeService.getSampleTypeWithName(
                TestMode.value(testMode),
                sampleName);

        if (sampleType == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(sampleType);
    }

    @RequestMapping(value="/api/sampleTypes/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSampleTypeByName(
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String sampleType = BusinessServices.sampleTypeService.getAllSampleTypes(
                TestMode.value(testMode));

        if (sampleType == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(sampleType);
    }


    // T E S T   D A T A

    @RequestMapping(value="/api/seedTestData", method = RequestMethod.GET)
    public ResponseEntity seedTestData() {

        return create_SUCCESS_response(BusinessServices.testDataService.seedTestDataInTheDatabase());
    }

    @RequestMapping(value="/api/removeTestData", method = RequestMethod.GET)
    public ResponseEntity removeTestData() {

        BusinessServices.testDataService.removeTestData();
        return create_SUCCESS_response("{ message: \"test data successfully removed\" }");
    }

    @RequestMapping(value="/api/dropTestDatabase", method = RequestMethod.GET)
    public ResponseEntity dropTheTestDatabase() {

        BusinessServices.testDataService.dropTheTestDatabase();
        return create_SUCCESS_response("{ message: \"test database successfully dropped\" }");
    }

    private ResponseEntity create_SUCCESS_response(String body) {
        return new ResponseEntity(body, HttpStatus.OK);
    }

    private ResponseEntity create_NOT_FOUND_response() {
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    private ResponseEntity create_INTERNAL_SERVER_ERROR_response() {
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
