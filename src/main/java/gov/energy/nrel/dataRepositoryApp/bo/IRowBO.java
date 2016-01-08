package gov.energy.nrel.dataRepositoryApp.bo;

import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownRow;
import gov.energy.nrel.dataRepositoryApp.dao.IRowDAO;
import gov.energy.nrel.dataRepositoryApp.dao.dto.SearchCriterion;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;


public interface IRowBO extends IBO {

    String getRow(String rowId) throws UnknownRow;

    String getRows(String query, ResultsMode returnAllColumns);

//    String getRows(RowSearchCriteria rowSearchCriteria, String projection);

//    String getRows(Bson query, Bson projection);

    String getRows(List<SearchCriterion> rowSearchCriteria, ResultsMode resultsMode);

    String getAllRows();

    String getRowsAssociatedWithDataset(String datasetId);

    IRowDAO getRowDAO();

    String getRowsFlat(String rowId);

    XSSFWorkbook getRowsAsExcelWorkbook(String query);
}
