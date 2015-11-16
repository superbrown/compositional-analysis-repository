package gov.energy.nbc.car.bo;

import gov.energy.nbc.car.dao.IRowDAO;
import gov.energy.nbc.car.dao.dto.SearchCriterion;

import java.util.List;


public interface IRowBO {

    String getRow(TestMode testMode, String rowId);

    String getRows(TestMode testMode, String query);

//    String getRows(TestMode testMode, RowSearchCriteria rowSearchCriteria, String projection);

//    String getRows(TestMode testMode, Bson query, Bson projection);

    String getRows(TestMode testMode, List<SearchCriterion> rowSearchCriteria);

    String getAllRows(TestMode testMode);

    String getRowAssociatedWithDataset(TestMode testMode, String datasetId);

    IRowDAO getRowDAO(TestMode testMode);
}
