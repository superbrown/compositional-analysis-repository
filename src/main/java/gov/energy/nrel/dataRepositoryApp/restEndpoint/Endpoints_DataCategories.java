package gov.energy.nrel.dataRepositoryApp.restEndpoint;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IDataCategoryBO;
import gov.energy.nrel.dataRepositoryApp.bo.exception.DataCategoryAlreadyExists;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataCatogory;
import gov.energy.nrel.dataRepositoryApp.utilities.ValueScrubbingHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static gov.energy.nrel.dataRepositoryApp.utilities.HTTPResponseUtility.create_INTERNAL_SERVER_ERROR_response;
import static gov.energy.nrel.dataRepositoryApp.utilities.HTTPResponseUtility.create_NOT_FOUND_response;
import static gov.energy.nrel.dataRepositoryApp.utilities.HTTPResponseUtility.create_SUCCESS_response;


@RestController
public class Endpoints_DataCategories {

    protected static Logger log = Logger.getLogger(Endpoints_DataCategories.class);

    @Autowired
    protected DataRepositoryApplication dataRepositoryApplication;

    @RequestMapping(
            value="/api/dataCategory/{dataCategoryId}",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity getDataCategory(
            @PathVariable(value = "dataCategoryId") String dataCategoryId) {

        try {
            ValueScrubbingHelper valueScrubbingHelper = getValueScrubbingHelper();
            dataCategoryId = valueScrubbingHelper.scrubValue(dataCategoryId);

            String dataCategory = getDataCategoryBO().getDataCategory(dataCategoryId);
            return create_SUCCESS_response(dataCategory);
        }
        catch (UnknownDataCatogory unknownDataCatogory) {

            return create_NOT_FOUND_response(
                    "{message: 'Unknown data category: " + dataCategoryId + "'" + "}");
        }
    }

    @RequestMapping(
            value="/api/dataCategory",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity getDataCategoryByName(
            @RequestParam(value = "dataCategoryName", required = true) String dataCategoryName) {

        try {
            ValueScrubbingHelper valueScrubbingHelper = getValueScrubbingHelper();
            dataCategoryName = valueScrubbingHelper.scrubValue(dataCategoryName);

            String dataCategory = getDataCategoryBO().getDataCategoryWithName(dataCategoryName);
            return create_SUCCESS_response(dataCategory);
        }
        catch (UnknownDataCatogory e) {
            return create_NOT_FOUND_response(
                    "{message: 'Unknown data category: " + dataCategoryName + "'" + "}");
        }
    }

    @RequestMapping(
            value="/api/dataCategory/searchableColumnNames",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity getSearchableColumnNames(
            @RequestParam(value = "dataCategoryName", required = true) String dataCategoryName) {

        try {
            ValueScrubbingHelper valueScrubbingHelper = getValueScrubbingHelper();
            dataCategoryName = valueScrubbingHelper.scrubValue(dataCategoryName);

            String columnNamesForDataCategoryName = getDataCategoryBO().getSearchableColumnNamesForDataCategoryName(dataCategoryName);
            return create_SUCCESS_response(columnNamesForDataCategoryName);

        } catch (UnknownDataCatogory e) {
            return create_NOT_FOUND_response(
                    "{message: 'Unknown data category: " + dataCategoryName + "'" + "}");
        }
    }

    @RequestMapping(
            value="/api/dataCategory/names/all",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity getAllDataCategoryNames() {

        String dataCategoryNames = getDataCategoryBO().getAllDataCategoryNames();
        return create_SUCCESS_response(dataCategoryNames);
    }

    @RequestMapping(
            value="/api/dataCategories/all",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity getDataCategoryByName() {

        String dataCategory = getDataCategoryBO().getAllDataCategories();
        return create_SUCCESS_response(dataCategory);
    }

    @RequestMapping(
            value="/api/addDataCategory",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity addDataCategory(
            @RequestParam(value = "name") String dataCategoryName) {

        try {
            getDataCategoryBO().addDataCategory(dataCategoryName);
            String dataCategory = getDataCategoryBO().getDataCategoryWithName(dataCategoryName);
            return create_SUCCESS_response(dataCategory);
        }
        catch (UnknownDataCatogory unknownDataCatogory) {
            return create_INTERNAL_SERVER_ERROR_response(
                    "{message: 'Added category, but then couldn't retreive it, so something must be amiss.'}");
        }
        catch (DataCategoryAlreadyExists dataCategoryAlreadyExists) {
            return create_SUCCESS_response(
                    "{message: 'Data category already exists'}");
        }
    }

    protected IDataCategoryBO getDataCategoryBO() {

        return dataRepositoryApplication.getBusinessObjects().getDataCategoryBO();
    }

    protected ValueScrubbingHelper getValueScrubbingHelper() {

        return dataRepositoryApplication.getValueScrubbingHelper();
    }
}
