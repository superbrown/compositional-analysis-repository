package gov.energy.nbc.car.restService;

import gov.energy.nbc.car.businessObject.BusinessObjects;
import gov.energy.nbc.car.businessObject.TestMode;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static gov.energy.nbc.car.utilities.HTTPResponseUtility.*;


@RestController
public class Endpoints_SpreadsheetRows {

    protected Logger log = Logger.getLogger(getClass());
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("mm/dd/yyyy");

    public Endpoints_SpreadsheetRows() {

    }

    @RequestMapping(value="/api/spreadsheetRows", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity getSpreadsheetRows(
            @RequestBody String query,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String spreadsheetRows =
                BusinessObjects.spreadsheetRowBO.getSpreadsheetRows(TestMode.value(testMode), query);

        if (spreadsheetRows == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(spreadsheetRows);
    }

    @RequestMapping(value="/api/spreadsheetRows/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getAllSpreadsheetRows(
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String spreadsheetRows = BusinessObjects.spreadsheetRowBO.getAllSpreadsheetRows(TestMode.value(testMode));

        if (spreadsheetRows == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(spreadsheetRows);
    }

    @RequestMapping(value="/api/spreadsheetRow/{spreadsheetRowId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSpreadsheetRow(
            @PathVariable(value = "spreadsheetRowId") String spreadsheetRowId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String spreadsheetRow = BusinessObjects.spreadsheetRowBO.getSpreadsheetRow(
                TestMode.value(testMode),
                spreadsheetRowId);

        if (spreadsheetRow == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(spreadsheetRow);
    }
}
