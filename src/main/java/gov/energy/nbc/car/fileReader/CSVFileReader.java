package gov.energy.nbc.car.fileReader;

import au.com.bytecode.opencsv.CSVReader;
import gov.energy.nbc.car.utilities.SpreadsheetData;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CSVFileReader {

    protected Logger log = Logger.getLogger(this.getClass());


    public static SpreadsheetData extractDataFromFile(File file)
            throws IOException, NonStringValueFoundInHeader, UnsupportedFileExtension {

        List<List> lines = parse(file);

        List<Object> firstRow = lines.get(0);

        List<String> columnNames = extractColumnNames(firstRow);
        int numberOfColumnNames = columnNames.size();

        List<List> dataRows = lines.subList(1, lines.size());
        List<List> dataRowsTruncatedDownToTheNumberOfColumnNames =
                toDataRowsTruncatedDownToTheNumberOfColumnNames(dataRows, numberOfColumnNames);

        SpreadsheetData spreasheetData = new SpreadsheetData(columnNames, dataRowsTruncatedDownToTheNumberOfColumnNames);
        return spreasheetData;
    }

    protected static List<String> extractColumnNames(List<Object> firstRow)
            throws NonStringValueFoundInHeader {

        List<String> columnNames = new ArrayList();

        int columnNumber = 1;

        for (Object o : firstRow) {

            if (o == null) {
                break;
            }

            if ((o instanceof String) == false) {

                throw new NonStringValueFoundInHeader(columnNumber, o);
            }

            String value = (String)o;

            if (StringUtils.isBlank(value)) {
                break;
            }

            columnNames.add(value);

            columnNumber++;
        }
        return columnNames;
    }

    protected static List<List> toDataRowsTruncatedDownToTheNumberOfColumnNames(List<List> dataRows, int numberOfColumnNames) {

        List<List> dataRowsTruncatedDownToTheNumberOfColumnNames = new ArrayList();

        for (List dataRow : dataRows) {
            List<List> dataRowTruncatedDownToTheNumberOfColumnNames = dataRow.subList(0, numberOfColumnNames);
            dataRowsTruncatedDownToTheNumberOfColumnNames.add(dataRowTruncatedDownToTheNumberOfColumnNames);
        }
        return dataRowsTruncatedDownToTheNumberOfColumnNames;
    }

    private static List<List> parse(File file) {

        List<List> lines = new ArrayList();

        CSVReader reader = null;
        try {
            //Get the CSVReader instance with specifying the delimiter to be used
            reader = new CSVReader(new FileReader(file), ',');
            String[] nextLine;

            //Read one line at a time
            while ((nextLine = reader.readNext()) != null) {

                List<Object> line = new ArrayList();

                for (String value : nextLine) {

                    Object appropriateDataType = toAppropriateDataType(value);
                    line.add(appropriateDataType);
                }

                lines.add(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                reader.close();
            } catch (IOException e) { }
        }

        return lines;
    }


    protected static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");

    private static Object toAppropriateDataType(String value) {

        if (StringUtils.isBlank(value)) {
            return null;
        }

        try {
            return new Double(value);
        }
        catch (NumberFormatException e) {
        }

        try {
            return simpleDateFormat.parse(value);
        }
        catch (ParseException e) {
        }

        return value;
    }
}
