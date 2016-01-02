package gov.energy.nrel.dataRepositoryApp.bo;


import gov.energy.nrel.dataRepositoryApp.restEndpoint.DataType;

public interface IDataTypeBO {

    String getInventoryOfComparisonOperators(DataType dataType);

    String getInventoryOfDataTypes();
}
