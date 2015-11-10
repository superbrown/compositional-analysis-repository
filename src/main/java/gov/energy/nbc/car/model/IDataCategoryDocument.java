package gov.energy.nbc.car.model;

import java.util.Set;


public interface IDataCategoryDocument extends IThingWithAnId {

    void setDataCategory(String dataCategory);

    String getDataCategory();

    void setColumnNames(Set<String> columnNames);

    Set<String> getColumnNames();
}
