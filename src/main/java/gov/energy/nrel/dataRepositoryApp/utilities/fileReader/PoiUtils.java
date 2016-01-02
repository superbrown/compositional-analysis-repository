package gov.energy.nbc.car.utilities.fileReader;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;

public class PoiUtils {

    protected static Logger log = Logger.getLogger(PoiUtils.class);

    public static Object toAppropriateDataType(Cell cell) {

        int cellType = determineCellType(cell);

        switch (cellType) {

            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();

            case Cell.CELL_TYPE_BLANK:
                return null;

            case Cell.CELL_TYPE_BOOLEAN:
                return new Boolean(cell.getBooleanCellValue());

            case Cell.CELL_TYPE_ERROR:
                return null;

            case Cell.CELL_TYPE_FORMULA:
                return null;

            case Cell.CELL_TYPE_NUMERIC:

                // Date
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                else {
                    return new Double(cell.getNumericCellValue());
                }
        }

        log.error("This shouldn't happen because we should have covered all possible cases.");
        return null;
    }

    protected static int determineCellType(Cell cell) {

        int cellType = cell.getCellType();

        if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {

            // For formula cells, Excel stores two things: a "cached" value and the formula itself.  The cached value is
            // the cell's last calculated value.  That's what we want.  We can access that value via POI using the same
            // accessor methods we'd use if it *weren't* a formula cell.  We just need to know its data type so we know
            // what accessor method to call.  We can determine that out by calling getCachedFormulaResultType();

            cellType = cell.getCachedFormulaResultType();
        }

        return cellType;
    }
}
