package gov.energy.nbc.car.restEndpoint;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.bo.IDataCategoryBO;
import gov.energy.nbc.car.bo.TestMode;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static gov.energy.nbc.car.utilities.HTTPResponseUtility.create_NOT_FOUND_response;
import static gov.energy.nbc.car.utilities.HTTPResponseUtility.create_SUCCESS_response;


@RestController
public class Endpoints_DataCategories {

    protected Logger log = Logger.getLogger(getClass());

    private IDataCategoryBO dataCategoryBO;

    public Endpoints_DataCategories() {

        dataCategoryBO = Application.getBusinessObjects().getDataCategoryBO();
    }

    @RequestMapping(value="/api/dataCategory/{dataCategoryId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getDataCategory(
            @PathVariable(value = "dataCategoryId") String dataCategoryId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String dataCategory = dataCategoryBO.getDataCategory(
                TestMode.value(testMode),
                dataCategoryId);

        if (dataCategory == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(dataCategory);
    }

    @RequestMapping(value="/api/dataCategory", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getDataCategoryByName(
            @RequestParam(value = "dataCategoryName", required = true) String dataCategoryName,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String dataCategory = dataCategoryBO.getDataCategoryWithName(
                TestMode.value(testMode),
                dataCategoryName);

        if (dataCategory == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(dataCategory);
    }

    @RequestMapping(value="/api/dataCategory/columnNames", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getColumnNamesForDataCategoryName(
            @RequestParam(value = "dataCategoryName", required = true) String dataCategoryName,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String columnNamesForDataCategoryName = dataCategoryBO.getColumnNamesForDataCategoryName(
                TestMode.value(testMode),
                dataCategoryName);

        if (columnNamesForDataCategoryName == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(columnNamesForDataCategoryName);
    }

    @RequestMapping(value="/api/dataCategory/names/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getAllDataCategoryNames(
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String dataCategoryNames = dataCategoryBO.getAllDataCategoryNames(
                TestMode.value(testMode));

        if (dataCategoryNames == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(dataCategoryNames);
    }

    @RequestMapping(value="/api/dataCategories/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getDataCategoryByName(
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String dataCategory = dataCategoryBO.getAllDataCategories(
                TestMode.value(testMode));

        if (dataCategory == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(dataCategory);
    }
}
