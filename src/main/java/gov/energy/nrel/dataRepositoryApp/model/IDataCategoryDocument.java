package gov.energy.nbc.car.model;

import java.util.Set;


public interface IDataCategoryDocument extends IThingWithAnId {

    void setName(String dataCategory);

    String getName();

    void setColumnNames(Set<String> columnNames);

    Set<String> getColumnNames();
}
