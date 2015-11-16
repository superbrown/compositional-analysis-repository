package gov.energy.nbc.car.restEndpoint;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.bo.IRowBO;
import gov.energy.nbc.car.bo.TestMode;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static gov.energy.nbc.car.utilities.HTTPResponseUtility.create_NOT_FOUND_response;
import static gov.energy.nbc.car.utilities.HTTPResponseUtility.create_SUCCESS_response;


@RestController
public class Endpoints_Rows {

    protected Logger log = Logger.getLogger(getClass());

    private final IRowBO rowBO;
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("mm/dd/yyyy");

    public Endpoints_Rows() {

        rowBO = Application.getBusinessObjects().getRowBO();
    }

    @RequestMapping(value="/api/rows", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity getRows(
            @RequestBody String query,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String rows =
                rowBO.getRows(TestMode.value(testMode), query);

        if (rows == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(rows);
    }

//    @RequestMapping(value="/api/rows/all", method = RequestMethod.GET, produces = "application/json")
//    public ResponseEntity getAllRows(
//            @RequestParam(value = "inTestMode", required = false) String testMode) {
//
//        String rows = Application.getBusinessObjects().getRowBO().getAllRows(TestMode.value(testMode));
//
//        if (rows == null) {
//            return create_NOT_FOUND_response();
//        }
//
//        return create_SUCCESS_response(rows);
//    }

    @RequestMapping(value="/api/row/{rowId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getRow(
            @PathVariable(value = "rowId") String rowId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String row = rowBO.getRow(
                TestMode.value(testMode),
                rowId);

        if (row == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(row);
    }
}
