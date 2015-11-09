package gov.energy.nbc.car.restService;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.businessObject.TestMode;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static gov.energy.nbc.car.utilities.HTTPResponseUtility.create_NOT_FOUND_response;
import static gov.energy.nbc.car.utilities.HTTPResponseUtility.create_SUCCESS_response;


@RestController
public class Endpoints_DataCategories {

    protected Logger log = Logger.getLogger(getClass());

    public Endpoints_DataCategories() {

    }

    @RequestMapping(value="/api/dataCategory/{dataCategoryId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getDataCategory(
            @PathVariable(value = "dataCategoryId") String dataCategoryId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String dataCategory = Application.getBusinessObjects().getDataCategoryBO().getDataCategory(
                TestMode.value(testMode),
                dataCategoryId);

        if (dataCategory == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(dataCategory);
    }

    @RequestMapping(value="/api/dataCategory/name/{sampleName}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getDataCategoryByName(
            @PathVariable(value = "sampleName") String sampleName,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String dataCategory = Application.getBusinessObjects().getDataCategoryBO().getDataCategoryWithName(
                TestMode.value(testMode),
                sampleName);

        if (dataCategory == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(dataCategory);
    }

    @RequestMapping(value="/api/dataCategories/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getDataCategoryByName(
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String dataCategory = Application.getBusinessObjects().getDataCategoryBO().getAllDataCategories(
                TestMode.value(testMode));

        if (dataCategory == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(dataCategory);
    }
}
