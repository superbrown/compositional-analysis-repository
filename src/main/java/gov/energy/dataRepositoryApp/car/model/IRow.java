package gov.energy.nbc.car.model;

import java.util.Set;


public interface IRow extends IThingWithAnId {

    Set<String> getColumnNames();

    Object getValue(String columnName);
}
