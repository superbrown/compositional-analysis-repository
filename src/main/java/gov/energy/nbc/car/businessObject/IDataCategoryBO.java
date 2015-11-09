package gov.energy.nbc.car.businessObject;

import gov.energy.nbc.car.dao.mongodb.DataCategoryDAO;
import gov.energy.nbc.car.model.document.DataCategoryDocument;

/**
 * Created by mbrown on 11/8/2015.
 */
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

    DataCategoryDocument getDataCategoryDocument(TestMode testMode,
                                                 String dataCategoryId);

    DataCategoryDAO getDataCategoryDAO(TestMode testMode);
}
