package gov.energy.nbc.car.fileReader;

import au.com.bytecode.opencsv.CSVReader;
import gov.energy.nbc.car.model.common.SpreadsheetRow;
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

        List<String> columnNames = determineColumnNames(lines);
        int numberOfColumnNames = columnNames.size();

        List<List> data = extractData(lines, numberOfColumnNames);

        // DESIGN NOTE: When the data was extracted, the the row number was added as the first data element so users
        //              will be able to trace the data back to the original source document.  So we need to add a
        //              name for that column.
        columnNames.add(0, SpreadsheetRow.ATTRIBUTE_KEY__ROW_NUMBER);


        SpreadsheetData spreasheetData = new SpreadsheetData(columnNames, data);
        return spreasheetData;
    }

    protected static List<String> determineColumnNames(List<List> lines)
            throws NonStringValueFoundInHeader {

        List<Object> firstRow = lines.get(0);

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

    protected static List<List> extractData(List<List> lines, int numberOfColumnNames) {

        // DESIGN NOTE: We skill the first line because it contains the column names
        List<List> dataRows = lines.subList(1, lines.size());

        List<List> data = new ArrayList();

        Integer lineNumber = 2;
        for (List dataRow : dataRows) {

            List lineData = dataRow.subList(0, numberOfColumnNames);

            // DESIGN NOTE: We are added the line number number so users will be able to trace the data back to the
            //              original source document.
            lineData.add(0, lineNumber);

            data.add(lineData);
            lineNumber++;
        }

        return data;
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
