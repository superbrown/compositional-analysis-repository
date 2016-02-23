package gov.energy.nrel.dataRepositoryApp.bo;


import gov.energy.nrel.dataRepositoryApp.restEndpoint.DataType;

public interface IDataTypeBO extends IBO {

    String getInventoryOfComparisonOperators(DataType dataType);

    String getInventoryOfDataTypes();
}
