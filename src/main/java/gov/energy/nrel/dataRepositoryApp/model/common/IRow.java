package gov.energy.nrel.dataRepositoryApp.model.common;

import gov.energy.nrel.dataRepositoryApp.model.document.IThingWithAnId;

import java.util.Set;


public interface IRow extends IThingWithAnId {

    Set<String> getColumnNames();

    Object getValue(String columnName);
}
