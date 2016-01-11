package gov.energy.nrel.dataRepositoryApp.restEndpoint.mongodb;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.ITestDataBO;
//import gov.energy.nrel.dataRepositoryApp.app.TestMode;
import gov.energy.nrel.dataRepositoryApp.dao.exception.CompletelyFailedToPersistDataset;
import gov.energy.nrel.dataRepositoryApp.dao.exception.PartiallyFailedToPersistDataset;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static gov.energy.nrel.dataRepositoryApp.utilities.HTTPResponseUtility.create_SUCCESS_response;


@RestController
public class Endpoints_TestData {

    protected static Logger log = Logger.getLogger(Endpoints_TestData.class);

    @Autowired
    protected DataRepositoryApplication dataRepositoryApplication;

    @RequestMapping(value="/api/seedTestData", method = RequestMethod.GET)
    public ResponseEntity seedTestData()
            throws PartiallyFailedToPersistDataset, CompletelyFailedToPersistDataset {

        return create_SUCCESS_response(getTestDataBO().seedTestDataInTheDatabase_dataset_1_and_2());
    }

    @RequestMapping(value="/api/seedBigAmountsOfData", method = RequestMethod.GET)
    public ResponseEntity seedBigAmountsOfData()
            throws PartiallyFailedToPersistDataset, CompletelyFailedToPersistDataset {

        return create_SUCCESS_response(getTestDataBO().seedTestDataInTheDatabase_dataset_1_and_2());
    }

    @RequestMapping(value="/api/removeTestData", method = RequestMethod.GET)
    public ResponseEntity removeTestData() {

        getTestDataBO().removeTestData();
        return create_SUCCESS_response("{ message: \"test data successfully removed\" }");
    }

    @RequestMapping(value="/api/dropTestDatabase", method = RequestMethod.GET)
    public ResponseEntity dropTheTestDatabase() {

        getTestDataBO().dropTheTestDatabase();
        return create_SUCCESS_response("{ message: \"test database successfully dropped\" }");
    }


    private ITestDataBO getTestDataBO() {

        return dataRepositoryApplication.getBusinessObjects().getTestDataBO();
    }
}
