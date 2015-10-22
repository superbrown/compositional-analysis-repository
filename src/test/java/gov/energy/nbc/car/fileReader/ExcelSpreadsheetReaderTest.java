package gov.energy.nbc.car.fileReader;

import gov.energy.nbc.car.model.common.SpreadsheetRow;
import gov.energy.nbc.car.utilities.SpreadsheetData;
import gov.energy.nbc.car.utilities.Utilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.fail;


public class ExcelSpreadsheetReaderTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: extractDataFromSpreadsheet(File file, String nameOfWorksheetContainingTheData)
     */
    @Test
    public void testExtractDataFromSpreadsheet_xls() throws Exception {

        try {
            File file = Utilities.getFile("/SpreadsheetForReadingTest.xls");
            testExtractDataFromSpreadsheet(file);
        }
        catch (NonStringValueFoundInHeader nonStringValueFoundInHeader) {
            fail();
        }
    }

    @Test
    public void testExtractDataFromSpreadsheet_xlm() throws Exception {

        try {
            File file = Utilities.getFile("/SpreadsheetForReadingTest.xlsm");
            testExtractDataFromSpreadsheet(file);
        }
        catch (NonStringValueFoundInHeader nonStringValueFoundInHeader) {
            fail();
        }
    }

    @Test
    public void testExtractDataFromSpreadsheet_xlsx() throws Exception {

        try {
            File file = Utilities.getFile("/SpreadsheetForReadingTest.xlsx");
            testExtractDataFromSpreadsheet(file);
        }
        catch (NonStringValueFoundInHeader nonStringValueFoundInHeader) {
            fail();
        }
    }

    private void testExtractDataFromSpreadsheet(File file) throws IOException, NonStringValueFoundInHeader, UnsupportedFileExtension, ParseException {

        SpreadsheetData spreadsheetData = ExcelSpreadsheetReader.extractDataFromSpreadsheet(file, "Sheet Containing Data");

        assertTrue(spreadsheetData.columnNames.size() == 7);
        assertTrue(spreadsheetData.columnNames.get(0).equals(SpreadsheetRow.ATTRIBUTE_KEY__ROW_NUMBER));
        assertTrue(spreadsheetData.columnNames.get(1).equals("Column 1"));
        assertTrue(spreadsheetData.columnNames.get(2).equals("Column 2"));
        assertTrue(spreadsheetData.columnNames.get(3).equals("Column 3"));
        assertTrue(spreadsheetData.columnNames.get(4).equals("Column 4"));
        assertTrue(spreadsheetData.columnNames.get(5).equals("Column 5"));
        assertTrue(spreadsheetData.columnNames.get(6).equals("Column 6"));

        assertTrue(spreadsheetData.spreadsheetData.size() == 5);

        List row_1 = spreadsheetData.spreadsheetData.get(0);
        assertTrue(row_1.size() == 7);
        assertTrue(row_1.get(1).equals(1.0));
        assertTrue(row_1.get(2).equals("one"));
        assertTrue(row_1.get(3).equals(toDate("01/01/2015")));
        assertTrue(row_1.get(4) == null);
        assertTrue(row_1.get(5).equals("one"));
        assertTrue(row_1.get(6) == null);

        List row_2 = spreadsheetData.spreadsheetData.get(1);
        assertTrue(row_2.size() == 7);
        assertTrue(row_2.get(1) == null);
        assertTrue(row_2.get(2).equals("two"));
        assertTrue(row_2.get(3).equals(toDate("01/02/2015")));
        assertTrue(row_2.get(4) == null);
        assertTrue(row_2.get(5).equals(2.0));
        assertTrue(row_2.get(6) == null);

        List row_3 = spreadsheetData.spreadsheetData.get(2);
        assertTrue(row_3.size() == 7);
        assertTrue(row_3.get(1).equals(3.0));
        assertTrue(row_3.get(2) == null);
        assertTrue(row_3.get(3).equals(toDate("01/03/2015")));
        assertTrue(row_3.get(4) == null);
        assertTrue(row_3.get(5).equals(3.345));
        assertTrue(row_3.get(6) == null);

        List row_4 = spreadsheetData.spreadsheetData.get(3);
        assertTrue(row_4.size() == 7);
        assertTrue(row_4.get(1).equals(4.0));
        assertTrue(row_4.get(2).equals("four"));
        assertTrue(row_4.get(3) == null);
        assertTrue(row_4.get(4) == null);
        assertTrue(row_4.get(5).equals("four"));
        assertTrue(row_4.get(6).equals("hello 1"));

        List row_5 = spreadsheetData.spreadsheetData.get(4);
        assertTrue(row_5.size() == 7);
        assertTrue(row_5.get(1).equals(5.0));
        assertTrue(row_5.get(2).equals("five"));
        assertTrue(row_5.get(3).equals(toDate("01/05/2015")));
        assertTrue(row_5.get(4) == null);
        assertTrue(row_5.get(5).equals("five"));
        assertTrue(row_5.get(6) == null);
    }

    private Date toDate(String dateAsString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.parse(dateAsString);
    }

}
