package gov.energy.nbc.car.bo;

import gov.energy.nbc.car.bo.exception.DeletionFailure;
import gov.energy.nbc.car.dao.IDataCategoryDAO;

public interface IDataCategoryBO {

    String getDataCategory(String dataCategoryId);

    String getDataCategoryWithName(String name);

    String getAllDataCategories();

    String getAllDataCategoryNames();

    void deleteDataCategory(String dataCategoryId) throws DeletionFailure;

    String addDataCategory(String jsonIn);

    String getColumnNamesForDataCategoryName(String dataCategoryName);

    IDataCategoryDAO getDataCategoryDAO();
}
