package gov.energy.nbc.car.businessObject;

import gov.energy.nbc.car.businessObject.dto.RowSearchCriteria;
import gov.energy.nbc.car.dao.mongodb.IRowDAO;
import org.bson.conversions.Bson;


public interface IRowBO {

    String getRow(TestMode testMode, String rowId);

    String getRows(TestMode testMode, String query, String projection);

    String getRows(TestMode testMode, RowSearchCriteria rowSearchCriteria, String projection);

    String getRows(TestMode testMode, Bson query, Bson projection);

    String getRows(TestMode testMode, RowSearchCriteria rowSearchCriteria);

    String getAllRows(TestMode testMode);

    String getRowAssociatedWithDataset(TestMode testMode, String datasetId);

    IRowDAO getRowDAO(TestMode testMode);
}
