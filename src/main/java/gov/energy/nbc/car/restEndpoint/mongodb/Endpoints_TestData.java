package gov.energy.nbc.car.restEndpoint.mongodb;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.bo.ITestDataBO;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static gov.energy.nbc.car.utilities.HTTPResponseUtility.create_SUCCESS_response;


@RestController
public class Endpoints_TestData {

    protected Logger log = Logger.getLogger(getClass());

    private final ITestDataBO testDataBO;
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("mm/dd/yyyy");

    public Endpoints_TestData() {

        testDataBO = Application.getBusinessObjects().getTestDataBO();
    }

    @RequestMapping(value="/api/seedTestData", method = RequestMethod.GET)
    public ResponseEntity seedTestData() {

        return create_SUCCESS_response(testDataBO.seedTestDataInTheDatabase_dataset_1_and_2());
    }

    @RequestMapping(value="/api/removeTestData", method = RequestMethod.GET)
    public ResponseEntity removeTestData() {

        testDataBO.removeTestData();
        return create_SUCCESS_response("{ message: \"test data successfully removed\" }");
    }

    @RequestMapping(value="/api/dropTestDatabase", method = RequestMethod.GET)
    public ResponseEntity dropTheTestDatabase() {

        testDataBO.dropTheTestDatabase();
        return create_SUCCESS_response("{ message: \"test database successfully dropped\" }");
    }
}
