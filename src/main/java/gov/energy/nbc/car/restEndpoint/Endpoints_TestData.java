package gov.energy.nbc.car.restEndpoint;

import gov.energy.nbc.car.Application;
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
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("mm/dd/yyyy");

    public Endpoints_TestData() {

    }

    @RequestMapping(value="/api/seedTestData", method = RequestMethod.GET)
    public ResponseEntity seedTestData() {

        return create_SUCCESS_response(Application.getBusinessObjects().getTestDataBO().seedTestDataInTheDatabase_dataset_1_and_2());
    }

    @RequestMapping(value="/api/removeTestData", method = RequestMethod.GET)
    public ResponseEntity removeTestData() {

        Application.getBusinessObjects().getTestDataBO().removeTestData();
        return create_SUCCESS_response("{ message: \"test data successfully removed\" }");
    }

    @RequestMapping(value="/api/dropTestDatabase", method = RequestMethod.GET)
    public ResponseEntity dropTheTestDatabase() {

        Application.getBusinessObjects().getTestDataBO().dropTheTestDatabase();
        return create_SUCCESS_response("{ message: \"test database successfully dropped\" }");
    }
}
