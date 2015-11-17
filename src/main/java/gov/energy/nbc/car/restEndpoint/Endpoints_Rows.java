package gov.energy.nbc.car.restEndpoint;

import gov.energy.nbc.car.app.AppSingleton;
import gov.energy.nbc.car.bo.IRowBO;
import gov.energy.nbc.car.app.TestMode;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static gov.energy.nbc.car.utilities.HTTPResponseUtility.create_NOT_FOUND_response;
import static gov.energy.nbc.car.utilities.HTTPResponseUtility.create_SUCCESS_response;


@RestController
public class Endpoints_Rows {

    protected Logger log = Logger.getLogger(getClass());

    @Autowired
    protected AppSingleton appSingleton;


    @RequestMapping(value="/api/rows", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity getRows(
            @RequestBody String query,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String rows = getiRowBO(testMode).getRows(query);

        if (rows == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(rows);
    }

    @RequestMapping(value="/api/row/{rowId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getRow(
            @PathVariable(value = "rowId") String rowId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String row = getiRowBO(testMode).getRow(rowId);

        if (row == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(row);
    }


    protected IRowBO getiRowBO(@RequestParam(value = "inTestMode", required = false) String testMode) {

        return appSingleton.getBusinessObjects(TestMode.value(testMode)).getRowBO();
    }
}
