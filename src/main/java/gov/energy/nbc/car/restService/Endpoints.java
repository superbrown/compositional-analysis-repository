package gov.energy.nbc.car.restService;

import gov.energy.nbc.car.businessService.BusinessServices;
import gov.energy.nbc.car.businessService.TestMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class Endpoints {

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

    @RequestMapping(value="/api/getSpreadsheet/{spreadsheetId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSpreadsheet(
            @PathVariable(value = "spreadsheetId") String spreadsheetId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String spreadsheet = BusinessServices.spreadsheetService.getSpreadsheet(TestMode.value(testMode), spreadsheetId);

        if (spreadsheet == null) {
            return creaed_NOT_FOUND_response();
        }

        return create_SUCCESS_response(spreadsheet);
    }

    @RequestMapping(value="/api/getSpreadsheetMetadata/{spreadsheetId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSpreadsheetMetadata(
            @PathVariable(value = "spreadsheetId") String spreadsheetId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String spreadsheetMetadata = BusinessServices.spreadsheetService.getSpreadsheetMetadata(TestMode.value(testMode), spreadsheetId);

        if (spreadsheetMetadata == null) {
            return creaed_NOT_FOUND_response();
        }

        return create_SUCCESS_response(spreadsheetMetadata);
    }

    @RequestMapping(value="/api/getSpreadsheetData/{spreadsheetId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSpreadsheetData(
            @PathVariable(value = "spreadsheetId") String spreadsheetId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String spreadsheetData = BusinessServices.spreadsheetService.getSpreadsheetData(TestMode.value(testMode), spreadsheetId);

        if (spreadsheetData == null) {
            return creaed_NOT_FOUND_response();
        }

        return create_SUCCESS_response(spreadsheetData);
    }

    // S P R E A D S H E E T   R O W S

    @RequestMapping(value="/api/getSpreadsheetRows", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity getSpreadsheetRows(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String spreadsheetRows = BusinessServices.spreadsheetRowsService.getSpreadsheetRows(TestMode.value(testMode), query);

        if (spreadsheetRows == null) {
            return creaed_NOT_FOUND_response();
        }

        return create_SUCCESS_response(spreadsheetRows);
    }

    @RequestMapping(value="/api/getSpreadsheetRow/{spreadsheetRowId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSpreadsheetRow(
            @PathVariable(value = "spreadsheetRowId") String spreadsheetRowId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String spreadsheetRow = BusinessServices.spreadsheetRowsService.getSpreadsheetRow(TestMode.value(testMode), spreadsheetRowId);

        if (spreadsheetRow == null) {
            return creaed_NOT_FOUND_response();
        }

        return create_SUCCESS_response(spreadsheetRow);
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

    private ResponseEntity creaed_NOT_FOUND_response() {
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
