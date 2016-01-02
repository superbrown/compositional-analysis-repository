package gov.energy.nrel.dataRepositoryApp.model;

import java.util.Set;


public interface IRow extends IThingWithAnId {

    Set<String> getColumnNames();

    Object getValue(String columnName);
}
