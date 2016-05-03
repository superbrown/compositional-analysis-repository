package gov.energy.nrel.dataRepositoryApp.restEndpoint;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IDataTypeBO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static gov.energy.nrel.dataRepositoryApp.utilities.HTTPResponseUtility.create_SUCCESS_response;


@RestController
public class Endpoints_DataTypes extends EndpointController {

    protected static Logger log = Logger.getLogger(Endpoints_DataTypes.class);

    @Autowired
    protected DataRepositoryApplication dataRepositoryApplication;


    @RequestMapping(
            value="/api/v02/dataTypes/all",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity getAllComparisonOperators() throws CleanupOperationIsOccurring {

        throwExceptionIfCleanupOperationsIsOccurring();

        String inventoryOfDataTypes = getDataTypeBO().getInventoryOfDataTypes();;
        return create_SUCCESS_response(inventoryOfDataTypes);
    }

    @RequestMapping(
            value="/api/v02/dataType/comparisonOperators",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity getComparisonOperators(
            @RequestParam(value = "dataType", required = true) String dataType) throws CleanupOperationIsOccurring {

        throwExceptionIfCleanupOperationsIsOccurring();

        String rows = getDataTypeBO().getInventoryOfComparisonOperators(
                DataType.valueOf(dataType));
        return create_SUCCESS_response(rows);
    }

    protected IDataTypeBO getDataTypeBO() {

        return dataRepositoryApplication.getBusinessObjects().getDataTypeBO();
    }
}
