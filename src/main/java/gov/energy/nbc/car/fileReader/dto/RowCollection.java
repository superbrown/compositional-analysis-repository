package gov.energy.nbc.car.fileReader.dto;

import java.util.List;

public class RowCollection {

    public final List<String> columnNames;
    public final List<List> rowData;

    public RowCollection(List<String> columnNames, List<List> rowData) {

        this.columnNames = columnNames;
        this.rowData = rowData;
    }
}
