package gov.energy.nbc.car.restService;

import gov.energy.nbc.car.businessObject.BusinessObjects;
import gov.energy.nbc.car.businessObject.TestMode;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static gov.energy.nbc.car.utilities.HTTPResponseUtility.*;


@RestController
public class Endpoints_SampleTypes {

    protected Logger log = Logger.getLogger(getClass());
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("mm/dd/yyyy");

    public Endpoints_SampleTypes() {

    }

    @RequestMapping(value="/api/sampleType/{sampleTypeId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSampleType(
            @PathVariable(value = "sampleTypeId") String sampleTypeId,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String sampleType = BusinessObjects.sampleTypeBO.getSampleType(
                TestMode.value(testMode),
                sampleTypeId);

        if (sampleType == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(sampleType);
    }

    @RequestMapping(value="/api/sampleType/name/{sampleName}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSampleTypeByName(
            @PathVariable(value = "sampleName") String sampleName,
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String sampleType = BusinessObjects.sampleTypeBO.getSampleTypeWithName(
                TestMode.value(testMode),
                sampleName);

        if (sampleType == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(sampleType);
    }

    @RequestMapping(value="/api/sampleTypes/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSampleTypeByName(
            @RequestParam(value = "inTestMode", required = false) String testMode) {

        String sampleType = BusinessObjects.sampleTypeBO.getAllSampleTypes(
                TestMode.value(testMode));

        if (sampleType == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(sampleType);
    }
}
