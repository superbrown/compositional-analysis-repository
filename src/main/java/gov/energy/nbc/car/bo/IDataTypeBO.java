package gov.energy.nbc.car.bo;


import gov.energy.nbc.car.restEndpoint.DataType;

public interface IDataTypeBO {

    String getInventoryOfComparisonOperators(DataType dataType);

    String getInventoryOfDataTypes();
}
