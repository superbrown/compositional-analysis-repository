package gov.energy.nbc.car.model.common;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

import java.util.ArrayList;
import java.util.List;

public class Data extends ArrayList<SpreadsheetRow> {

    public Data(List<List> spreadsheetContent) {

        super();

        List<Object> columnNames = spreadsheetContent.get(0);
        List<List> spreadsheetData = spreadsheetContent.subList(1, spreadsheetContent.size());

        init(columnNames, spreadsheetData);
    }

    public Data(List<Object> columnNames, List<List> spreadsheetData) {

        super();

        init(columnNames, spreadsheetData);
    }

    private void init(List columnNames, List<List> spreadsheetData) {

        for (int rowIndex = 0; rowIndex < spreadsheetData.size(); rowIndex++) {

            List<Object> rowValues = spreadsheetData.get(rowIndex);
            SpreadsheetRow spreadsheetRow = new SpreadsheetRow(columnNames, rowIndex, rowValues);
            add(spreadsheetRow);
        }
    }

    public Data(String json) {

        init(json);
    }

    private void init(String json) {

        BasicDBList parseredJson = (BasicDBList) JSON.parse(json);

        for (Object o : parseredJson) {

            BasicDBObject basicDBObject = (BasicDBObject)o;
            SpreadsheetRow spreadsheetRow = new SpreadsheetRow(basicDBObject);
            add(spreadsheetRow);
        }
    }
}
