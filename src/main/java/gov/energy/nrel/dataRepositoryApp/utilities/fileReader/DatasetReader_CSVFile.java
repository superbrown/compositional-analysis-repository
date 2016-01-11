package gov.energy.nrel.dataRepositoryApp.utilities.fileReader;

import au.com.bytecode.opencsv.CSVReader;
import gov.energy.nrel.dataRepositoryApp.model.common.mongodb.Row;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.dto.RowCollection;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.FileContainsInvalidColumnName;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DatasetReader_CSVFile extends AbsDatasetReader implements IDatasetReader, IDatasetReader_CSVFile {

    protected static Logger log = Logger.getLogger(DatasetReader_CSVFile.class);
    
    @Override
    public boolean canReadFile(File file) {

        return canReadFileWithExtension(file.getName());
    }

    @Override
    public boolean canReadFileWithExtension(String fileName) {

        return fileName.toLowerCase().endsWith(".csv") == true;
    }


    @Override
    public RowCollection extractDataFromFile(File file, int maxNumberOfValuesPerRow)
            throws IOException, FileContainsInvalidColumnName, UnsupportedFileExtension {

        List<List> lines = parse(file, maxNumberOfValuesPerRow);

        List<String> columnNames = determineColumnNames(lines);
        int numberOfColumnNames = columnNames.size();

        List<List> data = extractData(lines, numberOfColumnNames);

        // DESIGN NOTE: When the data was extracted, the the row number was added as the first data element so users
        //              will be able to trace the data back to the original source document.  So we need to add a
        //              name for that column.
        columnNames.add(0, Row.MONGO_KEY__ROW_NUMBER);


        RowCollection rowCollection = new RowCollection(columnNames, data);
        return rowCollection;
    }

    protected List<String> determineColumnNames(List<List> lines)
            throws FileContainsInvalidColumnName {

        List<Object> firstRow = lines.get(0);

        List<String> columnNames = new ArrayList();

        int columnNumber = 1;

        for (Object columnName : firstRow) {

            if (columnName == null) {
                break;
            }

            if (((columnName instanceof String) == false) &&
                ((columnName instanceof Number) == false)) {

                throw new FileContainsInvalidColumnName(columnNumber, columnName);
            }

            if ((columnName instanceof String) &&
                StringUtils.isBlank((String)columnName)) {
                // we interpret this as being an indication that we've passed the final column in the sequence
                break;
            }

            columnNames.add(columnName.toString());

            columnNumber++;
        }
        return columnNames;
    }

    protected List<List> extractData(List<List> lines, int numberOfColumnNames) {

        // DESIGN NOTE: We skip the first line because it contains the column names
        List<List> dataRows = lines.subList(1, lines.size());

        List<List> data = new ArrayList();

        Integer lineNumber = 2;
        for (List dataRow : dataRows) {

            List lineData = dataRow.subList(0, numberOfColumnNames);
            List allDataInRowExceptTheFirstColumnThatContainsTheRowNumber = new ArrayList(lineData);

            // DESIGN NOTE: We are adding the line number number so users will be able to trace the data back to the
            //              original source document.
            lineData.add(0, lineNumber);

            if (containsData(allDataInRowExceptTheFirstColumnThatContainsTheRowNumber)) {
                data.add(lineData);
            }

            lineNumber++;
        }

        return data;
    }

    private List<List> parse(File file, int maxNumberOfValuesPerRow) {

        List<List> lines = new ArrayList();

        CSVReader reader = null;
        try {
            //Get the CSVReader instance with specifying the delimiter to be used
            reader = new CSVReader(new java.io.FileReader(file), ',');
            String[] values;

            //Read one line at a time
            while ((values = reader.readNext()) != null) {

                List<Object> line = new ArrayList();

                int i = 1;
                for (String value : values) {

                    if ((maxNumberOfValuesPerRow != -1) &&
                        (i > maxNumberOfValuesPerRow)) {
                        break;
                    }

                    Object appropriateDataType = toAppropriateDataType(value);
                    line.add(appropriateDataType);
                    i++;
                }

                lines.add(line);
            }
        }
        catch (Exception e) {
            log.error(e, e);
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } 
            catch (IOException e) { }
        }

        return lines;
    }


    protected static SimpleDateFormat SIMPLE_DATE_FORMAT_WITH_DASHES = new SimpleDateFormat("M-d-yyyy");
    protected static SimpleDateFormat SIMPLE_DATE_FORMAT_WITH_SlASHES = new SimpleDateFormat("M/d/yyyy");

    protected Object toAppropriateDataType(String value) {

        if (StringUtils.isBlank(value)) {
            return null;
        }

        try {
            return new Double(value);
        }
        catch (NumberFormatException e) {
        }

        if (value.toLowerCase().equals("true")) {
            return Boolean.TRUE;
        }

        if (value.toLowerCase().equals("false")) {
            return Boolean.FALSE;
        }

        try {
            return SIMPLE_DATE_FORMAT_WITH_DASHES.parse(value);
        }
        catch (ParseException e) {
        }

        try {
            return SIMPLE_DATE_FORMAT_WITH_SlASHES.parse(value);
        }
        catch (ParseException e) {
        }

        return value;
    }
}
