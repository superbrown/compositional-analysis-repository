package gov.energy.nrel.dataRepositoryApp.restEndpoint;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IDataTypeBO;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknowDataType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static gov.energy.nrel.dataRepositoryApp.utilities.HTTPResponseUtility.create_BAD_REQUEST_missingRequiredParam_response;
import static gov.energy.nrel.dataRepositoryApp.utilities.HTTPResponseUtility.create_SUCCESS_response;


@RestController
public class Endpoints_DataTypes {

    protected Logger log = Logger.getLogger(getClass());

    @Autowired
    protected DataRepositoryApplication dataRepositoryApplication;


    @RequestMapping(value="/api/dataTypes/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getAllComparisonOperators() {

        String inventoryOfDataTypes = getDataTypeBO().getInventoryOfDataTypes();;
        return create_SUCCESS_response(inventoryOfDataTypes);
    }

    @RequestMapping(value="/api/dataType/comparisonOperators", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getComparisonOperators(
            @RequestParam(value = "dataType", required = true) String dataType) {

        try {
            String rows = getDataTypeBO().getInventoryOfComparisonOperators(
                    DataType.valueOf(dataType));
            return create_SUCCESS_response(rows);
        }
        catch (UnknowDataType e) {
            return create_BAD_REQUEST_missingRequiredParam_response(
                    "{message: 'Unknown data type: " + dataType + "'" + "}");
        }
    }

    protected IDataTypeBO getDataTypeBO() {

        return dataRepositoryApplication.getBusinessObjects().getDataTypeBO();
    }
}
