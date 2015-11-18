package gov.energy.nbc.car.restEndpoint.mongodb;

import gov.energy.nbc.car.app.AppSingleton;
import gov.energy.nbc.car.bo.ITestDataBO;
import gov.energy.nbc.car.app.TestMode;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static gov.energy.nbc.car.utilities.HTTPResponseUtility.create_SUCCESS_response;


@RestController
public class Endpoints_TestData {

    protected Logger log = Logger.getLogger(getClass());

    @Autowired
    protected AppSingleton appSingleton;

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

        return appSingleton.getBusinessObjects(TestMode.TEST_MODE).getTestDataBO();
    }
}
