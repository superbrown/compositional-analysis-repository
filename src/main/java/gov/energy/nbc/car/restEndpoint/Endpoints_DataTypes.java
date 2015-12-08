package gov.energy.nbc.car.restEndpoint;

import gov.energy.nbc.car.app.DataRepositoryApplication;
import gov.energy.nbc.car.bo.IDataTypeBO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static gov.energy.nbc.car.utilities.HTTPResponseUtility.create_NOT_FOUND_response;
import static gov.energy.nbc.car.utilities.HTTPResponseUtility.create_SUCCESS_response;


@RestController
public class Endpoints_DataTypes {

    protected Logger log = Logger.getLogger(getClass());

    @Autowired
    protected DataRepositoryApplication dataRepositoryApplication;


    @RequestMapping(value="/api/dataTypes/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getAllComparisonOperators() {

        String inventoryOfDataTypes = getDataTypeBO().getInventoryOfDataTypes();;

        if (inventoryOfDataTypes == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(inventoryOfDataTypes);
    }

    @RequestMapping(value="/api/dataType/comparisonOperators", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getComparisonOperators(
            @RequestParam(value = "dataType", required = true) String dataType) {

        String rows = getDataTypeBO().getInventoryOfComparisonOperators(DataType.valueOf(dataType));;

        if (rows == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(rows);
    }

    protected IDataTypeBO getDataTypeBO() {

        return dataRepositoryApplication.getBusinessObjects().getDataTypeBO();
    }
}
