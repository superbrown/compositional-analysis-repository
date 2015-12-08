package gov.energy.nbc.car.restEndpoint;

import gov.energy.nbc.car.app.DataRepositoryApplication;
import gov.energy.nbc.car.bo.IDataCategoryBO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static gov.energy.nbc.car.utilities.HTTPResponseUtility.create_NOT_FOUND_response;
import static gov.energy.nbc.car.utilities.HTTPResponseUtility.create_SUCCESS_response;


@RestController
public class Endpoints_DataCategories {

    protected Logger log = Logger.getLogger(getClass());

    @Autowired
    protected DataRepositoryApplication dataRepositoryApplication;

    @RequestMapping(value="/api/dataCategory/{dataCategoryId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getDataCategory(
            @PathVariable(value = "dataCategoryId") String dataCategoryId) {

        String dataCategory = getDataCategoryBO().getDataCategory(
                dataCategoryId);

        if (dataCategory == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(dataCategory);
    }

    @RequestMapping(value="/api/dataCategory", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getDataCategoryByName(
            @RequestParam(value = "dataCategoryName", required = true) String dataCategoryName) {

        String dataCategory = getDataCategoryBO().getDataCategoryWithName(
                dataCategoryName);

        if (dataCategory == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(dataCategory);
    }

    @RequestMapping(value="/api/dataCategory/columnNames", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getColumnNamesForDataCategoryName(
            @RequestParam(value = "dataCategoryName", required = true) String dataCategoryName) {

        String columnNamesForDataCategoryName = getDataCategoryBO().getColumnNamesForDataCategoryName(
                dataCategoryName);

        if (columnNamesForDataCategoryName == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(columnNamesForDataCategoryName);
    }

    @RequestMapping(value="/api/dataCategory/names/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getAllDataCategoryNames(
            ) {

        String dataCategoryNames = getDataCategoryBO().getAllDataCategoryNames(
        );

        if (dataCategoryNames == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(dataCategoryNames);
    }

    @RequestMapping(value="/api/dataCategories/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getDataCategoryByName(
            ) {

        String dataCategory = getDataCategoryBO().getAllDataCategories(
        );

        if (dataCategory == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(dataCategory);
    }


    protected IDataCategoryBO getDataCategoryBO() {

        return dataRepositoryApplication.getBusinessObjects().getDataCategoryBO();
    }
}
