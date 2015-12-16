package gov.energy.nbc.car.bo;

import gov.energy.nbc.car.ResultsMode;
import gov.energy.nbc.car.dao.IRowDAO;
import gov.energy.nbc.car.dao.dto.SearchCriterion;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.List;


public interface IRowBO {

    String getRow(String rowId);

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
