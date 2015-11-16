package gov.energy.nbc.car.bo;

import gov.energy.nbc.car.bo.exception.DeletionFailure;
import gov.energy.nbc.car.dao.IDataCategoryDAO;

public interface IDataCategoryBO {

    String getDataCategory(TestMode testMode,
                           String dataCategoryId);

    String getDataCategoryWithName(TestMode testMode,
                                   String name);

    String getAllDataCategories(TestMode testMode);

    String getAllDataCategoryNames(TestMode testMode);

    void deleteDataCategory(TestMode testMode,
                            String dataCategoryId) throws DeletionFailure;

    String addDataCategory(TestMode testMode,
                           String jsonIn);

    String getColumnNamesForDataCategoryName(TestMode testMode,
                                             String dataCategoryName);

    IDataCategoryDAO getDataCategoryDAO(TestMode testMode);
}
