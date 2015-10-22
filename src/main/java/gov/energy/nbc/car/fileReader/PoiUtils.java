package gov.energy.nbc.car.fileReader;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;

import java.util.Date;

public class PoiUtils {

    protected static Logger log = Logger.getLogger(PoiUtils.class);

    public static Object toAppropriateDataType(Cell cell) {

        switch (cell.getCellType()) {

            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();

            case Cell.CELL_TYPE_BLANK:
                return null;

            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();

            case Cell.CELL_TYPE_ERROR:
                return cell.getErrorCellValue();

            case Cell.CELL_TYPE_FORMULA:
                return null;

            case Cell.CELL_TYPE_NUMERIC:

                // Date
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    return date;
                }
                else {
                    return cell.getNumericCellValue();
                }
        }

        log.error("This shouldn't happen because we should have covered all possible cases.");
        return null;
    }
}
