package gov.energy.nrel.dataRepositoryApp.bo;

import gov.energy.nrel.dataRepositoryApp.bo.exception.DeletionFailure;
import gov.energy.nrel.dataRepositoryApp.dao.IDataCategoryDAO;

public interface IDataCategoryBO {

    String getDataCategory(String dataCategoryId);

    String getDataCategoryWithName(String name);

    String getAllDataCategories();

    String getAllDataCategoryNames();

    void deleteDataCategory(String dataCategoryId) throws DeletionFailure;

    String getColumnNamesForDataCategoryName(String dataCategoryName);

    void addDataCategory(String categoryName);

    IDataCategoryDAO getDataCategoryDAO();
}
