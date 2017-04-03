package gov.energy.nrel.dataRepositoryApp.restEndpoint;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IDataCategoryBO;
import gov.energy.nrel.dataRepositoryApp.bo.exception.DataCategoryAlreadyExists;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataCatogory;
import gov.energy.nrel.dataRepositoryApp.utilities.valueSanitizer.IValueSanitizer;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static gov.energy.nrel.dataRepositoryApp.utilities.HTTPResponseUtility.create_SUCCESS_response;


@RestController
public class Endpoints_DataCategories extends AbstractEndpoints {

    protected static Logger log = Logger.getLogger(Endpoints_DataCategories.class);

    @Autowired
    protected DataRepositoryApplication dataRepositoryApplication;

    @RequestMapping(
            value="/api/v02/dataCategories/{dataCategoryId}",
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
            value="/api/v02/dataCategories",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity getDataCategoryByName(
            @RequestParam(value = "dataCategoryName", required = false) String dataCategoryName)
            throws UnknownDataCatogory, CleanupOperationIsOccurring {

        throwExceptionIfCleanupOperationsIsOccurring();

        if (StringUtils.isNotBlank(dataCategoryName)) {

            // not certain this is necessary, but doing as a precaution
            dataCategoryName = getValueSanitizer().sanitize(dataCategoryName);

            String dataCategory = getDataCategoryBO().getDataCategoryWithName(dataCategoryName);
            return create_SUCCESS_response(dataCategory);
        }
        else {

            String dataCategory = getDataCategoryBO().getAllDataCategories();
            return create_SUCCESS_response(dataCategory);
        }
    }

    @RequestMapping(
            value="/api/v02/dataCategories/searchableColumnNames",
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
            value="/api/v02/dataCategories/names",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity getAllDataCategoryNames() throws CleanupOperationIsOccurring {

        throwExceptionIfCleanupOperationsIsOccurring();

        String dataCategoryNames = getDataCategoryBO().getAllDataCategoryNames();
        return create_SUCCESS_response(dataCategoryNames);
    }

    @RequestMapping(
            value="/api/v02/addDataCategory",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity addDataCategory(
            @RequestParam(value = "name") String dataCategoryName)
            throws DataCategoryAlreadyExists, CleanupOperationIsOccurring {

        // NOTE: This has been made GET operations for ease of use.

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

    protected IValueSanitizer getValueSanitizer() {

        return dataRepositoryApplication.getValueSanitizer();
    }
}
