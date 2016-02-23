package gov.energy.nrel.dataRepositoryApp.bo;

import gov.energy.nrel.dataRepositoryApp.bo.exception.DataCategoryAlreadyExists;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataCatogory;
import gov.energy.nrel.dataRepositoryApp.dao.IDataCategoryDAO;

public interface IDataCategoryBO extends IBO {

    String getDataCategory(String dataCategoryId) throws UnknownDataCatogory;

    String getDataCategoryWithName(String name) throws UnknownDataCatogory;

    String getAllDataCategories();

    String getAllDataCategoryNames();

    String getSearchableColumnNamesForDataCategoryName(String dataCategoryName) throws UnknownDataCatogory;

    void addDataCategory(String categoryName) throws DataCategoryAlreadyExists;

    void assureCategoriesAreInTheDatabase(String[] dataCategoryNames);

    IDataCategoryDAO getDataCategoryDAO();
}
