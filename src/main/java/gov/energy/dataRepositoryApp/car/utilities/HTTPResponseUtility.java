package gov.energy.nbc.car.utilities;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HTTPResponseUtility {

    public static ResponseEntity create_SUCCESS_response(String response) {
        return new ResponseEntity(response, HttpStatus.OK);
    }

    public static ResponseEntity create_BAD_REQUEST_missingRequiredParam_response(String body) {
        return create_BAD_REQUEST_response("Missing parameter: " + body);
    }

    public static ResponseEntity create_BAD_REQUEST_response(String body) {
        return new ResponseEntity(body, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity create_NOT_FOUND_response() {
        // FIXME: This seems screwy because it may look to callers like they reached an invalid UIR.
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity create_INTERNAL_SERVER_ERROR_response() {
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
