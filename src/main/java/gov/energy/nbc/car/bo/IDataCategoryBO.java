package gov.energy.nbc.car.bo;

import gov.energy.nbc.car.bo.exception.DeletionFailure;
import gov.energy.nbc.car.dao.IDataCategoryDAO;
import gov.energy.nbc.car.model.IDataCategoryDocument;

public interface IDataCategoryBO {

    String getDataCategory(TestMode testMode,
                           String dataCategoryId);

    String getDataCategoryWithName(TestMode testMode,
                                   String name);

    String getAllDataCategories(TestMode testMode);

    long deleteDataCategory(TestMode testMode,
                            String dataCategoryId) throws DeletionFailure;

    String addDataCategory(TestMode testMode,
                           String jsonIn);

    IDataCategoryDocument getDataCategoryDocument(TestMode testMode,
                                                  String dataCategoryId);

    IDataCategoryDAO getDataCategoryDAO(TestMode testMode);
}
