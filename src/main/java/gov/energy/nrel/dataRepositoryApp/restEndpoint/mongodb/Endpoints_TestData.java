package gov.energy.nrel.dataRepositoryApp.restEndpoint.mongodb;

import gov.energy.nrel.dataRepositoryApp.app.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.ITestDataBO;
//import gov.energy.nrel.dataRepositoryApp.app.TestMode;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static gov.energy.nrel.dataRepositoryApp.utilities.HTTPResponseUtility.create_SUCCESS_response;


@RestController
public class Endpoints_TestData {

    protected Logger log = Logger.getLogger(getClass());

    @Autowired
    protected DataRepositoryApplication dataRepositoryApplication;

    @RequestMapping(value="/api/seedTestData", method = RequestMethod.GET)
    public ResponseEntity seedTestData() {

        return create_SUCCESS_response(getTestDataBO().seedTestDataInTheDatabase_dataset_1_and_2());
    }

    @RequestMapping(value="/api/seedBigAmountsOfData", method = RequestMethod.GET)
    public ResponseEntity seedBigAmountsOfData() {

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
