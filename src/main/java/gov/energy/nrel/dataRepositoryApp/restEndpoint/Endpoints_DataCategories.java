package gov.energy.nrel.dataRepositoryApp.restEndpoint;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IDataCategoryBO;
import gov.energy.nrel.dataRepositoryApp.bo.exception.DataCategoryAlreadyExists;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataCatogory;
import gov.energy.nrel.dataRepositoryApp.utilities.AbsValueSanitizer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static gov.energy.nrel.dataRepositoryApp.utilities.HTTPResponseUtility.create_SUCCESS_response;


@RestController
public class Endpoints_DataCategories extends EndpointController {

    protected static Logger log = Logger.getLogger(Endpoints_DataCategories.class);

    @Autowired
    protected DataRepositoryApplication dataRepositoryApplication;

    @RequestMapping(
            value="/api/v01/dataCategory/{dataCategoryId}",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity getDataCategory(
            @PathVariable(value = "dataCategoryId") String dataCategoryId)
            throws UnknownDataCatogory, CleanupOperationIsOccurring {

        throwExceptionIfCleanupOperationsIsOccurring();

        // not certain this is necessary, but doing as a precaution
        dataCategoryId = getValueSanitizer().sanitize(dataCategoryId);

        String dataCategory = getDataCategoryBO().getDataCategory(dataCategoryId);
        return create_SUCCESS_response(dataCategory);
    }

    @RequestMapping(
            value="/api/v01/dataCategory",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity getDataCategoryByName(
            @RequestParam(value = "dataCategoryName", required = true) String dataCategoryName)
            throws UnknownDataCatogory, CleanupOperationIsOccurring {

        throwExceptionIfCleanupOperationsIsOccurring();

        // not certain this is necessary, but doing as a precaution
        dataCategoryName = getValueSanitizer().sanitize(dataCategoryName);

        String dataCategory = getDataCategoryBO().getDataCategoryWithName(dataCategoryName);
        return create_SUCCESS_response(dataCategory);
    }

    @RequestMapping(
            value="/api/v01/dataCategory/searchableColumnNames",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity getSearchableColumnNames(
            @RequestParam(value = "dataCategoryName", required = true) String dataCategoryName)
            throws UnknownDataCatogory, CleanupOperationIsOccurring {

        throwExceptionIfCleanupOperationsIsOccurring();

        // not certain this is necessary, but doing as a precaution
        dataCategoryName = getValueSanitizer().sanitize(dataCategoryName);

        String columnNamesForDataCategoryName =
                getDataCategoryBO().getSearchableColumnNamesForDataCategoryName(dataCategoryName);
        return create_SUCCESS_response(columnNamesForDataCategoryName);
    }

    @RequestMapping(
            value="/api/v01/dataCategory/names/all",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity getAllDataCategoryNames() throws CleanupOperationIsOccurring {

        throwExceptionIfCleanupOperationsIsOccurring();

        String dataCategoryNames = getDataCategoryBO().getAllDataCategoryNames();
        return create_SUCCESS_response(dataCategoryNames);
    }

    @RequestMapping(
            value="/api/v01/dataCategories/all",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity getDataCategoryByName() throws CleanupOperationIsOccurring {

        throwExceptionIfCleanupOperationsIsOccurring();

        String dataCategory = getDataCategoryBO().getAllDataCategories();
        return create_SUCCESS_response(dataCategory);
    }

    @RequestMapping(
            value="/api/v01/addDataCategory",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity addDataCategory(
            @RequestParam(value = "name") String dataCategoryName)
            throws DataCategoryAlreadyExists, CleanupOperationIsOccurring {

        throwExceptionIfCleanupOperationsIsOccurring();

        getDataCategoryBO().addDataCategory(dataCategoryName);

        String dataCategory = null;
        try {
            dataCategory = getDataCategoryBO().getDataCategoryWithName(dataCategoryName);
        }
        catch (UnknownDataCatogory e) {
            throw new RuntimeException(
                    "Data category appeared to be created, but could not be found afterwards: " +
                            dataCategoryName);
        }

        return create_SUCCESS_response(dataCategory);
    }

    protected IDataCategoryBO getDataCategoryBO() {

        return dataRepositoryApplication.getBusinessObjects().getDataCategoryBO();
    }

    protected AbsValueSanitizer getValueSanitizer() {

        return dataRepositoryApplication.getValueSanitizer();
    }
}
